package org.strykeforce.thirdcoast.canifier

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.CanifierService
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class RunCanifierPwmCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val canifierService: CanifierService by inject()

    override fun execute(): Command {
        while (true) {
            try {
                val line = reader.readLine(prompt("channel, duty cycle"))
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')

                if (setpoints.size != 2) throw IllegalArgumentException()


                val channel = setpoints[0].toInt()
                if (!(0..2).contains(channel)) throw IllegalArgumentException("PWM output valid for channels 0, 1, and 2 only")


                val dutyCycle = setpoints[1].toDouble()
                if (!(0.0..1.0).contains(dutyCycle)) throw IllegalArgumentException("duty cycle must be in range 0.0 - 1.0")

                canifierService.active.forEach { it.enablePWMOutput(channel, true) }
                canifierService.active.forEach { it.setPWMOutput(channel, dutyCycle) }
                logger.debug { "set PWM channel $channel to duty cycle $dutyCycle" }
                break
            } catch (e: Exception) {
                terminal.warn(e.message ?: "please enter PWM channel and duty cycle separated by ','")
            }
        }

        return super.execute()
    }
}