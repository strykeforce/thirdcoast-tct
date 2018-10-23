package org.strykeforce.thirdcoast

import org.jline.terminal.Terminal
import org.jline.utils.AttributedString
import org.jline.utils.AttributedString.EMPTY
import org.jline.utils.AttributedStyle
import org.jline.utils.InfoCmp
import org.jline.utils.Status

fun Terminal.info(msg: String) = this.writer().println("\n$msg")

fun Terminal.warn(msg: String) {
    val line = AttributedString("\n$msg", AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
    this.puts(InfoCmp.Capability.bell);
    this.writer().println(line.toAnsi())
}

fun Terminal.invalidMenuChoice() = this.warn("Please select from this menu or <ENTER> to cancel")


fun Terminal.status(line: String?, style: AttributedStyle = AttributedStyle.INVERSE) {
    val lines = if (line != null) listOf(EMPTY, AttributedString(line, style)) else listOf(EMPTY, EMPTY)
    val status = Status.getStatus(this)
    status?.update(lines) ?: this.writer().println(line)
}
