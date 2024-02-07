package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.controls.CoastOut
import com.ctre.phoenix6.controls.ControlRequest
import com.ctre.phoenix6.controls.DifferentialDutyCycle
import com.ctre.phoenix6.controls.DifferentialFollower
import com.ctre.phoenix6.controls.DifferentialMotionMagicDutyCycle
import com.ctre.phoenix6.controls.DifferentialMotionMagicVoltage
import com.ctre.phoenix6.controls.DifferentialPositionDutyCycle
import com.ctre.phoenix6.controls.DifferentialPositionVoltage
import com.ctre.phoenix6.controls.DifferentialStrictFollower
import com.ctre.phoenix6.controls.DifferentialVelocityDutyCycle
import com.ctre.phoenix6.controls.DifferentialVelocityVoltage
import com.ctre.phoenix6.controls.DifferentialVoltage
import com.ctre.phoenix6.controls.DutyCycleOut
import com.ctre.phoenix6.controls.DynamicMotionMagicDutyCycle
import com.ctre.phoenix6.controls.DynamicMotionMagicTorqueCurrentFOC
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage
import com.ctre.phoenix6.controls.Follower
import com.ctre.phoenix6.controls.MotionMagicDutyCycle
import com.ctre.phoenix6.controls.MotionMagicExpoDutyCycle
import com.ctre.phoenix6.controls.MotionMagicExpoTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage
import com.ctre.phoenix6.controls.MotionMagicTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicVelocityDutyCycle
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage
import com.ctre.phoenix6.controls.MotionMagicVoltage
import com.ctre.phoenix6.controls.MusicTone
import com.ctre.phoenix6.controls.PositionDutyCycle
import com.ctre.phoenix6.controls.PositionTorqueCurrentFOC
import com.ctre.phoenix6.controls.PositionVoltage
import com.ctre.phoenix6.controls.StaticBrake
import com.ctre.phoenix6.controls.StrictFollower
import com.ctre.phoenix6.controls.TorqueCurrentFOC
import com.ctre.phoenix6.controls.VelocityDutyCycle
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC
import com.ctre.phoenix6.controls.VelocityVoltage
import com.ctre.phoenix6.controls.VoltageOut
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.*
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger{}

