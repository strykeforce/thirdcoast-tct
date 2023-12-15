package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.hardware.TalonFX
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class P6SelectTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()

    override val menu: String
        get() = formatMenu(talonFxService.active.map(TalonFX::getDeviceID).joinToString())

    override fun execute(): Command {
        while(true){
            try {
                var ids: List<Int>
                var new: Set<Int>

                ids = reader.readIntList(this.prompt("ids"), talonFxService.active.map(TalonFX::getDeviceID))
                new = talonFxService.activate(ids)

                if(new.isNotEmpty()) {
                    terminal.info("Activating talons: ${new.joinToString()}")
                }
                return super.execute()
            } catch(e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Talon FX ids separated by ','")
            }
        }

    }

}