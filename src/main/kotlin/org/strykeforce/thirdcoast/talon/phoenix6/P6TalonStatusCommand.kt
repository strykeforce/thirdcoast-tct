package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.configs.TalonFXConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6TalonStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override fun execute(): Command {
        val writer = terminal.writer()
        if(bus == "rio") {
            talonFxService.active.forEach {
                var config = TalonFXConfiguration()
                it.configurator.refresh(config)
                writer.println(config.toString())
            }
        }else if(bus == "canivore") {
            talonFxFDService.active.forEach {
                var config = TalonFXConfiguration()
                it.configurator.refresh(config)
                writer.println(config.toString())
            }
        }else throw IllegalArgumentException()
        return super.execute()
    }
}