class P6RunCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()

    override fun execute(): Command {
        var done = false
        while(!done){
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val setpoint = setpoints[0].toDouble()
                val duration = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                val setpointType = talonFxService.setpointType
                val units = talonFxService.activeUnits
                val motionType = talonFxService.active_MM_type
                val differentialType = talonFxService.differentialType
                val followerType = talonFxService.activeFollowerType
                val enableFOC = talonFxService.activeFOC
                val overrideNeutral = talonFxService.activeOverrideNeutral
                val limFwdMotion = talonFxService.limFwdMotion
                val limRevMotion = talonFxService.limRevMotion
                val velocity = talonFxService.activeVelocity
                val acceleration = talonFxService.activeAcceleration
                val jerk = talonFxService.activeJerk
                val feedFwd = talonFxService.activeFeedForward
                val slot = talonFxService.activeSlotIndex
                val diffSlot = talonFxService.activeDifferentialSlot
                val diffPos = talonFxService.activeDifferentialTarget
                var controlRequest: ControlRequest = DutyCycleOut(0.0, false, false,limFwdMotion,limRevMotion)

                //sanity checks
                if (units == Units.PERCENT && !(-1.0..1.0).contains(setpoint)) {
                    terminal.warn("setpoint must be in range -1.0 to 1.0 for percent modes")
                    continue
                }

                if (((setpointType == SetpointType.MOTION_MAGIC && motionType != MM_Type.VELOCITY) || setpointType == SetpointType.POSITION) && duration > 0.0) {
                    terminal.warn("specifying a duration in position modes not allowed")
                    continue
                }

                logger.info { "Setpoint: $setpoint" }

                //make control request
                when (setpointType) {
                    SetpointType.OPEN_LOOP -> {
                        when (units) {
                            Units.PERCENT -> controlRequest = DutyCycleOut(setpoint, enableFOC, overrideNeutral,limFwdMotion, limRevMotion)
                            Units.VOLTAGE -> controlRequest = VoltageOut(setpoint, enableFOC, overrideNeutral, limFwdMotion, limRevMotion)
                            Units.TORQUE_CURRENT -> controlRequest = TorqueCurrentFOC(
                                setpoint,
                                talonFxService.activeTorqueCurrentMaxOut,
                                talonFxService.activeTorqueCurrentDeadband,
                                overrideNeutral,limFwdMotion,limRevMotion
                            )
                        }
                    }

                    SetpointType.POSITION -> {
                        when (units) {
                            Units.PERCENT -> controlRequest =
                                PositionDutyCycle(setpoint, velocity, enableFOC, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)

                            Units.VOLTAGE -> controlRequest =
                                PositionVoltage(setpoint, velocity, enableFOC, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)

                            Units.TORQUE_CURRENT -> controlRequest =
                                PositionTorqueCurrentFOC(setpoint, velocity, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)
                        }
                    }

                    SetpointType.VELOCITY -> {
                        when (units) {
                            Units.PERCENT -> controlRequest =
                                VelocityDutyCycle(setpoint, acceleration, enableFOC, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)

                            Units.VOLTAGE -> controlRequest =
                                VelocityVoltage(setpoint, acceleration, enableFOC, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)

                            Units.TORQUE_CURRENT -> controlRequest =
                                VelocityTorqueCurrentFOC(setpoint, acceleration, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)
                        }
                    }

                    SetpointType.MOTION_MAGIC -> {
                        when (motionType) {
                            MM_Type.STANDARD -> {
                                logger.info { "Motion Magic: $motionType, $units" }
                                when (units) {
                                    Units.PERCENT -> controlRequest =
                                        MotionMagicDutyCycle(setpoint, enableFOC, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)

                                    Units.VOLTAGE -> controlRequest =
                                        MotionMagicVoltage(setpoint, enableFOC, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)

                                    Units.TORQUE_CURRENT -> controlRequest =
                                        MotionMagicTorqueCurrentFOC(setpoint, feedFwd, slot, overrideNeutral,limFwdMotion,limRevMotion)
                                }
                            }

                            MM_Type.VELOCITY -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = MotionMagicVelocityDutyCycle(
                                        setpoint,
                                        acceleration,
                                        enableFOC,
                                        feedFwd,
                                        slot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.VOLTAGE -> controlRequest = MotionMagicVelocityVoltage(
                                        setpoint,
                                        acceleration,
                                        enableFOC,
                                        feedFwd,
                                        slot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.TORQUE_CURRENT -> controlRequest = MotionMagicVelocityTorqueCurrentFOC(
                                        setpoint,
                                        acceleration,
                                        enableFOC,
                                        feedFwd,
                                        slot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )
                                }
                            }

                            MM_Type.DYNAMIC -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DynamicMotionMagicDutyCycle(
                                        setpoint,
                                        velocity,
                                        acceleration,
                                        jerk,
                                        enableFOC,
                                        feedFwd,
                                        slot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.VOLTAGE -> controlRequest = DynamicMotionMagicVoltage(
                                        setpoint,
                                        velocity,
                                        acceleration,
                                        jerk,
                                        enableFOC,
                                        feedFwd,
                                        slot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.TORQUE_CURRENT -> controlRequest = DynamicMotionMagicTorqueCurrentFOC(
                                        setpoint,
                                        velocity,
                                        acceleration,
                                        jerk,
                                        feedFwd,
                                        slot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )
                                }
                            }
                            MM_Type.EXPONENTIAL -> {
                                when(units) {
                                    Units.PERCENT -> controlRequest = MotionMagicExpoDutyCycle(setpoint, enableFOC, feedFwd, slot, overrideNeutral, limFwdMotion, limRevMotion)
                                    Units.VOLTAGE -> controlRequest = MotionMagicExpoVoltage(setpoint, enableFOC, feedFwd, slot, overrideNeutral, limFwdMotion, limRevMotion)
                                    Units.TORQUE_CURRENT -> controlRequest = MotionMagicExpoTorqueCurrentFOC(setpoint, feedFwd, slot, overrideNeutral, limFwdMotion, limRevMotion)
                                }
                            }
                        }
                    }

                    SetpointType.DIFFERENTIAL -> {
                        when (differentialType) {
                            DifferentialType.OPEN_LOOP -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest =
                                        DifferentialDutyCycle(setpoint, diffPos, enableFOC, diffSlot, overrideNeutral,limFwdMotion,limRevMotion)

                                    Units.VOLTAGE -> controlRequest =
                                        DifferentialVoltage(setpoint, diffPos, enableFOC, diffSlot, overrideNeutral,limFwdMotion,limRevMotion)

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.POSITION -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DifferentialPositionDutyCycle(
                                        setpoint,
                                        diffPos,
                                        enableFOC,
                                        slot,
                                        diffSlot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.VOLTAGE -> controlRequest = DifferentialPositionVoltage(
                                        setpoint,
                                        diffPos,
                                        enableFOC,
                                        slot,
                                        diffSlot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.VELOCITY -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DifferentialVelocityDutyCycle(
                                        setpoint,
                                        diffPos,
                                        enableFOC,
                                        slot,
                                        diffSlot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.VOLTAGE -> controlRequest = DifferentialVelocityVoltage(
                                        setpoint,
                                        diffPos,
                                        enableFOC,
                                        slot,
                                        diffSlot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.MOTION_MAGIC -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DifferentialMotionMagicDutyCycle(
                                        setpoint,
                                        diffPos,
                                        enableFOC,
                                        slot,
                                        diffSlot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    Units.VOLTAGE -> controlRequest = DifferentialMotionMagicVoltage(
                                        setpoint,
                                        diffPos,
                                        enableFOC,
                                        slot,
                                        diffSlot,
                                        overrideNeutral,limFwdMotion,limRevMotion
                                    )

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.FOLLOWER -> {
                                when (followerType) {
                                    FollowerType.STANDARD -> controlRequest =
                                        DifferentialFollower(setpoint.toInt(), talonFxService.activeOpposeMain)

                                    FollowerType.STRICT -> controlRequest = DifferentialStrictFollower(setpoint.toInt())
                                }
                            }

                        }

                    }

                    SetpointType.FOLLOWER -> {
                        when (followerType) {
                            FollowerType.STANDARD -> controlRequest =
                                Follower(setpoint.toInt(), talonFxService.activeOpposeMain)

                            FollowerType.STRICT -> controlRequest = StrictFollower(setpoint.toInt())
                        }
                    }

                    SetpointType.NEUTRAL -> {
                        when (talonFxService.activeNeutralOut) {
                            NeutralModeValue.Coast -> controlRequest = CoastOut()
                            NeutralModeValue.Brake -> controlRequest = StaticBrake()
                        }
                    }

                    SetpointType.MUSIC -> {
                        controlRequest = MusicTone(setpoint)
                    }

                }

                //run Talon
                talonFxService.active.forEach {
                    logger.info { "Control Request: ${controlRequest.name}: ${controlRequest.controlInfo}" }
                    it.setControl(controlRequest)
                }

                //Check Timeout
                if (duration > 0.0) {
                    logger.debug { "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting output = 0.0" }
                    talonFxService.active.forEach { it.set(0.0) }
                }
            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }
}