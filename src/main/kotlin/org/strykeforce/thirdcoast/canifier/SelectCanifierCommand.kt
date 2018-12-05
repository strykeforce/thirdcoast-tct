package org.strykeforce.thirdcoast.canifier

import com.ctre.phoenix.CANifier
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.CanifierService
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

class SelectCanifierCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val canifierService: CanifierService by inject()

    override val menu: String
        get() = formatMenu(canifierService.active.map(CANifier::getDeviceID).joinToString())

    override fun execute(): Command {
        while (true) {
            try {
                val id = reader.readInt(this.prompt("id"))
                canifierService.activate(listOf(id))
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a Canifier id")
            }
        }
    }
}