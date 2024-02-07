package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.hardware.CANcoder
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.CancoderService
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

class SelectCancoderCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {
    private  val cancoderService: CancoderService by inject()

    override val menu: String
        get() = formatMenu(cancoderService.active.map(CANcoder::getDeviceID).joinToString())

    override fun execute(): Command {
        while(true) {
            try {
                val id = reader.readInt(this.prompt("id"))
                cancoderService.activate(listOf(id))
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a CANcoder id")
            }
        }
    }
}