package org.strykeforce.thirdcoast

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.UserInterruptException
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.MenuCommand
import org.strykeforce.thirdcoast.command.prompt

const val INVALID = 0
const val BACK = -1
const val QUIT = -2

private const val PROMPT = "> "

fun LineReader.readInt(prompt: String = PROMPT, default: Int = 0) =
    this.readDouble(prompt, default.toDouble(), true).toInt()

fun LineReader.readDouble(
    prompt: String = PROMPT,
    default: Double = 0.0,
    truncate: Boolean = false
): Double {
    val buffer =
        if (truncate && default == Math.floor(default)) default.toInt().toString()
        else default.toString()
    return try {
        val line = this.readLine(prompt, null, buffer).trim()
        if (line.isEmpty()) throw EndOfFileException()
        line.toDouble()
    } catch (e: Exception) {
        when (e) {
            is EndOfFileException, is UserInterruptException -> default
            else -> throw e
        }
    }
}

fun LineReader.readBoolean(prompt: String = PROMPT, default: Boolean = false) = try {
    val line = this.readLine(prompt, null, if (default) "y" else "n").trim()
    when (line.toLowerCase()) {
        "y" -> true
        "n" -> false
        "" -> throw EndOfFileException()
        else -> throw IllegalArgumentException()
    }
} catch (e: Exception) {
    when (e) {
        is EndOfFileException, is UserInterruptException -> default
        else -> throw e
    }
}

fun LineReader.readIntList(prompt: String = PROMPT, default: List<Int> = emptyList()): List<Int> = try {
    val line = this.readLine(prompt, null, default.joinToString(",")).trim()
    if (line.isEmpty()) throw EndOfFileException()
    line.split(',').map { it.trim().toInt() }
} catch (e: Exception) {
    when (e) {
        is EndOfFileException, is UserInterruptException -> default
        else -> throw e
    }
}


fun LineReader.readMenu(command: Command): Int {
    command as? MenuCommand ?: throw IllegalArgumentException("not a MenuCommand")
    return this.readMenu(command.children.size, command.prompt(), quit = true)
}

fun LineReader.readMenu(size: Int, prompt: String, default: String = "", quit: Boolean = false) = try {

    val line = this.readLine(prompt, null, default).trim()

    try {
        val choice = line.toInt()
        when (choice) {
            in 1..size -> choice
            else -> INVALID
        }
    } catch (nfe: NumberFormatException) {
        when (line.toLowerCase()) {
            "b" -> BACK
            "q" -> if (quit) QUIT else INVALID
            else -> INVALID
        }
    }
} catch (e: EndOfFileException) {
    QUIT
}