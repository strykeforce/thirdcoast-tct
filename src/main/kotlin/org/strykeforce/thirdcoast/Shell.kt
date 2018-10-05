package org.strykeforce.thirdcoast

import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.InfoCmp
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
        terminal.puts(InfoCmp.Capability.clear_screen)
        while (true) {
            if (command is MenuCommand) {
                terminal.printMenu(command as MenuCommand)
                val choice = reader.readMenu(command)
                when (choice) {
                    INVALID -> terminal.warn("Please enter a choice from this menu")
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

}
