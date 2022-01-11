package org.strykeforce.thirdcoast.solenoid

import edu.wpi.first.wpilibj.Solenoid
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.SolenoidService
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectSolenoidsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val solenoidService: SolenoidService by inject()

    override val menu: String
        get() = formatMenu(solenoidService.active.joinToString(transform = Solenoid::port))

    override fun execute(): Command {
        while (true) {
            try {
                val ports = reader.readIntList(this.prompt("ports"))
                solenoidService.activate(ports)
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Solenoid ports separated by ','")
            }
        }
    }
}

internal fun Solenoid.port() = this.channel.toString()