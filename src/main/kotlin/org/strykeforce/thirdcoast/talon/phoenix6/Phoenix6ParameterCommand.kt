package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.configs.SlotConfigs
import com.ctre.phoenix6.hardware.TalonFX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.talon.phoenix6.Phoenix6Parameter.P6Enum.*
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {  }

class Phoenix6ParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {



    private val talonFxService : TalonFxService by inject()
    //val type = toml.getString(Command.DEVICE_KEY) ?: throw  Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonFxService.timeout
    private val param = Phoenix6Parameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            val config = talonFxService.activeConfiguration
            logger.info { "Current Param: ${param.name}, ${param.prompt}" }

            return when(param.enum) {
                ROTOR_OFFSET -> formatMenu(config.Feedback.FeedbackRotorOffset, DOUBLE_FORMAT_4)
                SENSOR_TO_MECH_RATIO -> formatMenu(config.Feedback.SensorToMechanismRatio, DOUBLE_FORMAT_4)
                ROTOR_TO_SENSOR_RATIO -> formatMenu(config.Feedback.RotorToSensorRatio, DOUBLE_FORMAT_4)
                REMOTE_SENSOR_ID -> formatMenu(config.Feedback.FeedbackRemoteSensorID)

                SLOT_KP -> formatMenu(
                    when(talonFxService.activeSlotIndex) {
                        0 -> talonFxService.activeConfiguration.Slot0.kP
                        1 -> talonFxService.activeConfiguration.Slot1.kP
                        2 -> talonFxService.activeConfiguration.Slot2.kP
                        else -> SlotConfigs().kP
                    }
                )
                SLOT_KI -> formatMenu(
                    when(talonFxService.activeSlotIndex) {
                        0 -> talonFxService.activeConfiguration.Slot0.kI
                        1 -> talonFxService.activeConfiguration.Slot1.kI
                        2 -> talonFxService.activeConfiguration.Slot2.kI
                        else -> SlotConfigs().kI
                    }
                )
                SLOT_KD -> formatMenu(
                    when(talonFxService.activeSlotIndex) {
                        0 -> talonFxService.activeConfiguration.Slot0.kD
                        1 -> talonFxService.activeConfiguration.Slot1.kD
                        2 -> talonFxService.activeConfiguration.Slot2.kD
                        else -> SlotConfigs().kD
                    }
                )
                SLOT_KS -> formatMenu(
                    when(talonFxService.activeSlotIndex) {
                        0 -> talonFxService.activeConfiguration.Slot0.kS
                        1 -> talonFxService.activeConfiguration.Slot1.kS
                        2 -> talonFxService.activeConfiguration.Slot2.kS
                        else -> SlotConfigs().kS
                    }
                )
                SLOT_KV -> formatMenu(
                    when(talonFxService.activeSlotIndex) {
                        0 -> talonFxService.activeConfiguration.Slot0.kV
                        1 -> talonFxService.activeConfiguration.Slot1.kV
                        2 -> talonFxService.activeConfiguration.Slot2.kV
                        else -> SlotConfigs().kV
                    }
                )
                SLOT_KA -> formatMenu(
                    when(talonFxService.activeSlotIndex){
                        0 -> talonFxService.activeConfiguration.Slot0.kA
                        1 -> talonFxService.activeConfiguration.Slot1.kA
                        2 -> talonFxService.activeConfiguration.Slot2.kA
                        else -> SlotConfigs().kA
                    }
                )
                SLOT_KG -> formatMenu(
                    when(talonFxService.activeSlotIndex) {
                        0 -> talonFxService.activeConfiguration.Slot0.kG
                        1 -> talonFxService.activeConfiguration.Slot1.kG
                        2 -> talonFxService.activeConfiguration.Slot2.kG
                        else -> SlotConfigs().kG
                    }
                )

                MM_CRUISE_VEL -> formatMenu(config.MotionMagic.MotionMagicCruiseVelocity)
                MM_ACCEL -> formatMenu(config.MotionMagic.MotionMagicAcceleration)
                MM_JERK -> formatMenu(config.MotionMagic.MotionMagicJerk)

                PEAK_DIFF_DC -> formatMenu(config.DifferentialConstants.PeakDifferentialDutyCycle)
                PEAK_DIFF_VOLT -> formatMenu(config.DifferentialConstants.PeakDifferentialVoltage)
                PEAK_DIFF_TORQUE -> formatMenu(config.DifferentialConstants.PeakDifferentialTorqueCurrent)
                DIFF_SENSOR_REMOTE_ID -> formatMenu(config.DifferentialSensors.DifferentialRemoteSensorID)
                DIFF_FX_ID -> formatMenu(config.DifferentialSensors.DifferentialTalonFXSensorID)

                STATOR_LIM_EN -> formatMenu(config.CurrentLimits.StatorCurrentLimitEnable)
                STATOR_LIM -> formatMenu(config.CurrentLimits.StatorCurrentLimit)
                SUPP_LIM_EN -> formatMenu(config.CurrentLimits.SupplyCurrentLimitEnable)
                SUPP_LIM -> formatMenu(config.CurrentLimits.SupplyCurrentLimit)
                SUPP_TRIP_THRES -> formatMenu(config.CurrentLimits.SupplyCurrentThreshold)
                SUPP_TRIP_TIME -> formatMenu(config.CurrentLimits.SupplyTimeThreshold)

                CLOSED_LOOP_RAMP_DC -> formatMenu(config.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod)
                PEAK_FWD_DC -> formatMenu(config.MotorOutput.PeakForwardDutyCycle)
                PEAK_REV_DC -> formatMenu(config.MotorOutput.PeakReverseDutyCycle)
                NEUTRAL_DEADBAND_DC -> formatMenu(config.MotorOutput.DutyCycleNeutralDeadband)
                OPEN_LOOP_RAMP_DC -> formatMenu(config.OpenLoopRamps.DutyCycleOpenLoopRampPeriod)
                PEAK_FWD_V -> formatMenu(config.Voltage.PeakForwardVoltage)
                PEAK_REV_V -> formatMenu(config.Voltage.PeakReverseVoltage)
                SUPPLY_V_TIME_CONST -> formatMenu(config.Voltage.SupplyVoltageTimeConstant, DOUBLE_FORMAT_4)
                OPEN_LOOP_RAMP_V -> formatMenu(config.OpenLoopRamps.VoltageOpenLoopRampPeriod)
                CLOSED_LOOP_RAMP_V -> formatMenu(config.ClosedLoopRamps.VoltageClosedLoopRampPeriod)
                PEAK_FWD_I -> formatMenu(config.TorqueCurrent.PeakForwardTorqueCurrent)
                PEAK_REV_I -> formatMenu(config.TorqueCurrent.PeakReverseTorqueCurrent)
                TORQUE_NEUTRAL_DEADBAND -> formatMenu(config.TorqueCurrent.TorqueNeutralDeadband)
                OPEN_LOOP_RAMP_I -> formatMenu(config.OpenLoopRamps.TorqueOpenLoopRampPeriod)
                CLOSED_LOOP_RAMP_I -> formatMenu(config.ClosedLoopRamps.TorqueClosedLoopRampPeriod)

                FWD_SOFT_EN -> formatMenu(config.SoftwareLimitSwitch.ForwardSoftLimitEnable)
                FWD_SOFT_THRES -> formatMenu(config.SoftwareLimitSwitch.ForwardSoftLimitThreshold)
                REV_SOFT_EN -> formatMenu(config.SoftwareLimitSwitch.ReverseSoftLimitEnable)
                REV_SOFT_THRES -> formatMenu(config.SoftwareLimitSwitch.ReverseSoftLimitThreshold)

                FWD_HARD_EN -> formatMenu(config.HardwareLimitSwitch.ForwardLimitEnable)
                FWD_REMOTE_ID -> formatMenu(config.HardwareLimitSwitch.ForwardLimitRemoteSensorID)
                FWD_AUTOSET_POS -> formatMenu(config.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable)
                FWD_AUTOSET_POS_VALUE -> formatMenu(config.HardwareLimitSwitch.ForwardLimitAutosetPositionValue)
                REV_HARD_EN -> formatMenu(config.HardwareLimitSwitch.ReverseLimitEnable)
                REV_REMOTE_ID -> formatMenu(config.HardwareLimitSwitch.ReverseLimitRemoteSensorID)
                REV_AUTOSET_POS -> formatMenu(config.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable)
                REV_AUTOSET_POS_VALUE -> formatMenu(config.HardwareLimitSwitch.ReverseLimitAutosetPositionValue)

                ALLOW_MUSIC_DIS -> formatMenu(config.Audio.AllowMusicDurDisable)
                BEEP_ON_BOOT -> formatMenu(config.Audio.BeepOnBoot)
                BEEP_ON_CONFIG -> formatMenu(config.Audio.BeepOnConfig)
                VELOCITY -> formatMenu(talonFxService.activeVelocity)
                ACCELERATION -> formatMenu(talonFxService.activeAcceleration)
                JERK -> formatMenu(talonFxService.activeJerk)
                TORQUE_CURRENT_DEADBAND -> formatMenu(talonFxService.activeTorqueCurrentDeadband)
                TORQUE_CURRENT_MAX -> formatMenu(talonFxService.activeTorqueCurrentMaxOut)
                FEED_FORWARD -> formatMenu(talonFxService.activeFeedForward)
                OPPOSE_MAIN -> formatMenu(talonFxService.activeOpposeMain)
                DIFFERENTIAL_SLOT -> formatMenu(talonFxService.activeDifferentialSlot)
                DIFFERENTIAL_TARGET -> formatMenu(talonFxService.activeDifferentialTarget)
                FOC -> formatMenu(talonFxService.activeFOC)
                OVERRIDE_NEUTRAL -> formatMenu(talonFxService.activeOverrideNeutral)
                LIM_FWD_MOT -> formatMenu(talonFxService.limFwdMotion)
                LIM_REV_MOT -> formatMenu(talonFxService.limRevMotion)
                GRAPHER_FRAME -> formatMenu(talonFxService.grapherStatusFrameHz)

                else -> TODO("${param.enum} not implemented")
            }
        }

    override fun execute(): Command {
        val config = talonFxService.activeConfiguration
        val activeSlotIndex = talonFxService.activeSlotIndex
        val timeout = talonFxService.timeout

        when(param.enum) {
            ROTOR_OFFSET -> configDoubleParam(config.Feedback.FeedbackRotorOffset) {
                talonFx, value ->
                config.Feedback.FeedbackRotorOffset = value
                talonFx.configurator.apply(config.Feedback)
            }
            SENSOR_TO_MECH_RATIO -> configDoubleParam(config.Feedback.SensorToMechanismRatio) {
                talonFx, value ->
                config.Feedback.SensorToMechanismRatio = value
                talonFx.configurator.apply(config.Feedback)
            }
            ROTOR_TO_SENSOR_RATIO -> configDoubleParam(config.Feedback.RotorToSensorRatio) {
                talonFx, value ->
                config.Feedback.RotorToSensorRatio = value
                talonFx.configurator.apply(config.Feedback)
            }
            REMOTE_SENSOR_ID -> configIntParam(config.Feedback.FeedbackRemoteSensorID) {
                talonFx, value ->
                config.Feedback.FeedbackRemoteSensorID = value
                talonFx.configurator.apply(config.Feedback)
            }

            SLOT_KP -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kP) {
                        talonFx, value ->
                        config.Slot0.kP = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kP) {
                        talonFx, value ->
                        config.Slot1.kP = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot2.kP) {
                        talonFx, value ->
                        config.Slot2.kP = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }
            SLOT_KI -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kI) {
                        talonFx, value ->
                        config.Slot0.kI = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kI) {
                        talonFx, value ->
                        config.Slot1.kI = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot0.kI) {
                        talonFx, value ->
                        config.Slot2.kI = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }
            SLOT_KD -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kD) {
                        talonFx, value ->
                        config.Slot0.kD = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kD) {
                        talonFx, value ->
                        config.Slot1.kD = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot2.kD) {
                        talonFx, value ->
                        config.Slot2.kD = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }
            SLOT_KS -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kS) {
                            talonFx, value ->
                        config.Slot0.kS = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kS) {
                            talonFx, value ->
                        config.Slot1.kS = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot2.kS) {
                            talonFx, value ->
                        config.Slot2.kS = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }
            SLOT_KV -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kV) {
                            talonFx, value ->
                        config.Slot0.kV = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kV) {
                            talonFx, value ->
                        config.Slot1.kV = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot2.kV) {
                            talonFx, value ->
                        config.Slot2.kV = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }
            SLOT_KA -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kA) {
                            talonFx, value ->
                        config.Slot0.kA = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kA) {
                            talonFx, value ->
                        config.Slot1.kA = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot2.kA) {
                            talonFx, value ->
                        config.Slot2.kA = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }
            SLOT_KG -> {
                when(activeSlotIndex) {
                    0 -> configDoubleParam(config.Slot0.kG) {
                            talonFx, value ->
                        config.Slot0.kG = value
                        talonFx.configurator.apply(config.Slot0)
                    }
                    1 -> configDoubleParam(config.Slot1.kG) {
                            talonFx, value ->
                        config.Slot1.kG = value
                        talonFx.configurator.apply(config.Slot1)
                    }
                    2 -> configDoubleParam(config.Slot2.kG) {
                            talonFx, value ->
                        config.Slot2.kG = value
                        talonFx.configurator.apply(config.Slot2)
                    }
                    else -> TODO("$activeSlotIndex is not a valid slot")
                }
            }

            MM_ACCEL -> configDoubleParam(config.MotionMagic.MotionMagicAcceleration) {
                talonFx, value ->
                config.MotionMagic.MotionMagicAcceleration = value
                talonFx.configurator.apply(config.MotionMagic)
            }
            MM_CRUISE_VEL -> configDoubleParam(config.MotionMagic.MotionMagicCruiseVelocity) {
                talonFx, value ->
                config.MotionMagic.MotionMagicCruiseVelocity = value
                talonFx.configurator.apply(config.MotionMagic)
            }
            MM_JERK -> configDoubleParam(config.MotionMagic.MotionMagicJerk) {
                talonFx, value ->
                config.MotionMagic.MotionMagicJerk = value
                talonFx.configurator.apply(config.MotionMagic)
            }

            PEAK_DIFF_DC -> configDoubleParam(config.DifferentialConstants.PeakDifferentialDutyCycle) {
                talonFx, value ->
                config.DifferentialConstants.PeakDifferentialDutyCycle = value
                talonFx.configurator.apply(config.DifferentialConstants)
            }
            PEAK_DIFF_TORQUE -> configDoubleParam(config.DifferentialConstants.PeakDifferentialTorqueCurrent) {
                talonFx, value ->
                config.DifferentialConstants.PeakDifferentialTorqueCurrent = value
                talonFx.configurator.apply(config.DifferentialConstants)
            }
            PEAK_DIFF_VOLT -> configDoubleParam(config.DifferentialConstants.PeakDifferentialVoltage) {
                talonFx, value ->
                config.DifferentialConstants.PeakDifferentialVoltage = value
                talonFx.configurator.apply(config.DifferentialConstants)
            }
            DIFF_SENSOR_REMOTE_ID -> configIntParam(config.DifferentialSensors.DifferentialRemoteSensorID) {
                talonFx, value ->
                config.DifferentialSensors.DifferentialRemoteSensorID = value
                talonFx.configurator.apply(config.DifferentialSensors)
            }
            DIFF_FX_ID -> configIntParam(config.DifferentialSensors.DifferentialTalonFXSensorID) {
                talonFx, value ->
                config.DifferentialSensors.DifferentialTalonFXSensorID = value
                talonFx.configurator.apply(config.DifferentialSensors)
            }

            STATOR_LIM_EN -> configBooleanParam(config.CurrentLimits.StatorCurrentLimitEnable){
                talonFx, value ->
                config.CurrentLimits.StatorCurrentLimitEnable = value
                talonFx.configurator.apply(config.CurrentLimits)
            }
            STATOR_LIM -> configDoubleParam(config.CurrentLimits.StatorCurrentLimit) {
                talonFx, value ->
                config.CurrentLimits.StatorCurrentLimit = value
                talonFx.configurator.apply(config.CurrentLimits)
            }
            SUPP_LIM_EN -> configBooleanParam(config.CurrentLimits.SupplyCurrentLimitEnable) {
                talonFx, value ->
                config.CurrentLimits.SupplyCurrentLimitEnable = value
                talonFx.configurator.apply(config.CurrentLimits)
            }
            SUPP_LIM -> configDoubleParam(config.CurrentLimits.SupplyCurrentLimit) {
                talonFx, value ->
                config.CurrentLimits.SupplyCurrentLimit = value
                talonFx.configurator.apply(config.CurrentLimits)
            }
            SUPP_TRIP_THRES -> configDoubleParam(config.CurrentLimits.SupplyCurrentThreshold) {
                talonFx, value ->
                config.CurrentLimits.SupplyCurrentThreshold = value
                talonFx.configurator.apply(config.CurrentLimits)
            }
            SUPP_TRIP_TIME -> configDoubleParam(config.CurrentLimits.SupplyTimeThreshold) {
                talonFx, value ->
                config.CurrentLimits.SupplyTimeThreshold = value
                talonFx.configurator.apply(config.CurrentLimits)
            }

            CLOSED_LOOP_RAMP_DC -> configDoubleParam(config.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod) {
                talonFx, value ->
                config.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = value
                talonFx.configurator.apply(config.ClosedLoopRamps)
            }
            PEAK_FWD_DC -> configDoubleParam(config.MotorOutput.PeakForwardDutyCycle) {
                talonFx, value ->
                config.MotorOutput.PeakForwardDutyCycle = value
                talonFx.configurator.apply(config.MotorOutput)
            }
            PEAK_REV_DC -> configDoubleParam(config.MotorOutput.PeakReverseDutyCycle) {
                talonFx, value ->
                config.MotorOutput.PeakReverseDutyCycle = value
                talonFx.configurator.apply(config.MotorOutput)
            }
            NEUTRAL_DEADBAND_DC -> configDoubleParam(config.MotorOutput.DutyCycleNeutralDeadband) {
                talonFx, value ->
                config.MotorOutput.DutyCycleNeutralDeadband = value
                talonFx.configurator.apply(config.MotorOutput)
            }
            OPEN_LOOP_RAMP_DC -> configDoubleParam(config.OpenLoopRamps.DutyCycleOpenLoopRampPeriod) {
                talonFx, value ->
                config.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = value
                talonFx.configurator.apply(config.OpenLoopRamps)
            }
            PEAK_FWD_V -> configDoubleParam(config.Voltage.PeakForwardVoltage) {
                talonFx, value ->
                config.Voltage.PeakForwardVoltage = value
                talonFx.configurator.apply(config.Voltage)
            }
            PEAK_REV_V -> configDoubleParam(config.Voltage.PeakReverseVoltage) {
                talonFx, value ->
                config.Voltage.PeakReverseVoltage = value
                talonFx.configurator.apply(config.Voltage)
            }
            SUPPLY_V_TIME_CONST -> configDoubleParam(config.Voltage.SupplyVoltageTimeConstant) {
                talonFx, value ->
                config.Voltage.SupplyVoltageTimeConstant = value
                talonFx.configurator.apply(config.Voltage)
            }
            OPEN_LOOP_RAMP_V -> configDoubleParam(config.OpenLoopRamps.VoltageOpenLoopRampPeriod) {
                talonFx, value ->
                config.OpenLoopRamps.VoltageOpenLoopRampPeriod = value
                talonFx.configurator.apply(config.OpenLoopRamps)
            }
            CLOSED_LOOP_RAMP_V -> configDoubleParam(config.ClosedLoopRamps.VoltageClosedLoopRampPeriod) {
                talonFx, value ->
                config.ClosedLoopRamps.VoltageClosedLoopRampPeriod = value
                talonFx.configurator.apply(config.ClosedLoopRamps)
            }
            PEAK_FWD_I -> configDoubleParam(config.TorqueCurrent.PeakForwardTorqueCurrent) {
                talonFx, value ->
                config.TorqueCurrent.PeakForwardTorqueCurrent = value
                talonFx.configurator.apply(config.TorqueCurrent)
            }
            PEAK_REV_I -> configDoubleParam(config.TorqueCurrent.PeakReverseTorqueCurrent) {
                talonFx, value ->
                config.TorqueCurrent.PeakReverseTorqueCurrent = value
                talonFx.configurator.apply(config.TorqueCurrent)
            }
            TORQUE_NEUTRAL_DEADBAND -> configDoubleParam(config.TorqueCurrent.TorqueNeutralDeadband) {
                talonFx, value ->
                config.TorqueCurrent.TorqueNeutralDeadband = value
                talonFx.configurator.apply(config.TorqueCurrent)
            }
            OPEN_LOOP_RAMP_I -> configDoubleParam(config.OpenLoopRamps.TorqueOpenLoopRampPeriod) {
                talonFx, value ->
                config.OpenLoopRamps.TorqueOpenLoopRampPeriod = value
                talonFx.configurator.apply(config.OpenLoopRamps)
            }
            CLOSED_LOOP_RAMP_I -> configDoubleParam(config.ClosedLoopRamps.TorqueClosedLoopRampPeriod) {
                talonFx, value ->
                config.ClosedLoopRamps.TorqueClosedLoopRampPeriod = value
                talonFx.configurator.apply(config.ClosedLoopRamps)
            }
            CONTINUOUS_WRAP -> configBooleanParam(config.ClosedLoopGeneral.ContinuousWrap){
                talonFx, value ->
                config.ClosedLoopGeneral.ContinuousWrap = value
                talonFx.configurator.apply(config.ClosedLoopGeneral)
            }

            FWD_SOFT_EN -> configBooleanParam(config.SoftwareLimitSwitch.ForwardSoftLimitEnable) {
                talonFx, value ->
                config.SoftwareLimitSwitch.ForwardSoftLimitEnable = value
                talonFx.configurator.apply(config.SoftwareLimitSwitch)
            }
            FWD_SOFT_THRES -> configDoubleParam(config.SoftwareLimitSwitch.ForwardSoftLimitThreshold) {
                talonFx, value ->
                config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = value
                talonFx.configurator.apply(config.SoftwareLimitSwitch)
            }
            REV_SOFT_EN -> configBooleanParam(config.SoftwareLimitSwitch.ReverseSoftLimitEnable) {
                talonFx, value ->
                config.SoftwareLimitSwitch.ReverseSoftLimitEnable = value
                talonFx.configurator.apply(config.SoftwareLimitSwitch)
            }
            REV_SOFT_THRES -> configDoubleParam(config.SoftwareLimitSwitch.ReverseSoftLimitThreshold) {
                talonFx, value ->
                config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = value
                talonFx.configurator.apply(config.SoftwareLimitSwitch)
            }

            FWD_HARD_EN -> configBooleanParam(config.HardwareLimitSwitch.ForwardLimitEnable) {
                talonFx, value ->
                config.HardwareLimitSwitch.ForwardLimitEnable = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            FWD_REMOTE_ID -> configIntParam(config.HardwareLimitSwitch.ForwardLimitRemoteSensorID) {
                talonFx, value ->
                config.HardwareLimitSwitch.ForwardLimitRemoteSensorID = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            FWD_AUTOSET_POS -> configBooleanParam(config.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable) {
                talonFx, value ->
                config.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            FWD_AUTOSET_POS_VALUE -> configDoubleParam(config.HardwareLimitSwitch.ForwardLimitAutosetPositionValue) {
                talonFx, value ->
                config.HardwareLimitSwitch.ForwardLimitAutosetPositionValue = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            REV_HARD_EN -> configBooleanParam(config.HardwareLimitSwitch.ReverseLimitEnable) {
                talonFx, value ->
                config.HardwareLimitSwitch.ReverseLimitEnable = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            REV_REMOTE_ID -> configIntParam(config.HardwareLimitSwitch.ReverseLimitRemoteSensorID) {
                talonFx, value ->
                config.HardwareLimitSwitch.ReverseLimitRemoteSensorID = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            REV_AUTOSET_POS -> configBooleanParam(config.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable) {
                talonFx, value ->
                config.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }
            REV_AUTOSET_POS_VALUE -> configDoubleParam(config.HardwareLimitSwitch.ReverseLimitAutosetPositionValue) {
                talonFx, value ->
                config.HardwareLimitSwitch.ReverseLimitAutosetPositionValue = value
                talonFx.configurator.apply(config.HardwareLimitSwitch)
            }

            ALLOW_MUSIC_DIS -> configBooleanParam(config.Audio.AllowMusicDurDisable) {
                talonFx, value ->
                config.Audio.AllowMusicDurDisable = value
                talonFx.configurator.apply(config.Audio)
            }
            BEEP_ON_BOOT -> configBooleanParam(config.Audio.BeepOnBoot) {
                talonFx, value ->
                config.Audio.BeepOnBoot = value
                talonFx.configurator.apply(config.Audio)
            }
            BEEP_ON_CONFIG -> configBooleanParam(config.Audio.BeepOnConfig) {
                talonFx, value ->
                config.Audio.BeepOnConfig = value
                talonFx.configurator.apply(config.Audio)
            }
            VELOCITY -> configDoubleParam(talonFxService.activeVelocity) {
                talonFx, value ->
                talonFxService.activeVelocity = value
            }
            ACCELERATION -> configDoubleParam(talonFxService.activeAcceleration){
                talonFx, value ->
                talonFxService.activeAcceleration = value
            }
            JERK -> configDoubleParam(talonFxService.activeJerk){
                talonFx, value ->
                talonFxService.activeJerk = value
            }
            TORQUE_CURRENT_DEADBAND -> configDoubleParam(talonFxService.activeTorqueCurrentDeadband){
                talonFx, value ->
                talonFxService.activeTorqueCurrentDeadband = value
            }
            TORQUE_CURRENT_MAX -> configDoubleParam(talonFxService.activeTorqueCurrentMaxOut){
                talonFx, value ->
                talonFxService.activeTorqueCurrentMaxOut = value
            }
            FEED_FORWARD -> configDoubleParam(talonFxService.activeFeedForward){
                talonFx, value ->
                talonFxService.activeFeedForward = value
            }
            OPPOSE_MAIN -> configBooleanParam(talonFxService.activeOpposeMain){
                talonFx, value ->
                talonFxService.activeOpposeMain = value
            }
            DIFFERENTIAL_SLOT -> configIntParam(talonFxService.activeDifferentialSlot){
                talonFx, value ->
                if(value >= 0 && value <= 2){
                    talonFxService.activeDifferentialSlot = value
                } else {
                    terminal.warn("${value} is not a valid slot index, must be 0-2")
                }
            }
            DIFFERENTIAL_TARGET -> configDoubleParam(talonFxService.activeDifferentialTarget){
                talonFx, value ->
                talonFxService.activeDifferentialTarget = value
            }
            FOC -> configBooleanParam(talonFxService.activeFOC){
                talonFx, value ->
                talonFxService.activeFOC = value
            }
            OVERRIDE_NEUTRAL -> configBooleanParam(talonFxService.activeOverrideNeutral){
                talonFx, value ->
                talonFxService.activeOverrideNeutral = value
            }
            LIM_FWD_MOT -> configBooleanParam(talonFxService.limFwdMotion) {
                talonFx, value ->
                talonFxService.limFwdMotion = value;
            }
            LIM_REV_MOT -> configBooleanParam(talonFxService.limRevMotion) {
                talonFx, value ->
                talonFxService.limRevMotion = value;
            }
            GRAPHER_FRAME -> configDoubleParam(talonFxService.grapherStatusFrameHz) {
                talonFx, value ->
                talonFxService.grapherStatusFrameHz = value;
                talonFx.acceleration.setUpdateFrequency(value, timeout)
                talonFx.bridgeOutput.setUpdateFrequency(value, timeout)
                talonFx.deviceTemp.setUpdateFrequency(value, timeout)
                talonFx.differentialAveragePosition.setUpdateFrequency(value, timeout)
                talonFx.differentialAverageVelocity.setUpdateFrequency(value, timeout)
                talonFx.differentialDifferencePosition.setUpdateFrequency(value, timeout)
                talonFx.differentialDifferenceVelocity.setUpdateFrequency(value, timeout)
                talonFx.dutyCycle.setUpdateFrequency(value, timeout)
                talonFx.forwardLimit.setUpdateFrequency(value, timeout)
                talonFx.reverseLimit.setUpdateFrequency(value, timeout)
                talonFx.isProLicensed.setUpdateFrequency(value, timeout)
                talonFx.motionMagicIsRunning.setUpdateFrequency(value, timeout)
                talonFx.motorVoltage.setUpdateFrequency(value, timeout)
                talonFx.position.setUpdateFrequency(value, timeout)
                talonFx.processorTemp.setUpdateFrequency(value, timeout)
                talonFx.rotorPosition.setUpdateFrequency(value, timeout)
                talonFx.rotorVelocity.setUpdateFrequency(value, timeout)
                talonFx.statorCurrent.setUpdateFrequency(value, timeout)
                talonFx.supplyCurrent.setUpdateFrequency(value, timeout)
                talonFx.supplyVoltage.setUpdateFrequency(value, timeout)
                talonFx.torqueCurrent.setUpdateFrequency(value, timeout)
                talonFx.velocity.setUpdateFrequency(value, timeout)

                talonFx.closedLoopDerivativeOutput.setUpdateFrequency(value, timeout)
                talonFx.closedLoopError.setUpdateFrequency(value, timeout)
                talonFx.closedLoopFeedForward.setUpdateFrequency(value, timeout)
                talonFx.closedLoopIntegratedOutput.setUpdateFrequency(value, timeout)
                talonFx.closedLoopOutput.setUpdateFrequency(value, timeout)
                talonFx.closedLoopProportionalOutput.setUpdateFrequency(value, timeout)
                talonFx.closedLoopReference.setUpdateFrequency(value, timeout)
                talonFx.closedLoopReferenceSlope.setUpdateFrequency(value, timeout)
                talonFx.closedLoopSlot.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopDerivativeOutput.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopError.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopFeedForward.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopIntegratedOutput.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopOutput.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopProportionalOutput.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopReference.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopReferenceSlope.setUpdateFrequency(value, timeout)
                talonFx.differentialClosedLoopSlot.setUpdateFrequency(value, timeout)

            }
            else -> TODO("${param.name} not implemented")

        }
        return super.execute()
    }

    private fun configDoubleParam(default: Double, configure: (TalonFX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)

        talonFxService.active.forEach { configure(it, paramValue) }
        logger.info { "set ${talonFxService.active.size} talonFx's ${param.name}: $paramValue" }
    }

    private fun configIntParam(default: Int, configure: (TalonFX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonFxService.active.forEach { configure(it, paramValue) }
        logger.info { "set ${talonFxService.active.size} talonFx's  ${param.name}: $paramValue" }
    }

    private fun configBooleanParam(default: Boolean, configure: (TalonFX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        talonFxService.active.forEach { configure(it, paramValue) }
        logger.info { "Set ${talonFxService.active.size} talonFx's ${param.name}: $paramValue" }
    }



}