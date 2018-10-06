package org.strykeforce.thirdcoast

import org.jline.terminal.Terminal
import org.jline.utils.InfoCmp
import org.strykeforce.thirdcoast.command.Command

//    private val logger = KotlinLogging.logger {}

class Shell(private var command: Command, private val terminal: Terminal) {

    fun run() {
        terminal.puts(InfoCmp.Capability.clear_screen)
        while (true) {
            command = command.execute()
        }
    }
}
