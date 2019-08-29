package gln.glf

import glm_.L
import glm_.vec2.Vec2
import glm_.vec2.Vec2us
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import glm_.vec4.Vec4b
import glm_.vec4.Vec4ub
import gln.VertexAttrType
import gln.vertexArray.glDisableVertexAttribArray
import gln.vertexArray.glEnableVertexAttribArray
import gln.vertexArray.glVertexAttribPointer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20C


object glf {

    object pos2 : VertexLayout() {
        val stride = Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0))
    }

    object pos3 : VertexLayout() {
        val stride = Vec3.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0))
    }

    object pos4 : VertexLayout() {
        val stride = Vec4.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, 0))
    }

    object pos3_col3 : VertexLayout() {
        val stride = Vec3.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    object pos3_col4 : VertexLayout() {
        val stride = Vec3.size + Vec4.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    object pos3_col4b : VertexLayout() {
        val stride = Vec3.size + Vec4b.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4b.length, VertexAttrType(GL_UNSIGNED_BYTE), false, stride, Vec3.size.L))
    }

    object pos4_col4 : VertexLayout() {
        val stride = Vec4.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec4.size.L))
    }

    object pos4_col4_uv2 : VertexLayout() {
        val stride = Vec4.size * 2 + Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec4.size.L),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec4.size.L * 2))
    }


    object pos2_tc2 : VertexLayout() {
        val stride = Vec2.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L))
    }

    object pos2_tc2_col4b : VertexLayout() {
        val stride = Vec2.size * 2 + Vec4b.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L),
                VertexAttribute(semantic.attr.COLOR, Vec4b.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L * 2))
    }

    object pos2us_tc2us : VertexLayout() {
        val stride = Vec2us.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2us.length, VertexAttrType(GL_UNSIGNED_SHORT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2us.length, VertexAttrType(GL_UNSIGNED_SHORT), false, stride, Vec2us.size.L))
    }

    object pos3_tc2 : VertexLayout() {
        val stride = Vec3.size + Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    object pos3_col4ub : VertexLayout() {
        val stride = Vec3.size + Vec4ub.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4ub.length, VertexAttrType(GL_UNSIGNED_BYTE), false, stride, Vec3.size.L))
    }

    object pos2_tc3 : VertexLayout() {
        val stride = Vec2.size + Vec3.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L))
    }

    object pos3_tc3 : VertexLayout() {
        val stride = Vec3.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    object pos3_nor3 : VertexLayout() {
        val stride = Vec3.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.NORMAL, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    object pos3_nor3_tc2 : VertexLayout() {
        val stride = Vec3.size * 2 + Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.NORMAL, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L * 2))
    }

    object pos3_nor3_col4 : VertexLayout() {
        val stride = Vec3.size * 2 + Vec4.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.NORMAL, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L * 2))
    }

}

object Vertex { // TODO

    class pos2 : VertexLayout() {
        val stride = Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0))
    }

    class pos3 : VertexLayout() {
        val stride = Vec3.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0))
    }

    class pos4 : VertexLayout() {
        val stride = Vec4.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, 0))
    }

    class pos3_col4 : VertexLayout() {
        val stride = Vec3.size + Vec4.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    class pos3_col4b : VertexLayout() {
        val stride = Vec3.size + Vec4b.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4b.length, VertexAttrType(GL_UNSIGNED_BYTE), false, stride, Vec3.size.L))
    }

    class pos4_col4 : VertexLayout() {
        val stride = Vec4.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec4.size.L))
    }

    class pos4_col4_uv2 : VertexLayout() {
        val stride = Vec4.size * 2 + Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.COLOR, Vec4.length, VertexAttrType(GL_FLOAT), false, stride, Vec4.size.L),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec4.size.L * 2))
    }


    class pos2_tc2(val p: Vec2, val t: Vec2)

    class pos2_tc2_col4b : VertexLayout() {
        val stride = Vec2.size * 2 + Vec4b.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L),
                VertexAttribute(semantic.attr.COLOR, Vec4b.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L * 2))
    }

    class pos2us_tc2us : VertexLayout() {
        val stride = Vec2us.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2us.length, VertexAttrType(GL_UNSIGNED_SHORT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2us.length, VertexAttrType(GL_UNSIGNED_SHORT), false, stride, Vec2us.size.L))
    }

    class pos3_tc2 : VertexLayout() {
        val stride = Vec3.size + Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    class pos3_col4ub(var p: Vec3, var c: Vec4ub)

    class pos2_tc3 : VertexLayout() {
        val stride = Vec2.size + Vec3.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec2.size.L))
    }

    class pos3_tc3 : VertexLayout() {
        val stride = Vec3.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.TEX_COORD, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    class pos3_nor3 : VertexLayout() {
        val stride = Vec3.size * 2
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.NORMAL, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L))
    }

    class pos3_nor3_tc2 : VertexLayout() {
        val stride = Vec3.size * 2 + Vec2.size
        override var attributes = arrayOf(
                VertexAttribute(semantic.attr.POSITION, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, 0),
                VertexAttribute(semantic.attr.NORMAL, Vec3.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L),
                VertexAttribute(semantic.attr.TEX_COORD, Vec2.length, VertexAttrType(GL_FLOAT), false, stride, Vec3.size.L * 2))
    }

    class pos3_nor3_col4(var p: Vec3, var n: Vec3, var c: Vec4)

    abstract class Base
}


abstract class VertexLayout {

    abstract var attributes: Array<VertexAttribute>

    operator fun get(index: Int) = attributes[index]

    fun set() = glVertexAttribPointer(this)

    fun enable() = glEnableVertexAttribArray(this)
    fun disable() = glDisableVertexAttribArray(this)

    inline fun enabled(block: () -> Unit) {
        glEnableVertexAttribArray(this)
        block()
        glDisableVertexAttribArray(this)
    }
}

class VertexAttribute(
        var index: Int,
        var size: Int,
        var type: VertexAttrType,
        var normalized: Boolean,
        var interleavedStride: Int,
        var pointer: Long) { // TODO Ptr?

    // --- [ glVertexAttribPointer ] ---

    fun pointer() = GL20C.nglVertexAttribPointer(index, size, type.i, normalized, interleavedStride, pointer.L)

    // --- [ glEnableVertexAttribArray ] ---

    fun enable() = GL20C.glEnableVertexAttribArray(index)

    // --- [ glDisableVertexAttribArray ] ---

    fun disable() = GL20C.glDisableVertexAttribArray(index)

    var bufferBinding = 0
    var enabled = false
    var integer = false
    var divisor = 0
    var current = Vec4(0, 0, 0, 1)
}