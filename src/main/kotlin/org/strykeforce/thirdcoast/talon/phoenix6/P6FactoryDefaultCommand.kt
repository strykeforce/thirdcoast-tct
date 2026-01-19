package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.configs.TalonFXSConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService

class P6FactoryDefaultCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFdService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override fun execute(): Command {
        when(device) {
            "fx" -> {
                val config = TalonFXConfiguration()
                if(bus == "rio") {
                    talonFxService.activeConfiguration = config
                    talonFxService.active.forEach { it.talonFX.configurator.apply(config) }
                } else if(bus == "canivore") {
                    talonFxFdService.activeConfiguration = config
                    talonFxFdService.active.forEach { it.talonFX.configurator.apply(config)}
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                val config = TalonFXSConfiguration()
                if(bus == "rio") {
                    talonFxsService.activeConfiguration = config
                    talonFxsService.active.forEach { it.talonFXS.configurator.apply(config) }
                } else if(bus == "canivore") {
                    talonFxsFDService.activeConfiguration = config
                    talonFxsFDService.active.forEach { it.talonFXS.configurator.apply(config)}
                } else throw IllegalArgumentException()
            }
        }


        return super.execute()
    }

}