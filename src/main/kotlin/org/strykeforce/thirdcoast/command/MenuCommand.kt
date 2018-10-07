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

    private fun readMenuChoice() =
        reader.readMenu(children.size, prompt(), quit = true)


    private fun readRawMenuChoice() =
        terminal.readRawMenu(children.size, prompt(), quit = true)
}

fun String.toMenu(index: Int, highlight: Boolean = false): String =
    this.toMenu((index + 1).toString(), highlight)

fun String.toRawMenu(index: Int): String =
    this.toMenu(menuChoices.elementAt(index).toString())

fun String.toMenu(index: String, highlight: Boolean = false): String =
    AttributedStringBuilder()
        .styled(AttributedStyle.BOLD, index.padStart(2))
        .also {
            val style = if (highlight) AttributedStyle.BOLD else AttributedStyle.DEFAULT
            it.styled(style, ": $this")
        }.toAnsi()

