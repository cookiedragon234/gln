package gln.identifiers

import glm_.bool
import glm_.i
import gln.NUL
import gln.ShaderType
import gln.gl
import gln.program.Defines
import org.lwjgl.opengl.*
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
        fun createFromSource(type: ShaderType, sourceText: String) =
                create(type).apply {
                    source(sourceText)
                    compile()
                    require(compileStatus) { "glShader compile status false: $infoLog" }
                }

        fun createFromSource(type: ShaderType, sourceText: Array<String>) =
                create(type).apply {
                    source(*sourceText)
                    compile()
                    require(compileStatus) { "glShader compile status false: $infoLog" }
                }

        @Throws(Exception::class)
        fun createFromPath(path: String, transform: ((String) -> String)?): GlShader {
            TODO()
        }

        @Throws(Exception::class)
        fun createFromPath(path: String, defines: Map<String, String> = emptyMap()): GlShader {

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

            return createFromSource(ShaderType(path.type), source)
        }

        @Throws(Exception::class)
        fun createFromPath(context: Class<*>, path: String): Int {

            val shader = GL20.glCreateShader(path.type)

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

            GL20.glShaderSource(shader, source)

            GL20.glCompileShader(shader)

            val status = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS)
            require(status.bool) {
                "Compiler failure in shader '${path.substringAfterLast('/')}'\n${GL20C.glGetShaderInfoLog(shader)}"
            }

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
            fun p(d: Int = 0) = text[i + d]
            while (++i < text.length) {
                // remove block comments
                if (p() == '*' && p(1) == '/') {
                    val end = text.indexOf("*/", i)
                    text.delete(i, end + 1)
                }
                //remove `//` comments
                if (p() == '/' && p(1) == '/') {
                    val start = i
                    i += 2
                    while (p() != '\n')
                        i++
                    text.delete(start, ++i)
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

        private val String.type: Int
            get() = when (substringAfterLast('.')) {
                "vert" -> GL20.GL_VERTEX_SHADER
                "tesc" -> GL40.GL_TESS_CONTROL_SHADER
                "tese" -> GL40.GL_TESS_EVALUATION_SHADER
                "geom" -> GL32.GL_GEOMETRY_SHADER
                "frag" -> GL20.GL_FRAGMENT_SHADER
                "comp" -> GL43.GL_COMPUTE_SHADER
                else -> error("invalid shader extension")
            }
    }
}

inline class GLshaders(val i: IntBuffer)