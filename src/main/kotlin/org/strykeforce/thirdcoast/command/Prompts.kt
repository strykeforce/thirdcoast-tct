package org.strykeforce.thirdcoast.command

private const val LINE_END = "> "

fun Command.breadcrumbs(): String {
    var currentNode = this
    val promptLevels = kotlin.collections.mutableListOf<String>()
    promptLevels += currentNode.key
    while (currentNode.parent != null) {
        currentNode = currentNode.parent as Command
        promptLevels += currentNode.key
    }
    if (promptLevels.size == 1) return "" // ROOT
    return promptLevels.dropLast(1).asReversed().joinToString(separator = " : ")
}

fun Command.prompt() = "${this.breadcrumbs()}$LINE_END"

fun Command.prompt(parameter: String) = "${this.breadcrumbs()} : $parameter$LINE_END"