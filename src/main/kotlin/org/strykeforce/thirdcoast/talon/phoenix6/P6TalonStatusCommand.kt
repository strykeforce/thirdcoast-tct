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

class P6TalonStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override fun execute(): Command {
        val writer = terminal.writer()
        if(device == "fx") {
            if(bus == "rio") {
                talonFxService.active.forEach {
                    var config = TalonFXConfiguration()
                    it.talonFX.configurator.refresh(config)
                    writer.println(config.toString())
                }
            }else if(bus == "canivore") {
                talonFxFDService.active.forEach {
                    var config = TalonFXConfiguration()
                    it.talonFX.configurator.refresh(config)
                    writer.println(config.toString())
                }
            }else throw IllegalArgumentException()
        } else if(device == "fxs") {
            if(bus == "rio") {
                talonFxsService.active.forEach {
                    var config = TalonFXSConfiguration()
                    it.talonFXS.configurator.refresh(config)
                    writer.println(config.toString())
                }
            }else if(bus == "canivore") {
                talonFxsFDService.active.forEach {
                    var config = TalonFXSConfiguration()
                    it.talonFXS.configurator.refresh(config)
                    writer.println(config.toString())
                }
            }else throw IllegalArgumentException()
        }

        return super.execute()
    }
}