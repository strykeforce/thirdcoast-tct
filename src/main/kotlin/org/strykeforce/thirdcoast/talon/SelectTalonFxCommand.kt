package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonFX
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFXService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn
import java.lang.Exception
import java.lang.IllegalArgumentException

class SelectTalonFxCommand(parent: Command?,
                           key: String,
                           toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonFXService: TalonFXService by inject()

    override val menu: String
        get() = formatMenu(talonFXService.active.map(TalonFX::getDeviceID).joinToString())

    override fun execute(): Command {
        while (true){
            try {
                val ids = reader.readIntList(this.prompt("ids"), talonFXService.active.map(TalonFX::getDeviceID))
                val new = talonFXService.activate(ids)
                if(new.isNotEmpty()){
                    terminal.info("reset control mode, current limit enabled, brake, voltage compensation\\nand sensor phase \" +\n" +
                            "                                \"for talonfxs: ${new.joinToString()}")
                }
                return super.execute()
            } catch (e: IllegalArgumentException){
                terminal.warn("Please enter a list of TalonFx ids separated by ','")
            }
        }
    }
}