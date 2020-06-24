package gln.program

import gln.Dsl
import gln.checkError
import gln.identifiers.GlProgram
import gln.identifiers.GlShader
import org.lwjgl.opengl.GL20C
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

typealias Defines = MutableMap<String, String>

fun Defines(): Defines = mutableMapOf()

var activeProgram: GlslProgram? = null

/**
 * Mainly for:
 * - Java
 * - more advanced shader with #define/import(s)
 * - quick uniforms store-and-use
 * - one instance to modify and re-use over and over again
 */
open class GlslProgram {

    var id: Int = -1

    /** combined shader program name */
    var name = "<uninitialized>"
    var vert = ""
    var frag = ""
    var geom = ""
    var comp = ""

//    std::map<std::string, Location> uniforms;
//    std::map<std::string, Location> attributes;
//    std::map<std::string, UniformBlock> uniformBlocks

    //    static std::set<Shader*>* instances;
//    static std::map<GLuint, Shader*> instanceLookup;

    val dirs = ArrayList<String>()
    val contexts = ArrayList<Class<*>>()

//    std::vector<std::pair<std::string, int> > references; //all files included in shader and their timestamps

    val defines = Defines()
    val existingDefines = Defines()

    val program
        get() = GlProgram(id)

    /** need to reload() before use()? */
    var dirty = true
    var compileError = ""

    //    GLuint getLoc(const std::string& name);
//    void preSet(const std::string& name);
//    bool checkSet(const std::string& name, const std::string& type = "(whatever's correct)");
//    void init(); //called from constructor
//    void fillLocations();
    fun findFile(name: String): String {
        val result = dirs.any { ClassLoader.getSystemResource(it + name) != null }
                || contexts.any { it.javaClass.classLoader.getResource(name) != null }
        //if it exists in includeOverrides, ShaderBuild will find it
        if (name in includeOverrides) {
            //printf("Using internal %s\n", name.c_str());
            if (result && verbose)
                println("WARNING: Shader $name is overridden internally")
            return name
        }

        //check current directory or any added search paths
        if (result) {
            //printf("Using file %s\n", result.c_str());
            return name
        }

        return "" //doesn't exist, remove from list
    }

    val attribs = hashSetOf<Int>()

    val uniforms = HashMap<String, Int>()
    operator fun get(s: String): Int = uniforms.getOrPut(s) { GL20C.glGetUniformLocation(id, s) }

    constructor()

    constructor(filename: String) {
        name = filename
        vert = "$filename.vert"
        frag = "$filename.frag"
        geom = "$filename.geom"
        comp = "$filename.comp"
    }

    /** contains concatenated errors from compiling shaders, if there were any */
    val errorStr = StringBuilder()

    /** uniforms/attributes with invalid locations */
    val variableError = hashSetOf<String>()

    /** recompile the shader. called automatically on first use() or use() following define/undef */
    fun reload(): Boolean {

        //even if there's a compile error, it's no longer not dirty. if dirty is set later it may be because the error has been fixed
        dirty = false

        checkError()
        println(File(".").absolutePath)

        //check the separate files exist
        vert = findFile(vert)
        frag = findFile(frag)
        geom = findFile(geom)
        comp = findFile(comp)

        if (verbose)
            println("Shader: $name = v[$vert] - f[$frag] - g[$geom] - c[$comp]")

        //free current program if there is one
//        release()

        //sometimes it happens...
        if (vert.isEmpty() && frag.isEmpty() && geom.isEmpty() && comp.isEmpty()) {
            println("Error: No shaders found for \"$name\"")
            return false
        } else {
            errorStr.clear()
            var ok = true
        }

        return false
    }

    //IMPORTANT: call unuse on THIS object as attrib and other cleanups are done
    fun use(): Boolean {
        assert(attribs.isEmpty()) { "unuse() must be called, in order, after use()" }
        assert(activeProgram == null) { "must unuse() previous shader before use()" }

        if (dirty)
            reload()

//        if (error())
//            return false;
//
//        active = this;
//        glUseProgram(program);
        return true
    }

    fun unuse() = program.unuse()

    fun use(block: ProgramUse.() -> Unit) = program.use(block)

    // building utils

    //returns a line from a source file, if available
//    bool getFileLine(std::string file, int line, std::string& source);
//
//    //extracts file index and line number from log if possible. if errorStr is NULL, prints to stdout
//    void parseLog(std::string* errorStr, std::string log, int baseFile, bool readLineNumbers);
//
//    //parses a shader source file, recursively loading #includes and replacing #defines
//    bool parse(std::string file, FileBits& sourceBits, Defines& defs, std::vector<std::string> path = std::vector<std::string>());
//
//    //files that would normally be read from disk can be overridden with .include(<filename to override>, <file data>)
//    //NOTE: the data pointer must remain valid until all compile() calls complete
//    void include(std::string filename, const char* data);
//
//    //if defs is not passed to ::compile(), the local ::defines is used instead. ::define() adds to the local ::defines
//    void define(std::string def, std::string val);

    //compiles a shader, returning true on success
    fun compileAll(shader: String, defs: Defines = mutableMapOf()): List<String> =
        xShaderExts.mapTo(ArrayList()) { compile(shader + it) }

    fun compile(shader: String, defs: Defines = mutableMapOf()): String {
TODO()
    }

//    //returns a list of all files compiled so far, including #included files
//    std::vector<std::string> getReferences();
//
//    //links all compiled shaders and returns the program.
//    GLuint link(std::string* errStr = NULL);
//
//    //cleans up all variables, deletes shader objects etc.
//    void cleanup();

    companion object {

        val includeOverrides = mutableMapOf<String, String>()

        fun from(dirs: List<String>, shader: String, defines: Defines): GlslProgram {

            val shaderNames = arrayListOf<GlShader>()

            val glslProgram = GlslProgram()
            val program = glslProgram.program

            var isGraphic = false
            for (ext in xShaderExts)
                try {
                    isGraphic = true
                    val stream = find(dirs, shader + ext)
//                    val shaderName = GlShader.create(path = "$fullShader$ext")
//                    program += shaderName
//                    shaderNames += shaderName
                } catch (exc: RuntimeException) {
                    throw exc // propagate
                } catch (exc: FileNotFoundException) {
                    // silent ignore
                }

//            if (!isGraphic)
//                program += GlShader.create(path = "$fullShader.comp")

            program.link()

            if (!program.linkStatus)
                System.err.println("Linker failure: ${program.infoLog}")     // TODO change to exception

            for (s in shaderNames) {
                program -= s
                s.delete()
            }

            return glslProgram
        }

        fun find(dirs: List<String>, shader: String): InputStream? {
            for (dir in dirs) {
                val d = when {
                    dir.endsWith('\\') || dir.endsWith('/') -> dir
                    else -> dir + File.pathSeparator
                }
                ClassLoader.getSystemResourceAsStream(d + shader)?.let { return it }
            }
            return null
        }

        var verbose = true

        //        private val shaderExtensions = arrayOf(".vert", ".tesc", ".tese", ".geom", ".frag", ".comp")
        private val xShaderExts = arrayOf(".vert", ".tesc", ".tese", ".geom", ".frag")

        // dsl
        @Dsl
        operator fun invoke(block: GlslProgram.() -> Unit): GlslProgram = GlslProgram().apply(block)
    }
}