package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        while (true) {
            try {
                val ids = reader.readIntList(this.prompt("ids"))
                talonService.activate(ids)
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Talon ids separated by ','")
            }
        }
    }
}