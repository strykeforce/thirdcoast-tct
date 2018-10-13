package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override val menu: String
        get() = formatMenu(talonService.active.map(TalonSRX::getDeviceID).joinToString())

    override fun execute(): Command {
        while (true) {
            try {
                val ids = reader.readIntList(this.prompt("ids"))
                val seen = talonService.idsInAll(ids)
                talonService.activate(ids)
                if (seen.isNotEmpty())
                    terminal.warn(
                        "reset control mode, current limit enabled, brake, voltage compensation\nand sensor phase " +
                                "for talons: ${seen.joinToString()}"
                    )
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Talon ids separated by ','")
            }
        }
    }
}