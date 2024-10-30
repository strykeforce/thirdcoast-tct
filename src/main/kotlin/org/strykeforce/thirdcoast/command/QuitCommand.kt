package org.strykeforce.thirdcoast.command

import org.jline.terminal.Terminal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.system.exitProcess

class QuitCommand : Command, KoinComponent {

    override val key = "quit"
    override val parent: Command? = null
    override val menu = "Quit"
    override val order = 0
    override val children: List<Command> = emptyList()

    val terminal: Terminal by inject()

    override fun execute(): Command {
        val writer = terminal.writer()

        writer.println("bye")
        writer.flush()
        terminal.close()
        exitProcess(0)
    }
}