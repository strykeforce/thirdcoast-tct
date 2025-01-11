package org.strykeforce.thirdcoast.solenoid

import edu.wpi.first.wpilibj.Servo
import edu.wpi.first.wpilibj.Solenoid
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.ServoService
import org.strykeforce.thirdcoast.device.SolenoidService
import org.strykeforce.thirdcoast.readBoolean
import org.strykeforce.thirdcoast.readDouble
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class RunSolenoidsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val solenoidService: SolenoidService by inject()

    override val menu: String
        get() = formatMenu(solenoidService.active.map(Solenoid::get).joinToString())

    override fun execute(): Command {
        if (solenoidService.active.isEmpty()) {
            terminal.warn("no solenoids selected")
            return super.execute()
        }
        var done = false

        while (!done) {
            try {
                val setpoints = mutableListOf<Boolean>()
                solenoidService.active.forEach {
                    val setpoint = reader.readBoolean(this.prompt("solenoid ${it.port()} (y/n)"), it.get())
                    setpoints += setpoint
                }
                solenoidService.active.forEachIndexed { i, solenoid ->
                    solenoid.set(setpoints[i])
                    logger.info { "set solenoid ${solenoid.port()} to ${setpoints[i]}" }
                }
                done = true
            } catch (e: Exception) {
                terminal.warn("please enter a y/n value)")
            }
        }

        return super.execute()
    }

}