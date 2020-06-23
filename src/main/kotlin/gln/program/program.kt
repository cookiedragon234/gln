@file:Suppress("NOTHING_TO_INLINE")

package gln.program

import kool.get
import org.lwjgl.opengl.GL20
import kotlin.properties.Delegates


var programName: IntArray by Delegates.notNull()


inline fun glCreatePrograms(programs: IntArray) = repeat(programs.size) { programs[it] = GL20.glCreateProgram() }

inline fun glUseProgram(program: Enum<*>) = GL20.glUseProgram(programName[program])
inline fun glUseProgram(program: IntArray) = GL20.glUseProgram(program[0])
inline fun glUseProgram(program: GlslProgram) = GL20.glUseProgram(program.id)
inline fun glUseProgram() = GL20.glUseProgram(0)

inline fun glDeletePrograms(programs: Enum<*>) = GL20.glDeleteProgram(programName[programs])
inline fun glDeletePrograms(programs: IntArray) = programs.forEach { GL20.glDeleteProgram(it) }
inline fun glDeleteProgram(program: GlslProgram) = GL20.glDeleteProgram(program.id)
inline fun glDeletePrograms(vararg programs: GlslProgram) = programs.forEach { GL20.glDeleteProgram(it.id) }

typealias ShaderSource = String