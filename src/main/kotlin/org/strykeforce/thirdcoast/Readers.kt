package org.strykeforce.thirdcoast

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.UserInterruptException
import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT
import org.strykeforce.thirdcoast.command.MenuCommand
import org.strykeforce.thirdcoast.command.prompt

const val INVALID = -1
const val BACK = -2
const val QUIT = -3

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
        else DOUBLE_FORMAT.format(default)
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


fun LineReader.readMenu(command: MenuCommand): Int {
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

val menuChoices = sortedSetOf(
    '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z'
)


fun Terminal.readRawMenu(size: Int, prompt: String, quit: Boolean = false): Int {
    val validMenuChoices = menuChoices.headSet(menuChoices.elementAt(size))
    val prev = this.enterRawMode()
    this.writer().print(prompt)
    this.flush()
    val char = this.reader().read().toChar()
    this.attributes = prev
    this.writer().println()
    return when (char) {
        'b', 'B', '\r' -> BACK
        'q', 'Q' -> if (quit) QUIT else INVALID
        else -> validMenuChoices.indexOf(char) // -1 == INVALID
    }
}