package org.strykeforce.thirdcoast

import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.MenuCommand

//    private val logger = KotlinLogging.logger {}

class Shell(
    private var command: Command,
    private val terminal: Terminal,
    private val reader: LineReader
) {
    private val writer = terminal.writer()

    fun run() {
        while (true) {
            if (command is MenuCommand) {
                printMenu()
                val choice = reader.readMenu(command)
                when (choice) {
                    INVALID -> writer.println("try again")
                    BACK -> command = command.parent ?: command
                    QUIT -> {
                        writer.println("bye")
                        writer.flush()
                        terminal.close()
                        return
                    }
                    else -> command = command.children[choice - 1]
                }
            } else {
                command = command.execute()
            }
        }
    }

    private fun printMenu() {
        command.children.forEachIndexed { index, command ->
            writer.println(command.menu.toMenu(index))
        }
        if (command.parent != null)
            writer.println("back to previous menu".toMenu("b"))
        writer.println("quit TCT".toMenu("q"))
    }

}

fun String.toMenu(index: Int): String = this.toMenu((index + 1).toString())

fun String.toMenu(index: String): String = AttributedStringBuilder()
    .styled(AttributedStyle.BOLD, index)
    .append(": $this")
    .toAnsi()

