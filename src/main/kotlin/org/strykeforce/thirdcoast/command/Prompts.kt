package org.strykeforce.thirdcoast.command

const val PROMPT = "> "

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

fun Command.prompt() = "${this.breadcrumbs()}$PROMPT"

fun Command.prompt(parameter: String) = "${this.breadcrumbs()} : $parameter$PROMPT"