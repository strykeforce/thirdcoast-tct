package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.configs.CANcoderConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderService

class CancoderStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val cancoderService: CancoderService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        cancoderService.active.forEach{
            val config = CANcoderConfiguration()
            it.configurator.refresh(config)
            writer.println(config.toString())
        }
        return super.execute()
    }
}