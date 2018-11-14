package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic
import com.ctre.phoenix.motorcontrol.ControlMode.Position
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class RunTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

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

                talonService.active.forEach { it.set(talonService.controlMode, setpoint) }

                if (duration > 0.0 && (talonService.controlMode == MotionMagic || talonService.controlMode == Position)) {
                    terminal.warn("specifying a duration in closed-loop position mode not allowed")
                    done = true
                }

                if (duration > 0.0) {
                    logger.debug { "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting output = 0.0" }
                    talonService.active.forEach { it.set(talonService.controlMode, 0.0) }
                }
            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }
}