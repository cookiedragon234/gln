package gln.program

import gln.checkError
import gln.identifiers.GlProgram
import gln.identifiers.GlShader
import org.lwjgl.opengl.GL20C
import java.io.FileNotFoundException

typealias Defines = MutableMap<String, String>

fun Defines() = mutableMapOf<String, String>()

var activeProgram: GlslProgram? = null

/**
 * Mainly for:
 * - Java
 * - more advanced shader with #define/import(s)
 * - quick uniforms store-and-use
 * - one instance to modify and re-use over and over again
 */
open class GlslProgram(
        @JvmField
        var id: Int = -1) {

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
    val includeOverrides = mutableMapOf<String, String>()

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
                println("WARNING: Shader $name is overidden internally")
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

    constructor(filename: String) : this() {
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
            reload();

//        if (error())
//            return false;
//
//        active = this;
//        glUseProgram(program);
        return true
    }

    fun unuse() = program.unuse()

    fun use(block: ProgramUse.() -> Unit) = program.use(block)


//
//    constructor(vert: GlShader, frag: GlShader, block: GlProgram) : this() {
//
//        attach(vert)
//        attach(frag)
//
//        link()
//
//        if (!linkStatus)
//            throw Exception("Linker failure: $infoLog")
//
//        detach(vert)
//        detach(frag)
//        vert.delete()
//        frag.delete()
//    }
//
//    constructor(vertSrc: String, fragSrc: String) : this() {
//
//        val v = GlShader.createFromSource(ShaderType.VERTEX_SHADER, vertSrc)
//        val f = GlShader.createFromSource(ShaderType.FRAGMENT_SHADER, fragSrc)
//
//        attach(v)
//        attach(f)
//
//        link()
//
//        if (!linkStatus)
//            throw Exception("Linker failure: $infoLog")
//
//        detach(v)
//        detach(f)
//        v.delete()
//        f.delete()
//    }
//
//    constructor(vertSrc: String, geomSrc: String, fragSrc: String) : this() {
//
//        val v = GlShader.createFromSource(ShaderType.VERTEX_SHADER, vertSrc)
//        val g = GlShader.createFromSource(ShaderType.GEOMETRY_SHADER, geomSrc)
//        val f = GlShader.createFromSource(ShaderType.FRAGMENT_SHADER, fragSrc)
//
//        attach(v)
//        attach(g)
//        attach(f)
//
//        link()
//
//        if (!linkStatus)
//            throw Exception("Linker failure: $infoLog")
//
//        detach(v)
//        detach(g)
//        detach(f)
//        v.delete()
//        g.delete()
//        f.delete()
//    }
//
//    // for Learn OpenGL
//
//    /** (root, vertex, fragment) or (vertex, fragment)  */
//    /* constructor(context: Class<*>, vararg strings: String) {
//
//         val root =
//                 if (strings[0].isShaderPath())
//                     ""
//                 else {
//                     var r = strings[0]
//                     if (r[0] != '/')
//                         r = "/$r"
//                     if (!r.endsWith('/'))
//                         r = "$r/"
//                     r
//                 }
//
//         val (shaders, uniforms) = strings.drop(if (root.isEmpty()) 0 else 1).partition { it.isShaderPath() }
//
//         val shaderNames = shaders.map { createShaderFromPath(context, root + it) }.onEach { GL20.glAttachShader(name, it) }
//
//         GL20.glLinkProgram(name)
//
//         if (GL20.glGetProgrami(name, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
//             System.err.println("Linker failure: ${GL20.glGetProgramInfoLog(name)}")
//
//         shaderNames.forEach {
//             GL20.glDetachShader(name, it)
//             GL20.glDeleteShader(it)
//         }
//
//         uniforms.forEach {
//             val i = GL20.glGetUniformLocation(name, it)
//             if (i != -1)
//                 this.uniforms[it] = i
//             else
//                 println("unable to find '$it' uniform location!")
//         }
//     }
// */
//
//
//
//    // TODO remo?
//    fun createProgram(shaderList: List<Int>): Int {
//
//        val program = GlslProgram()
//
//        shaderList.forEach { program += it }
//
//        program.link()
//
//        if (!program.linkStatus)
//            throw Exception("Linker failure: ${program.infoLog}")
//
//        shaderList.forEach {
//            program -= it
//            GL20C.glDeleteShader(it)
//        }
//
//        return program.id
//    }

    companion object {

        var verbose = true
    }
}