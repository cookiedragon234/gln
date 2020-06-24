package gln.identifiers

import glm_.bool
import glm_.i
import gln.NUL
import gln.ShaderType
import gln.gl
import gln.program.Defines
import org.lwjgl.opengl.GL20C
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.IntBuffer

inline class GlShader(val name: Int) {

    // --- [ glDeleteShader ] ---

    fun delete() = GL20C.glDeleteShader(name)

    // --- [ glIsShader ] ---

    val valid: Boolean
        get() = GL20C.glIsShader(name)

    // --- [ glCompileShader ] ---

    fun compile() = GL20C.glCompileShader(name)

    // --- [ glShaderSource ] ---

    infix fun source(source: CharSequence) = gl.shaderSource(this, source)

    fun source(vararg sources: CharSequence) = gl.shaderSource(this, *sources)

    // --- [ glGetShaderiv ] ---

    val type: ShaderType
        get() = ShaderType(GL20C.glGetShaderi(name, GL20C.GL_SHADER_TYPE))

    val deleteStatus: Boolean
        get() = GL20C.glGetShaderi(name, GL20C.GL_DELETE_STATUS).bool

    val compileStatus: Boolean
        get() = GL20C.glGetShaderi(name, GL20C.GL_COMPILE_STATUS).bool

    val infoLogLength: Int
        get() = GL20C.glGetShaderi(name, GL20C.GL_INFO_LOG_LENGTH)

    val sourceLength: Int
        get() = GL20C.glGetShaderi(name, GL20C.GL_SHADER_SOURCE_LENGTH)

    // --- [ glGetShaderInfoLog ] ---

    val infoLog: String
        get() = gl.getShaderInfoLog(this)

    // --- [ glGetShaderSource ] ---

    val shaderSource: String
        get() = gl.getShaderSource(this)

    // --- [ glSpecializeShader ] ---

    fun specialize(entryPoint: CharSequence, pConstantIndex: IntBuffer, pConstantValue: IntBuffer) =
            gl.specializeShader(this, entryPoint, pConstantIndex, pConstantValue)

    companion object {

        // --- [ glCreateShader ] ---
        fun create(type: ShaderType) = GlShader(GL20C.glCreateShader(type.i))

        @Throws(Exception::class)
        fun create(type: ShaderType, source: String): GlShader =
                create(type).apply {
                    source(source)
                    compile()
                    require(compileStatus) { "glShader compile status false: $infoLog" }
                }

        fun createFromSource(type: ShaderType, sourceText: Array<String>): Int =
                create(type).apply {
                    source(*sourceText)
                    compile()
                    require(compileStatus) { "glShader compile status false: $infoLog" }
                }.name

        @Throws(Exception::class)
        fun createFromPath(path: String, transform: ((String) -> String)?): GlShader {
            TODO()
        }

        @Throws(Exception::class)
        fun create(path: String, defines: Defines = mutableMapOf()): GlShader {

            val lines = ClassLoader.getSystemResourceAsStream(path)?.use {
                InputStreamReader(it).readLines()
            } ?: throw FileNotFoundException("$path does not exist")

            var source = ""
            lines.forEach {
                source += when {
                    it.startsWith("#include ") -> parseInclude(path.substringBeforeLast('/'), it.substring("#include ".length).trim())
                    else -> it
                } + '\n'
            }

            return create(ShaderType(path), source)
        }

        @Throws(Exception::class)
        fun createFromPath(context: Class<*>, path: String): GlShader {

            val shader = create(path)

            val url = context::class.java.getResource(path)
            val lines = File(url.toURI()).readLines()

            var source = ""
            lines.forEach {
                source += when {
                    it.startsWith("#include ") -> parseInclude(context, path.substringBeforeLast('/'), it.substring("#include ".length).trim())
                    else -> it
                }
                source += '\n'
            }

            shader.source(source)

            shader.compile()

            require(shader.compileStatus) { "Compiler failure in shader '${path.substringAfterLast('/')}'\n${shader.infoLog}" }

            return shader
        }

        private fun parseInclude(context: Class<*>, root: String, shader: String): String {
            if (shader.startsWith('"') && shader.endsWith('"'))
                shader.substring(1, shader.length - 1)
            val url = context::class.java.getResource("$root/$shader")
            return File(url.toURI()).readText() + "\n"
        }

        private fun parseInclude(root: String, shader_: String): String {
            val shader = shader_.trim { it == '"' }
            return ClassLoader.getSystemResourceAsStream("$root/$shader")?.use {
                InputStreamReader(it).readText()
            } ?: throw FileNotFoundException("$root/$shader does not exist")
        }

        /*
        this is inspired by pyarlib from Pyarelal Knowles, Nvidia:
        - https://github.com/pknowles/pyarlib
        - https://github.com/pknowlesnv
         */

        private fun parse(root: String, shader: String, existingDefines: Defines, defs: Defines): String {
            val input = ClassLoader.getSystemResourceAsStream("$root/$shader")
                    ?: throw FileNotFoundException("$root/$shader does not exist")
            val builder = StringBuilder(input.reader().readText())
            // search for includes and parse
            var index = builder.indexOf('#')
            while (index != -1) {
                builder.extractDefine(index)?.let {
                    val (define, value) = it
                    existingDefines[define] = value
                    if (define in defs) {
//                        builder.
                    }
                }
                builder.extractInclude(index)?.let {
//                    builder.insert(index, parse(root, it))
                }
                index = builder.indexOf('#', startIndex = index)
            } // TODO remember to increment index when including parsed shaders
            val (a, b) = builder.extractDefine(2)!!
            return builder.toString()
        }

        fun readUncomment(input: InputStream): StringBuilder {
            val text = StringBuilder(input.reader().readText())
            var i = -1
            fun p(d: Int = 0) = text.getOrElse(i + d) { NUL }
            while (++i < text.length) {
                // remove block comments
                if (p() == '/' && p(1) == '*') {
                    var end = text.indexOf("*/", i) + 2
                    if (text[end] == '\r') end++
                    if (text[end] == '\n') end++
                    text.delete(i, end)
                }
                //remove `//` comments
                if (p() == '/' && p(1) == '/') {
                    var b = 1
                    // delete any leading space, tab
                    while (p(-b) == ' ' || p(-b) == '\t')
                        b++
                    // if just after a new line, delete that also
                    if (p(-b) == '\n') b++
                    if (p(-b) == '\r') b++
                    val start = i - (b - 1)
                    i += 2 // skip the two slashes
                    while (p() != '\n' && p() != '\r')
                        i++
                    text.delete(start, i)
                    // reset index
                    i = start
                }
            }
            return text
        }

        private fun StringBuilder.extractInclude(startIndex: Int = 0): String? {
            var index = startIndex
            fun p() = if (index < length) get(index) else NUL

            //whitespace after #
            while (p() == ' ' || p() == '\t')
                ++index

            //the 'include' word
            if (!regionMatches(index, "include", 0, length = 7))
                return null

            //whitespace before filename
            while (p() == ' ' || p() == '\t')
                ++index
            val doubleQuoted = p() == '"'
            val start = index + doubleQuoted.i

            //the filename
            if (doubleQuoted)
                while (p() != '"' && index != length)
                    ++index
            else
                while (p() != ' ' && p() != '\t' && p() != '\r' && p() != '\n')
                    ++index
            return when (val end = index) {
                start -> null
                else -> substring(start, end)
            }
        }

        private fun StringBuilder.extractDefine(startIndex: Int = 0): Pair<String, String>? {
            var index = startIndex
            fun p() = get(index)

            //whitespace after #
            while (p() == ' ' || p() == '\t')
                ++index

            //the 'define' word
            if (!regionMatches(index, "define", 0, length = 6))
                return null

            //whitespace before word
            while (p() == ' ' || p() == '\t')
                ++index

            //the filename
            var start = index
            while (p() != ' ' && p() != '\t' && p() != '\r' && p() != '\n')
                ++index //everything until space or newline
            var end = index

            if (start == end)
                return null

            val filename = substring(start, end)

            //whitespace after filename
            while (p() == ' ' || p() == '\t')
                ++index

            //the value
            start = index
            while (p() != ' ' && p() != '\t' && p() != '\r' && p() != '\n')
                ++index //everything until space or newline
            end = index

            return when (start) {
                end -> null
                else -> filename to substring(start, end)
            }
        }
    }
}

inline class GLshaders(val i: IntBuffer)