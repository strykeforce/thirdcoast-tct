package org.strykeforce.thirdcoast

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.MenuCommand
import org.strykeforce.thirdcoast.command.prompt

const val INVALID = 0
const val BACK = -1
const val QUIT = -2

private const val PROMPT = "> "

fun LineReader.readInt(prompt: String = PROMPT, default: Int = 0): Int {
    val buffer = default.toString()
    while (true) {
        return try {
            val line = this.readLine(prompt, null, buffer).trim()
            if (line.isEmpty()) throw EndOfFileException()
            line.toInt()
        } catch (nfe: NumberFormatException) {
            continue
        } catch (eof: EndOfFileException) {
            default
        }
    }
}

fun LineReader.readDouble(prompt: String = PROMPT, default: Double = 0.0): Double {
    val buffer = default.toString()
    while (true) {
        return try {
            val line = this.readLine(prompt, null, buffer).trim()
            if (line.isEmpty()) throw EndOfFileException()
            line.toDouble()
        } catch (nfe: NumberFormatException) {
            continue
        } catch (eof: EndOfFileException) {
            default
        }
    }
}

fun LineReader.readBoolean(prompt: String = PROMPT, default: Boolean = false): Boolean {
    val buffer = if (default) "y" else "n"
    while (true) {
        return try {
            val line = this.readLine(prompt, null, buffer).trim()
            when (line.toLowerCase()) {
                "y" -> true
                "n" -> false
                "" -> throw EndOfFileException()
                else -> throw IllegalArgumentException()
            }
        } catch (iae: IllegalArgumentException) {
            continue
        } catch (eof: EndOfFileException) {
            default
        }
    }
}

fun LineReader.readIntList(prompt: String = PROMPT, default: List<Int> = emptyList()): List<Int> {
    while (true) {
        return try {
            val line = this.readLine(prompt, null, default.joinToString(",")).trim()
            if (line.isEmpty()) throw EndOfFileException()
            line.split(',').map { it.trim().toInt() }
        } catch (e: NumberFormatException) {
            continue
        } catch (e: EndOfFileException) {
            emptyList()
        }
    }
}

fun LineReader.readMenu(command: Command) = try {
    command as? MenuCommand ?: throw IllegalArgumentException("not a MenuCommand")

    val line = this.readLine(command.prompt()).trim()

    try {
        val choice = line.toInt()
        when (choice) {
            in 1..command.children.size -> choice
            else -> INVALID
        }
    } catch (nfe: NumberFormatException) {
        when (line.toLowerCase()) {
            "b" -> BACK
            "q" -> QUIT
            else -> INVALID
        }
    }
} catch (e: EndOfFileException) {
    QUIT
}