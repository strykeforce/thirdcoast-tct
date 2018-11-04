package org.strykeforce.thirdcoast.dio

import edu.wpi.first.wpilibj.DigitalOutput
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.DigitalOutputService
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class RunDigitalOutputsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val digitalOutputService: DigitalOutputService by inject()

    override val menu: String
        get() = formatMenu(digitalOutputService.active.map(DigitalOutput::getInt).joinToString())

    override fun execute(): Command {
        if (digitalOutputService.active.isEmpty()) {
            terminal.warn("no digital outputs selected")
            return super.execute()
        }
        var done = false

        while (!done) {
            try {
                val setpoints = mutableListOf<Int>()
                digitalOutputService.active.forEach {
                    val setpoint = reader.readInt(this.prompt("digital output ${it.channel}"), it.getInt())
                    if (!(0..1).contains(setpoint)) throw IllegalArgumentException()
                    setpoints += setpoint
                }
                digitalOutputService.active.forEachIndexed { i, digitalOutput ->
                    digitalOutput.set(setpoints[i] == 1)
                    logger.info { "set digital output ${digitalOutput.channel} to ${setpoints[i]}" }
                }
                done = true
            } catch (e: Exception) {
                terminal.warn("please enter a 0 or 1")
            }
        }

        return super.execute()
    }

}

private fun DigitalOutput.getInt() = if (this.get()) 1 else 0