package org.strykeforce.thirdcoast

import mu.KotlinLogging
import org.jline.terminal.Terminal
import org.jline.utils.InfoCmp
import org.strykeforce.thirdcoast.command.Command
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class Shell(private var command: Command, private val terminal: Terminal) {

    fun run() {
        terminal.puts(InfoCmp.Capability.clear_screen)
        while (true) {
            try {
                command = command.execute()
            } catch (t: Throwable) {
                logger.error(t) { "error in command loop, aborting" }
                terminal.warn("Unrecoverable error, please report: ${t.message}")
                terminal.flush()
                exitProcess(1)
            }
        }
    }
}
