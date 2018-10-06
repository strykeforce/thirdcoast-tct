package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.strykeforce.thirdcoast.*
import java.util.*

class MenuCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override val children = TreeSet<Command>(compareBy(Command::order, Command::key))

    val validMenuChoices: SortedSet<Char>
        get() = menuChoices.headSet(menuChoices.elementAt(children.size))

    override fun execute(): Command {
        while (true) {
            printMenu()
            val choice = readRawMenuChoice()
            when (choice) {
                INVALID -> terminal.warn("Please enter a choice from this menu")
                BACK -> return parent ?: this
                QUIT -> return QuitCommand()
                else -> return children.elementAt(choice)
            }
        }
    }

    private fun printMenu() {
        val writer = terminal.writer()
        children.forEachIndexed { index, cmd ->
            writer.println(cmd.menu.toRawMenu(index))
        }
        if (parent != null)
            writer.println("back to previous menu".toMenu("b"))
        writer.println("quit TCT".toMenu("q"))

    }

    private fun readMenuChoice(): Int {
        return reader.readMenu(children.size, prompt(), quit = true)
    }

    private fun readRawMenuChoice(): Int {
        val prev = terminal.enterRawMode()
        terminal.writer().print(prompt())
        terminal.flush()
        val char = terminal.reader().read().toChar()
        terminal.attributes = prev
        terminal.writer().println()
        return when (char) {
            'b', 'B' -> return BACK
            'q', 'Q' -> return QUIT
            else -> validMenuChoices.indexOf(char) // -1 == INVALID
        }
    }

}

private val menuChoices = listOf(
    '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'c', 'd', 'e', 'f', 'g'
).toSortedSet()


fun String.toMenu(index: Int): String = this.toMenu((index + 1).toString())

fun String.toRawMenu(index: Int): String = this.toMenu(menuChoices.elementAt(index).toString())

fun String.toMenu(index: String): String = AttributedStringBuilder()
    .styled(AttributedStyle.BOLD, index.padStart(2))
    .append(": $this")
    .toAnsi()

