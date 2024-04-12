package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.hardware.TalonFX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class P6SelectTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val logger = KotlinLogging.logger {}

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val menu: String
        get() = formatMenu(
                if(bus == "rio") talonFxService.active.map(TalonFX::getDeviceID).joinToString()
                else talonFxFDService.active.map(TalonFX::getDeviceID).joinToString()
        )


    override fun execute(): Command {
        while(true){
            try {
                var ids: List<Int>
                var new: Set<Int>

                if(bus == "rio") {
                    ids = reader.readIntList(this.prompt("ids"), talonFxService.active.map(TalonFX::getDeviceID))
                    logger.info("IDS: ${ids}")
                    new = talonFxService.activate(ids)
                } else if(bus == "canivore") {
                    ids = reader.readIntList(this.prompt("ids"), talonFxFDService.active.map(TalonFX::getDeviceID))
                    logger.info("IDS: ${ids}")
                    new = talonFxFDService.activate(ids)
                } else throw IllegalArgumentException()

//                ids = reader.readIntList(this.prompt("ids"), talonFxService.active.map(TalonFX::getDeviceID))
//
//                new =
//                    if(bus == "rio") talonFxService.activate(ids)
//                    else if(bus == "canivore") talonFxFDService.activate(ids)
//                    else throw IllegalArgumentException()

                if(new.isNotEmpty()) {
                    logger.info("Activating talons: ${new.joinToString()}")
                }
                return super.execute()
            } catch(e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Talon FX ids separated by ','")
            }
        }

    }

}