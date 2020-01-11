package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFXService
import org.strykeforce.thirdcoast.warn
import java.lang.Exception

private val logger = KotlinLogging.logger {}

class RunTalonFxCommand(
        parent: Command?,
        key: String,
        toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFXService by inject()

    override fun execute(): Command {
        var done = false

        while (!done){
            try {
                val line = reader.readLine(prompt())
                if(line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(",")
                val setpoint = setpoints[0].toDouble()
                val duration = if(setpoints.size > 1) setpoints[1].toDouble() else 0.0
                val mode = talonFxService.controlMode

                //Check inputs
                if(mode == ControlMode.PercentOutput && !(-1.0..1.0).contains(setpoint)){
                    terminal.warn("setpoint must be in range -1.0 to 1.0 for percent output mode")
                    continue
                }
                if((mode == ControlMode.MotionMagic || mode == ControlMode.Position) && duration > 0.0){
                    terminal.warn("specifying a duration in position modes not allowed")
                    continue
                }

                //Run Talon FX's
                talonFxService.active.forEach{ it.set(mode, setpoint)}

                if(duration > 0.0){
                    logger.debug{ "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting output = 0.0" }
                    talonFxService.active.forEach{ it.set(mode, 0.0) }
                }
            } catch (e: Exception){
                done = true
            }
        }
        return super.execute()
    }
}