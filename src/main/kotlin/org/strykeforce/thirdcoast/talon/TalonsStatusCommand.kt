package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.LegacyTalonFxService
import org.strykeforce.thirdcoast.device.TalonService

class TalonsStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()
    private val legacyTalonFxService: LegacyTalonFxService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override fun execute(): Command {
        val writer = terminal.writer()
        if(type == "srx"){
            talonService.active.forEach {
                val config = TalonSRXConfiguration()
                it.getAllConfigs(config)
                writer.println(config.toString(it.deviceID.toString()))
            }
        } else if(type == "fx"){
            legacyTalonFxService.active.forEach {
                val config = TalonFXConfiguration()
                it.getAllConfigs(config)
                writer.println(config.toString(it.deviceID.toString()))
            }
        }
        return super.execute()
    }
}