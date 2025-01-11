package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.hardware.CANcoder
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.CancoderFDService
import org.strykeforce.thirdcoast.device.CancoderService
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

class SelectCancoderCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {
    private  val cancoderService: CancoderService by inject()
    private val cancoderFDService: CancoderFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override val menu: String
        get() = formatMenu(
            if(bus == "rio") cancoderService.active.map(CANcoder::getDeviceID).joinToString()
            else cancoderFDService.active.map(CANcoder::getDeviceID).joinToString()
        )

    override fun execute(): Command {
        while(true) {
            try {
                val id = reader.readInt(this.prompt("id"))
                if(bus == "rio") {
                    cancoderService.activate(listOf(id))
                } else if(bus == "canivore") {
                    cancoderFDService.activate(listOf(id))
                } else throw  IllegalArgumentException()
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a CANcoder id")
            }
        }
    }
}