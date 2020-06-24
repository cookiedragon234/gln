package gln.program

import gln.identifiers.GlShader
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ReadUncomment : StringSpec() {

    init {

        "lfb.glsl" {
            val lfb = ClassLoader.getSystemResourceAsStream("oit/lfb/shaders/lfb.glsl")!!
            val a = GlShader.readUncomment(lfb).toString()
            val b = "\r\n" +
                    "#ifndef GLOBAL_SORT\r\n" +
                    "#define GLOBAL_SORT 0\r\n" +
                    "#endif\r\n" +
                    "\r\n" +
                    "#ifndef LFB_READONLY\r\n" +
                    "#define LFB_READONLY 0\r\n" +
                    "#endif\r\n" +
                    "\r\n" +
                    "#ifndef LFB_L_REVERSE\r\n" +
                    "#define LFB_L_REVERSE 0\r\n" +
                    "#endif\r\n" +
                    "\r\n" +
                    "#define _MAX_FRAGS this_is_defined_by_the_application\r\n" +
                    "\r\n" +
                    "#ifndef MAX_FRAGS\r\n" +
                    "#define MAX_FRAGS _MAX_FRAGS\r\n" +
                    "#endif\r\n" +
                    "\r\n" +
                    "#define LFB_REQUIRE_COUNTS 0\r\n" +
                    "#define LFB_BINDLESS 0\r\n" +
                    "\r\n" +
                    "#define LFB_FRAG_SIZE set_by_app\r\n" +
                    "\r\n" +
                    "#if LFB_FRAG_SIZE == 1\r\n" +
                    "#define LFB_IMAGE_TYPE r32f\r\n" +
                    "#define LFB_FRAG_TYPE float\r\n" +
                    "#define LFB_FRAG_PAD ,0,0,0\r\n" +
                    "#define LFB_FRAG_DEPTH(v) (v)\r\n" +
                    "#elif LFB_FRAG_SIZE == 2\r\n" +
                    "#define LFB_IMAGE_TYPE rg32f\r\n" +
                    "#define LFB_FRAG_TYPE vec2\r\n" +
                    "#define LFB_FRAG_PAD ,0,0\r\n" +
                    "#define LFB_FRAG_DEPTH(v) (v.y)\r\n" +
                    "#elif LFB_FRAG_SIZE == 3\r\n" +
                    "#define LFB_IMAGE_TYPE rgb32f\r\n" +
                    "#define LFB_FRAG_TYPE vec3\r\n" +
                    "#define LFB_FRAG_PAD ,0\r\n" +
                    "#define LFB_FRAG_DEPTH(v) (v.z)\r\n" +
                    "#elif LFB_FRAG_SIZE == 4\r\n" +
                    "#define LFB_IMAGE_TYPE rgba32f\r\n" +
                    "#define LFB_FRAG_TYPE vec4\r\n" +
                    "#define LFB_FRAG_PAD\r\n" +
                    "#define LFB_FRAG_DEPTH(v) (v.w)\r\n" +
                    "#else\r\n" +
                    "#error Invalid LFB_FRAG_SIZE\r\n" +
                    "#endif\r\n" +
                    "\r\n" +
                    "#if LFB_BINDLESS\r\n" +
                    "\t#extension GL_NV_gpu_shader5 : enable\r\n" +
                    "\t#extension GL_NV_shader_buffer_load : enable\r\n" +
                    "\r\n" +
                    "\t#define LFB_EXPOSE_TABLE uint*\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_COHERENT coherent LFB_EXPOSE_TABLE\r\n" +
                    "\t#define LFB_EXPOSE_DATA LFB_FRAG_TYPE*\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_GET(buffer, index) int(buffer[index])\r\n" +
                    "\t#define LFB_EXPOSE_DATA_GET(buffer, index) buffer[index]\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_SET(buffer, index, val) buffer[index] = uint(val)\r\n" +
                    "\t#define LFB_EXPOSE_DATA_SET(buffer, index, val) buffer[index] = val\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_ADD(buffer, index, val) int(atomicAdd(buffer + index, val))\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_EXCHANGE(buffer, index, val) int(atomicExchange(buffer + index, val))\r\n" +
                    "#else\r\n" +
                    "\t#if LFB_READONLY\r\n" +
                    "\t\t#define LFB_EXPOSE_TABLE layout(r32ui) readonly uimageBuffer\r\n" +
                    "\t\t#define LFB_EXPOSE_TABLE_COHERENT LFB_EXPOSE_TABLE\r\n" +
                    "\t\t#define LFB_EXPOSE_DATA layout(LFB_IMAGE_TYPE) readonly imageBuffer\r\n" +
                    "\t#else\r\n" +
                    "\t\t#define LFB_EXPOSE_TABLE layout(r32ui) uimageBuffer\r\n" +
                    "\t\t#define LFB_EXPOSE_TABLE_COHERENT coherent LFB_EXPOSE_TABLE\r\n" +
                    "\t\t#define LFB_EXPOSE_DATA layout(LFB_IMAGE_TYPE) imageBuffer\r\n" +
                    "\t#endif\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_GET(buffer, index) int(imageLoad(buffer, index).r)\r\n" +
                    "\t#define LFB_EXPOSE_DATA_GET(buffer, index) LFB_FRAG_TYPE(imageLoad(buffer, index))\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_SET(buffer, index, val) imageStore(nextPtrs, index, uvec4(val, 0U, 0U, 0U));\r\n" +
                    "\t#define LFB_EXPOSE_DATA_SET(buffer, index, val) imageStore(buffer, index, vec4(val LFB_FRAG_PAD));\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_ADD(buffer, index, val) int(imageAtomicAdd(buffer, index, val))\r\n" +
                    "\t#define LFB_EXPOSE_TABLE_EXCHANGE(buffer, index, val) int(imageAtomicExchange(buffer, index, val).r)\r\n" +
                    "#endif\r\n" +
                    "\r\n" +
                    "#define LFB_SIZE(suffix) lfbInfo##suffix.size\r\n" +
                    "\r\n" +
                    "#define LFB_COORD_HASH(suffix, coord) \\\r\n" +
                    "\t(int((coord).y * lfbInfo##suffix.size.y) * lfbInfo##suffix.size.x + int((coord).x * lfbInfo##suffix.size.x))\r\n" +
                    "#define LFB_HASH(suffix, coord) \\\r\n" +
                    "\t((int((coord).y) * lfbInfo##suffix.size.x) + int((coord).x))\r\n" +
                    "#define LFB_FRAG_HASH(suffix) LFB_HASH(suffix, gl_FragCoord.xy)\r\n" +
                    "\r\n" +
                    "vec2 sillyEncode(vec4 v)\r\n" +
                    "{\r\n" +
                    "\treturn vec2(floor(clamp(v.r, 0.0, 1.0)*4096.0) + clamp(v.g*0.9, 0.0, 0.9), floor(clamp(v.b, 0.0, 1.0)*4096.0) + clamp(v.a*0.9, 0.0, 0.9));\r\n" +
                    "}\r\n" +
                    "vec4 sillyDecode(vec2 v)\r\n" +
                    "{\r\n" +
                    "\treturn vec4(floor(v.x)/4096.0, fract(v.x)/0.9, floor(v.y)/4096.0, fract(v.y)/0.9);\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "LFB_FRAG_TYPE make_lfb_data(vec2 d)\r\n" +
                    "{\r\n" +
                    "#if LFB_FRAG_SIZE == 1\r\n" +
                    "\treturn LFB_FRAG_TYPE(d.y);\r\n" +
                    "#elif LFB_FRAG_SIZE == 2\r\n" +
                    "\treturn d;\r\n" +
                    "#elif LFB_FRAG_SIZE == 3\r\n" +
                    "\treturn LFB_FRAG_TYPE(d.x, 0.0, d.y);\r\n" +
                    "#elif LFB_FRAG_SIZE == 4\r\n" +
                    "\treturn LFB_FRAG_TYPE(d.x, 0.0, 0.0, d.y);\r\n" +
                    "#endif\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "#include \"lfbTiles.glsl\"\r\n" +
                    "\r\n" +
                    "#include \"LFB_METHOD_H\"\r\n" +
                    "\r\n" +
                    "#define LFB_FOREACH(suffix, frag) \\\r\n" +
                    "\tLFB_ITER_BEGIN(suffix); \\\r\n" +
                    "\tfor (; LFB_ITER_CONDITION(suffix); LFB_ITER_INC(suffix) ) \\\r\n" +
                    "\t{ \\\r\n" +
                    "\t\tLFB_FRAG_TYPE frag = LFB_GET(suffix);\r\n" +
                    "\r\n" +
                    "\r\n"
            a shouldBe b
            println()
        }
    }
}