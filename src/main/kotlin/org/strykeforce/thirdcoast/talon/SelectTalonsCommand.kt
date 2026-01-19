package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val talonService: TalonService by inject()

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val menu: String
        get() {
            return formatMenu(talonService.active.map(TalonSRX::getDeviceID).joinToString())
        }

    override fun execute(): Command {
        while (true) {
            try {
                var ids: List<Int>
                var new: Set<Int>

                ids = reader.readIntList(this.prompt("ids"), talonService.active.map(TalonSRX::getDeviceID))
                new = talonService.activate(ids)

                if (new.isNotEmpty())
                    terminal.info(
                        "reset control mode, current limit enabled, brake, voltage compensation\nand sensor phase " +
                                "for talons: ${new.joinToString()}"
                    )
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Talon ids separated by ','")
            }
        }
    }
}