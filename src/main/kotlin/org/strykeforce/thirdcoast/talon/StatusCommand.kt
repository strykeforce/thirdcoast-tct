package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command

class StatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        val writer = terminal.writer()

        talonService.active.forEach {
            val config = TalonSRXConfiguration()
            it.getAllConfigs(config)
            writer.println(config.toString(it.deviceID.toString()))
        }
        return super.execute()
    }
}