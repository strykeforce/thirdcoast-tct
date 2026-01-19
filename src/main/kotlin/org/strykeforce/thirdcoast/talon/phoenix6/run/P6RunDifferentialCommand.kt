package org.strykeforce.thirdcoast.talon.phoenix6.run

import edu.wpi.first.wpilibj.Timer
import net.consensys.cava.toml.TomlTable
import org.strykeforce.controller.CTRE_ClosedLoopType
import org.strykeforce.controller.CTRE_DifferentialType
import org.strykeforce.controller.MotionMagicType
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.*
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.warn
import mu.KotlinLogging
import org.jline.reader.EndOfFileException

private val logger = KotlinLogging.logger{}
class P6RunDifferentialCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override fun execute(): Command {
        var done = false
        while(!done) {
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val average = setpoints[0].toDouble()
                val offset = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                val duration = if(setpoints.size > 2) setpoints[2].toDouble() else 0.0
                var type: CTRE_ClosedLoopType
                var diffType: CTRE_DifferentialType = CTRE_DifferentialType.Open_Loop

                when (device) {
                    "fx" -> {
                        type =
                            if (bus == "rio") talonFxService.active.firstOrNull()?.getClosedLoopType() ?: CTRE_ClosedLoopType.Velocity else talonFxFDService.active.firstOrNull()?.getClosedLoopType() ?: CTRE_ClosedLoopType.Velocity
                        diffType =
                            if (bus == "rio") talonFxService.active.firstOrNull()?.getDifferentialType()?: CTRE_DifferentialType.Open_Loop else talonFxFDService.active.firstOrNull()?.getDifferentialType()?: CTRE_DifferentialType.Open_Loop
                    }

                    "fxs" -> {
                        type =
                            if (bus == "rio") talonFxsService.active.firstOrNull()?.getClosedLoopType() ?: CTRE_ClosedLoopType.Velocity else talonFxsFDService.active.firstOrNull()?.getClosedLoopType() ?: CTRE_ClosedLoopType.Velocity
                        diffType =
                            if (bus == "rio") talonFxsService.active.firstOrNull()?.getDifferentialType()?: CTRE_DifferentialType.Open_Loop else talonFxsFDService.active.firstOrNull()?.getDifferentialType()?: CTRE_DifferentialType.Open_Loop
                    }
                }

                //sanity checks
                if(diffType != CTRE_DifferentialType.Velocity && duration > 0.0) {
                    terminal.warn("specifying a duration in popsition modes is not allowed")
                    continue
                }

                logger.info {"Average: $average, Offset: $offset, Duration: $duration"}
                //run talon
                when (device) {
                    "fx" -> {
                        if (bus == "rio") {
                            talonFxService.active.forEach { it.runDifferential(average, offset) }
                        } else if (bus == "canivore") {
                            talonFxFDService.active.forEach { it.runClosedLoop(average, offset) }
                        } else throw IllegalArgumentException()
                    }

                    "fxs" -> {
                        if (bus == "rio") {
                            talonFxsService.active.forEach { it.runClosedLoop(average, offset) }
                        } else if (bus == "canivore") {
                            talonFxsFDService.active.forEach { it.runClosedLoop(average, offset) }
                        } else throw IllegalArgumentException()
                    }
                }

                //check timeout
                if (duration > 0.0) {
                    logger.debug { "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting to 0.0" }
                    when (device) {
                        "fx" -> {
                            if (bus == "rio") {
                                talonFxService.active.forEach { it.runOpenLoop(0.0) }
                            } else if (bus == "canivore") {
                                talonFxFDService.active.forEach { it.runOpenLoop(0.0) }
                            } else throw IllegalArgumentException()
                        }

                        "fxs" -> {
                            if (bus == "rio") {
                                talonFxsService.active.forEach { it.runOpenLoop(0.0) }
                            } else if (bus == "canivore") {
                                talonFxsFDService.active.forEach { it.runOpenLoop(0.0) }
                            } else throw IllegalArgumentException()
                        }
                    }
                }

            } catch (e: Exception) {
                done = true
            }
        }
        return super.execute()
    }
}