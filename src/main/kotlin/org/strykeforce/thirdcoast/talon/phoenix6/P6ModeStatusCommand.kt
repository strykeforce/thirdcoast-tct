package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

class P6ModeStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()

    override val menu: String
        get() = formatMenu(talonFxService.controlMode)

    override fun execute(): Command {
        //val writer = terminal.writer()
        //writer.println(talonFxService.controlMode)
        return super.execute()
    }
}