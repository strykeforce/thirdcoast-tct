package org.strykeforce.thirdcoast.dio

import edu.wpi.first.wpilibj.DigitalOutput
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.DigitalOutputService
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectDigitalOutputsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val digitalOutputService: DigitalOutputService by inject()

    override val menu: String
        get() = formatMenu(digitalOutputService.active.map(DigitalOutput::getChannel).joinToString())

    override fun execute(): Command {
        while (true) {
            try {
                val channels = reader.readIntList(this.prompt("channels"))
                digitalOutputService.activate(channels)
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Digital Output channels separated by ','")
            }
        }
    }
}