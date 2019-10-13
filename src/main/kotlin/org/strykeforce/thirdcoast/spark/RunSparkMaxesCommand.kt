package org.strykeforce.thirdcoast.spark

import com.ctre.phoenix.motorcontrol.ControlMode
import com.revrobotics.ControlType
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.SparkMaxService
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class RunSparkMaxesCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val sparkMaxService: SparkMaxService by inject()

    override fun execute(): Command {
        var done = false

        while (!done) {
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val setpoint = setpoints[0].toDouble()
                val duration = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                val mode = sparkMaxService.controlType

                // sanity checks
                if (mode == ControlType.kDutyCycle && !(-1.0..1.0).contains(setpoint)){
                    terminal.warn("setpoint must be in range -1.0 to 1.0 for percent output mode")
                    continue
                }

                if ((mode == ControlType.kSmartMotion || mode == ControlType.kPosition) && duration > 0.0) {
                    terminal.warn("specifying a duration in position modes not allowed")
                    continue
                }

                // run the spark max
                sparkMaxService.active.forEach {
                    if(sparkMaxService.controlType == ControlType.kDutyCycle) it.set(setpoint)
                    else it.pidController.setReference(setpoint,sparkMaxService.controlType)
                    logger.debug { "Run Spark Max ${it.deviceId} at $setpoint, running at ${it.appliedOutput}, error: ${it.lastError}" }
                }

                //sparkMaxService.active.forEach{ it.pidController.setReference(setpoint,sparkMaxService.controlType,sparkMaxService.activeSlot)}

                if (duration > 0.0) {
                    logger.debug { "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting output = 0.0" }
                    sparkMaxService.active.forEach { it.set(0.0) }
                }

            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }

}