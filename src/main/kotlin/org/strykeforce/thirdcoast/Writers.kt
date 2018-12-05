package org.strykeforce.thirdcoast

import org.jline.terminal.Terminal
import org.jline.utils.AttributedString
import org.jline.utils.AttributedString.EMPTY
import org.jline.utils.AttributedStyle
import org.jline.utils.InfoCmp
import org.jline.utils.Status

internal fun Terminal.info(msg: String) = this.writer().println("\n$msg")

internal fun Terminal.warn(msg: String) {
    val line = AttributedString("\n$msg", AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
    this.puts(InfoCmp.Capability.bell);
    this.writer().println(line.toAnsi())
}

internal fun Terminal.invalidMenuChoice() = this.warn("Please select from this menu or <ENTER> to cancel")


internal fun Terminal.status(line: String?, style: AttributedStyle = AttributedStyle.INVERSE) {
    val lines = if (line != null) listOf(EMPTY, AttributedString(line, style)) else listOf(EMPTY, EMPTY)
    val status = Status.getStatus(this)
    status?.update(lines) ?: this.writer().println(line)
}

internal fun greedyWordwrap(text: String, lineWidth: Int = 72): String {
    val words = text.split(' ')
    val sb = StringBuilder(words[0])
    var spaceLeft = lineWidth - words[0].length
    for (word in words.drop(1)) {
        val len = word.length
        if (len + 1 > spaceLeft) {
            sb.append("\n").append(word)
            spaceLeft = lineWidth - len
        }
        else {
            sb.append(" ").append(word)
            spaceLeft -= (len + 1)
        }
    }
    return sb.toString()
}

internal fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)