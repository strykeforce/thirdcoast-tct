package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.BACK
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.command.toMenu
import org.strykeforce.thirdcoast.readMenu
import org.strykeforce.thirdcoast.warn

class RunTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        val writer = terminal.writer()

        val done = false
        while (!done) {

        }

        return super.execute()
    }

}