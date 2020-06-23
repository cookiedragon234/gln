package gln

import gln.program.GlslProgram
import io.kotest.core.spec.style.StringSpec
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL


class ProgramTest : StringSpec() {

    inline fun newCurrentContext(block: () -> Unit) {
        glfwInit()
        val window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL)
        glfwMakeContextCurrent(window)
        GL.createCapabilities()

        block()

        glfwDestroyWindow(window)
        glfwTerminate()
    }

    val defs = mapOf(
            "BLOCKSORT_BASE" to "0",
            "BLOCKSORT_GMEM" to "0",
            "BLOCKSORT_LMEM" to "0",
            "COMPOSITE_ONLY" to "0",
            "DIRECT_RENDER" to "0",
            "HAS_GEOMETRY" to "0",
            "INCLUDE_MERGESORT" to "0",
            "INDEX_TILE_SIZE" to "2,8",
            "INDEX_WITH_TILES" to "0",
            "LFB_BINDLESS" to "0",
            "LFB_FRAG_SIZE" to "2",
            "LFB_METHOD_H" to "lfbLL.glsl",
            "LFB_REQUIRE_COUNTS" to "0",
            "SORT_IN_BOTH" to "0",
            "SORT_IN_REGISTERS" to "0",
            "_MAX_FRAGS" to "64")

    init {
        "count" {
            newCurrentContext {
                val program = GlslProgram()
//                GlslProgram.fromRoot("shaders", "phong", defines = defs)
            }
        }
    }
}