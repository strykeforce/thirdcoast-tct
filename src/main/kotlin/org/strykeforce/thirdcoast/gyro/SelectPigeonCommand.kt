package org.strykeforce.thirdcoast.gyro

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.sensors.PigeonIMU
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.PigeonService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectPigeonCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val pigeonService: PigeonService by inject()

    override val menu: String
        get() = formatMenu(pigeonService.active.map(PigeonIMU::getDeviceID).joinToString())

    override fun execute(): Command {
        while(true){
            try {
                var ids: List<Int>
                var new: Set<Int>

                ids = reader.readIntList(this.prompt("ids"), pigeonService.active.map(PigeonIMU::getDeviceID))
                new = pigeonService.activate(ids)
                if (new.isNotEmpty())
                    terminal.info(
                        "reset temp compensation and compass declination for pigeon: ${new.joinToString()}"
                    )
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Pigeon ids separated by ','")
            }

        }
    }
}