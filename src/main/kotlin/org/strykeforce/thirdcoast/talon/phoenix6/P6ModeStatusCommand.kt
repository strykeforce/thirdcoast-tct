package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6ModeStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val menu: String
        get() {
            if(bus == "rio") return formatMenu(talonFxService.controlMode)
            else if(bus == "canivore") return formatMenu(talonFxFDService.controlMode)
            else throw  IllegalArgumentException()
        }

    override fun execute(): Command {
        //val writer = terminal.writer()
        //writer.println(talonFxService.controlMode)
        return super.execute()
    }
}