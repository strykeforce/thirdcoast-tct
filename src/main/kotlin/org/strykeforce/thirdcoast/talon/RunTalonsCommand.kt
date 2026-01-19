package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.ControlMode.*
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.warn
import java.lang.IllegalArgumentException

private val logger = KotlinLogging.logger {}

class RunTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    private val talonService: TalonService by inject()


    override fun execute(): Command {
        var done = false

        while (!done) {
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val setpoint = setpoints[0].toDouble()
                val duration = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                val mode: ControlMode

                mode = talonService.controlMode

                // sanity checks
                if (mode == PercentOutput && !(-1.0..1.0).contains(setpoint)) {
                    terminal.warn("setpoint must be in range -1.0 to 1.0 for percent output mode")
                    continue
                }

                if ((mode == MotionMagic || mode == Position) && duration > 0.0) {
                    terminal.warn("specifying a duration in position modes not allowed")
                    continue
                }

                // run the talons
                talonService.active.forEach { it.set(mode, setpoint) }

                if (duration > 0.0) {
                    logger.debug { "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting output = 0.0" }
                    talonService.active.forEach { it.set(mode, 0.0) }
                }
            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }
}