package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.configs.CANcoderConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderFDService
import org.strykeforce.thirdcoast.device.CancoderService

class CancoderStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val cancoderService: CancoderService by inject()
    private val cancoderFDService: CancoderFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override fun execute(): Command {
        val writer = terminal.writer()
        if(bus == "rio") {
            cancoderService.active.forEach{
                val config = CANcoderConfiguration()
                it.configurator.refresh(config)
                writer.println(config.toString())
            }
        } else if(bus == "canivore") {
            cancoderFDService.active.forEach {
                val config = CANcoderConfiguration()
                it.configurator.refresh(config)
                writer.println(config.toString())
            }
        } else throw IllegalArgumentException()
        return super.execute()
    }
}