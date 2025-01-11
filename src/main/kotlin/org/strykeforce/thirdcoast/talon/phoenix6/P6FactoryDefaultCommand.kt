package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.configs.TalonFXConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6FactoryDefaultCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFdService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override fun execute(): Command {
        val config = TalonFXConfiguration()
        if(bus == "rio") {
            talonFxService.activeConfiguration = config
            talonFxService.active.forEach { it.configurator.apply(config) }
        } else if(bus == "canivore") {
            talonFxFdService.activeConfiguration = config
            talonFxFdService.active.forEach { it.configurator.apply(config)}
        } else throw IllegalArgumentException()

        return super.execute()
    }

}