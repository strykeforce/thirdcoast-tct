package org.strykeforce.thirdcoast.servo

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.ServoService
import org.strykeforce.thirdcoast.readDouble
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class RunServosCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val servoService: ServoService by inject()

    override fun execute(): Command {
        if (servoService.active.isEmpty()) {
            terminal.warn("no servos selected")
            return super.execute()
        }
        var done = false

        while (!done) {
            try {
                val setpoints = mutableListOf<Double>()
                servoService.active.forEach {
                    val setpoint = reader.readDouble(this.prompt("servo ${it.channel}"))
                    if (!(0.0..1.0).contains(setpoint)) throw IllegalArgumentException()
                    setpoints += setpoint
                }
                servoService.active.forEachIndexed { i, servo ->
                    servo.set(setpoints[i])
                    logger.info { "set servo ${servo.channel} to ${setpoints[i]}" }
                }
                done = true
            } catch (e: Exception) {
                terminal.warn("please enter a value from 0.0 to 1.0 (full left to full right)")
            }
        }

        return super.execute()
    }

}