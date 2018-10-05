package org.strykeforce.thirdcoast

import org.jline.terminal.Terminal
import org.jline.utils.*
import org.jline.utils.AttributedString.EMPTY
import org.strykeforce.thirdcoast.command.MenuCommand

fun Terminal.info(msg: String) = this.writer().println("\n$msg")

fun Terminal.warn(msg: String) {
    val line = AttributedString("\n$msg", AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
    this.puts(InfoCmp.Capability.bell);
    this.writer().println(line.toAnsi())
}


fun Terminal.status(line: String?, style: AttributedStyle = AttributedStyle.INVERSE) {
    val lines = if (line != null) listOf(EMPTY, AttributedString(line, style)) else listOf(EMPTY, EMPTY)
    val status = Status.getStatus(this)
    status?.update(lines) ?: this.writer().println(line)
}

fun Terminal.printMenu(command: MenuCommand) {
    val writer = this.writer()

    command.children.forEachIndexed { index, cmd ->
        writer.println(cmd.menu.toMenu(index))
    }
    if (command.parent != null)
        writer.println("back to previous printMenu".toMenu("b"))
    writer.println("quit TCT".toMenu("q"))
}

fun String.toMenu(index: Int): String = this.toMenu((index + 1).toString())

fun String.toMenu(index: String): String = AttributedStringBuilder()
    .styled(AttributedStyle.BOLD, index.padStart(2))
    .append(": $this")
    .toAnsi()

