package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.configs.TalonFXConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

class P6TalonStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        talonFxService.active.forEach {
            var config = TalonFXConfiguration()
            it.configurator.refresh(config)
            writer.println(config.toString())
        }
        return super.execute()
    }
}