package org.strykeforce.thirdcoast.talon.phoenix6.run

import edu.wpi.first.wpilibj.Timer
import net.consensys.cava.toml.TomlTable
import org.strykeforce.controller.CTRE_ClosedLoopType
import org.strykeforce.controller.CTRE_Units
import org.strykeforce.controller.MotionMagicType
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.warn
import mu.KotlinLogging
import org.jline.reader.EndOfFileException

private val logger = KotlinLogging.logger{}

class P6RunClosedLoopCommand(
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
        var done = false;
        while(!done) {
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val setpoint = setpoints[0].toDouble()
                val duration = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                var type: CTRE_ClosedLoopType = CTRE_ClosedLoopType.Velocity
                var mmType: MotionMagicType = MotionMagicType.Standard

                when (device) {
                    "fx" -> {
                        type =
                            if (bus == "rio") talonFxService.active.firstOrNull()?.getClosedLoopType()?: CTRE_ClosedLoopType.Velocity else talonFxFDService.active.firstOrNull()?.getClosedLoopType() ?: CTRE_ClosedLoopType.Velocity
                        mmType =
                            if (bus == "rio") talonFxService.active.firstOrNull()?.getMotionMagicType()?: MotionMagicType.Standard else talonFxFDService.active.firstOrNull()?.getMotionMagicType() ?: MotionMagicType.Standard
                    }

                    "fxs" -> {
                        type =
                            if (bus == "rio") talonFxsService.active.firstOrNull()?.getClosedLoopType()?: CTRE_ClosedLoopType.Velocity else talonFxsFDService.active.firstOrNull()?.getClosedLoopType() ?: CTRE_ClosedLoopType.Velocity
                        mmType =
                            if (bus == "rio") talonFxsService.active.firstOrNull()?.getMotionMagicType()?: MotionMagicType.Standard else talonFxsFDService.active.firstOrNull()?.getMotionMagicType() ?: MotionMagicType.Standard
                    }
                }

                //sanity checks
                if (((type == CTRE_ClosedLoopType.Motion_Magic && mmType != MotionMagicType.Velocity) || type == CTRE_ClosedLoopType.Position) && duration > 0.0) {
                    terminal.warn("specifying a duration in position modes is not allowed")
                    continue
                }

                logger.info { "Setpoint: $setpoint" }
                //run talon
                when (device) {
                    "fx" -> {
                        if (bus == "rio") {
                            talonFxService.active.forEach { it.runClosedLoop(setpoint) }
                        } else if (bus == "canivore") {
                            talonFxFDService.active.forEach { it.runClosedLoop(setpoint) }
                        } else throw IllegalArgumentException()
                    }

                    "fxs" -> {
                        if (bus == "rio") {
                            talonFxsService.active.forEach { it.runClosedLoop(setpoint) }
                        } else if (bus == "canivore") {
                            talonFxsFDService.active.forEach { it.runClosedLoop(setpoint) }
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
            } catch(e: Exception) {
                done = true
            }
        }
        return super.execute()
    }
}