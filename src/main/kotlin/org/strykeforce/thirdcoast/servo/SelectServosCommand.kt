package org.strykeforce.thirdcoast.servo

import edu.wpi.first.wpilibj.Servo
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.ServoService
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectServosCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val servoService: ServoService by inject()

    override val menu: String
        get() = formatMenu(servoService.active.map(Servo::getChannel).joinToString())

    override fun execute(): Command {
        while (true) {
            try {
                val channels = reader.readIntList(this.prompt("channels"))
                servoService.activate(channels)
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Servo channels separated by ','")
            }
        }
    }
}