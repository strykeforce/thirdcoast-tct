package org.strykeforce.thirdcoast.talon

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.BACK
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.command.toMenu
import org.strykeforce.thirdcoast.readMenu
import org.strykeforce.thirdcoast.readRawMenu
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        val writer = terminal.writer()
        var done = false
        while (!done) {
            for (slot in 0..3) {
                val active = slot == talonService.activeSlotIndex
                writer.println("Slot $slot".toMenu(slot, active))
            }
            val choice = terminal.readRawMenu(4, prompt("slot"))
            when (choice) {
                in 0..3 -> {
                    talonService.activeSlotIndex = choice
                    done = true
                    logger.info { "active slot ${talonService.activeSlotIndex}" }
                }
                BACK -> return super.execute()
                else -> terminal.warn("Please enter a valid slot")
            }

        }
        return super.execute()
    }

    private fun readSlotMenu() = reader.let {
        val default = (talonService.activeSlotIndex + 1).toString()
        it.readMenu(4, prompt("slot"), default, quit = false)
    }
}