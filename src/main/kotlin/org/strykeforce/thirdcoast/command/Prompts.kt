package org.strykeforce.thirdcoast.command

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle

private const val LINE_END = "> "

fun Command.breadcrumbs(): String {
    var currentNode = this
    val promptLevels = mutableListOf<String>()
    promptLevels += currentNode.key.formatKey()
    while (currentNode.parent != null) {
        currentNode = currentNode.parent as Command
        promptLevels += currentNode.key.formatKey()
    }
    if (promptLevels.size == 1) return "" // ROOT
    return promptLevels.dropLast(1).asReversed().joinToString(separator = " : ")
}

fun Command.prompt(): String = AttributedString("${this.breadcrumbs()}$LINE_END", AttributedStyle.BOLD).toAnsi()

fun Command.prompt(parameter: String): String =
    AttributedString("${this.breadcrumbs()} : $parameter$LINE_END", AttributedStyle.BOLD).toAnsi()

fun String.formatKey() = this.replace('_', ' ')