package org.strykeforce.thirdcoast.canifier

import com.ctre.phoenix.CANifierConfiguration
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CanifierService
import org.strykeforce.thirdcoast.device.TalonService

class CanifierStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val canifierService: CanifierService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        canifierService.active.forEach {
            val config = CANifierConfiguration()
            it.getAllConfigs(config)
            writer.println(config.toString(it.deviceID.toString()))
        }
        return super.execute()
    }
}