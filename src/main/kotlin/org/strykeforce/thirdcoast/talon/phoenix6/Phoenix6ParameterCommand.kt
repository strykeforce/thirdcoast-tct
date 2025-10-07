package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.configs.SlotConfigs
import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.configs.TalonFXSConfiguration
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.hardware.TalonFXS
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.strykeforce.thirdcoast.talon.phoenix6.Phoenix6Parameter.P6Enum.*
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {  }

class Phoenix6ParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {



    private val talonFxService : TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService : TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by  inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    //val type = toml.getString(Command.DEVICE_KEY) ?: throw  Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonFxService.timeout
    private val param = Phoenix6Parameter.create(this, toml.getString("param") ?: "UNKNOWN", bus, device)

    override val menu: String
        get() {
            var fxConfig: TalonFXConfiguration = TalonFXConfiguration()
            var activeSlot: Int = 0
            var fxsConfig: TalonFXSConfiguration = TalonFXSConfiguration()
            if(device == "fx") {
                fxConfig = if(bus == "rio") talonFxService.activeConfiguration else talonFxFDService.activeConfiguration
                activeSlot = if(bus == "rio") talonFxService.activeSlotIndex else talonFxFDService.activeSlotIndex
                logger.info { "Current Param: ${param.name}, ${param.prompt}" }
            } else if (device == "fxs") {
                fxsConfig = if(bus == "rio") talonFxsService.activeConfiguration else talonFxsFDService.activeConfiguration
                activeSlot = if(bus == "rio") talonFxsService.activeSlotIndex else talonFxsFDService.activeSlotIndex
                logger.info { "Current Param: ${param.name}, ${param.prompt}" }
            } else throw IllegalArgumentException("Invalid device type")

            return when(param.enum) {
                ROTOR_OFFSET -> when(device) {
                    "fx" -> formatMenu(fxConfig.Feedback.FeedbackRotorOffset, DOUBLE_FORMAT_4)
                    else -> throw IllegalArgumentException()
                }
                SENSOR_TO_MECH_RATIO -> when(device) {
                    "fx" -> formatMenu(fxConfig.Feedback.SensorToMechanismRatio, DOUBLE_FORMAT_4)
                    "fxs" -> formatMenu(fxsConfig.ExternalFeedback.SensorToMechanismRatio, DOUBLE_FORMAT_4)
                    else -> throw IllegalArgumentException()
                }
                ROTOR_TO_SENSOR_RATIO -> when(device) {
                    "fx" -> formatMenu(fxConfig.Feedback.RotorToSensorRatio, DOUBLE_FORMAT_4)
                    "fxs" -> formatMenu(fxsConfig.ExternalFeedback.RotorToSensorRatio, DOUBLE_FORMAT_4)
                    else -> throw IllegalArgumentException()
                }
                REMOTE_SENSOR_ID -> when(device) {
                    "fx" -> formatMenu(fxConfig.Feedback.FeedbackRemoteSensorID)
                    "fxs" -> formatMenu(fxsConfig.ExternalFeedback.FeedbackRemoteSensorID)
                    else -> throw IllegalArgumentException()
                }
                SLOT_KP -> when(device) {
                    "fx" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxService.activeConfiguration.Slot0.kP else talonFxFDService.activeConfiguration.Slot0.kP
                            1 -> if (bus == "rio") talonFxService.activeConfiguration.Slot1.kP else talonFxFDService.activeConfiguration.Slot1.kP
                            2 -> if (bus == "rio") talonFxService.activeConfiguration.Slot2.kP else talonFxFDService.activeConfiguration.Slot2.kP
                            else -> SlotConfigs().kP
                        }
                    )

                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kP else talonFxsFDService.activeConfiguration.Slot0.kP
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kP else talonFxsFDService.activeConfiguration.Slot1.kP
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kP else talonFxsFDService.activeConfiguration.Slot2.kP
                            else -> SlotConfigs().kP
                        }
                    )
                    else -> throw IllegalArgumentException()
                }
                SLOT_KI -> when(device){
                    "fx" -> formatMenu(
                        when(activeSlot) {
                            0 -> if(bus == "rio") talonFxService.activeConfiguration.Slot0.kI else talonFxFDService.activeConfiguration.Slot0.kI
                            1 -> if(bus == "rio") talonFxService.activeConfiguration.Slot1.kI else talonFxFDService.activeConfiguration.Slot1.kI
                            2 -> if(bus == "rio") talonFxService.activeConfiguration.Slot2.kI else talonFxFDService.activeConfiguration.Slot2.kI
                            else -> SlotConfigs().kI
                        }
                    )
                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kI else talonFxsFDService.activeConfiguration.Slot1.kI
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kI else talonFxsFDService.activeConfiguration.Slot0.kI
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kI else talonFxsFDService.activeConfiguration.Slot2.kI
                            else -> SlotConfigs().kI
                        }
                    )
                    else -> throw IllegalArgumentException()
                }
                SLOT_KD -> when(device){
                    "fx" -> formatMenu(
                        when(activeSlot) {
                            0 -> if(bus == "rio") talonFxService.activeConfiguration.Slot0.kD else talonFxFDService.activeConfiguration.Slot0.kD
                            1 -> if(bus == "rio") talonFxService.activeConfiguration.Slot1.kD else talonFxFDService.activeConfiguration.Slot1.kD
                            2 -> if(bus == "rio") talonFxService.activeConfiguration.Slot2.kD else talonFxFDService.activeConfiguration.Slot2.kD
                            else -> SlotConfigs().kD
                        }
                    )
                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kD else talonFxsFDService.activeConfiguration.Slot0.kD
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kD else talonFxsFDService.activeConfiguration.Slot1.kD
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kD else talonFxsFDService.activeConfiguration.Slot2.kD
                            else -> SlotConfigs().kD
                        }
                    )
                    else -> throw IllegalArgumentException()
                }
                SLOT_KS -> when(device){
                    "fx" -> formatMenu(
                        when(activeSlot) {
                            0 -> if(bus == "rio") talonFxService.activeConfiguration.Slot0.kS else talonFxFDService.activeConfiguration.Slot0.kS
                            1 -> if(bus == "rio") talonFxService.activeConfiguration.Slot1.kS else talonFxFDService.activeConfiguration.Slot1.kS
                            2 -> if(bus == "rio") talonFxService.activeConfiguration.Slot2.kS else talonFxFDService.activeConfiguration.Slot2.kS
                            else -> SlotConfigs().kS
                        }
                    )
                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kS else talonFxsFDService.activeConfiguration.Slot0.kS
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kS else talonFxsFDService.activeConfiguration.Slot1.kS
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kS else talonFxsFDService.activeConfiguration.Slot2.kS
                            else -> SlotConfigs().kS
                        }
                    )
                    else -> throw IllegalArgumentException()
                }
                SLOT_KV -> when(device){
                    "fx" -> formatMenu(
                        when(activeSlot) {
                            0 -> if(bus == "rio") talonFxService.activeConfiguration.Slot0.kV else talonFxFDService.activeConfiguration.Slot0.kV
                            1 -> if(bus == "rio") talonFxService.activeConfiguration.Slot1.kV else talonFxFDService.activeConfiguration.Slot1.kV
                            2 -> if(bus == "rio") talonFxService.activeConfiguration.Slot2.kV else talonFxFDService.activeConfiguration.Slot2.kV
                            else -> SlotConfigs().kV
                        }
                    )
                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kV else talonFxsFDService.activeConfiguration.Slot0.kV
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kV else talonFxsFDService.activeConfiguration.Slot1.kV
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kV else talonFxsFDService.activeConfiguration.Slot2.kV
                            else -> SlotConfigs().kV
                        }
                    )
                    else -> throw IllegalArgumentException()
                }
                SLOT_KA -> when(device){
                    "fx" -> formatMenu(
                        when(activeSlot){
                            0 -> if(bus == "rio") talonFxService.activeConfiguration.Slot0.kA else talonFxFDService.activeConfiguration.Slot0.kA
                            1 -> if(bus == "rio") talonFxService.activeConfiguration.Slot1.kA else talonFxFDService.activeConfiguration.Slot1.kA
                            2 -> if(bus == "rio") talonFxService.activeConfiguration.Slot2.kA else talonFxFDService.activeConfiguration.Slot2.kA
                            else -> SlotConfigs().kA
                        }
                    )
                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kA else talonFxsFDService.activeConfiguration.Slot0.kA
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kA else talonFxsFDService.activeConfiguration.Slot1.kA
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kA else talonFxsFDService.activeConfiguration.Slot2.kA
                            else -> SlotConfigs().kA
                        }
                    )
                    else -> throw IllegalArgumentException()
                }
                SLOT_KG -> when(device){
                    "fx" -> formatMenu(
                        when(activeSlot) {
                            0 -> if(bus == "rio") talonFxService.activeConfiguration.Slot0.kG else talonFxFDService.activeConfiguration.Slot0.kG
                            1 -> if(bus == "rio") talonFxService.activeConfiguration.Slot1.kG else talonFxFDService.activeConfiguration.Slot1.kG
                            2 -> if(bus == "rio") talonFxService.activeConfiguration.Slot2.kG else talonFxFDService.activeConfiguration.Slot2.kG
                            else -> SlotConfigs().kG
                        }
                    )
                    "fxs" -> formatMenu(
                        when (activeSlot) {
                            0 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot0.kG else talonFxsFDService.activeConfiguration.Slot0.kG
                            1 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot1.kG else talonFxsFDService.activeConfiguration.Slot1.kG
                            2 -> if (bus == "rio") talonFxsService.activeConfiguration.Slot2.kG else talonFxsFDService.activeConfiguration.Slot2.kG
                            else -> SlotConfigs().kG
                        }
                    )
                    else -> throw IllegalArgumentException()
                }

                MM_CRUISE_VEL -> when(device){
                    "fx" -> formatMenu(fxConfig.MotionMagic.MotionMagicCruiseVelocity)
                    "fxs" -> formatMenu(fxsConfig.MotionMagic.MotionMagicCruiseVelocity)
                    else -> throw IllegalArgumentException()
                }
                MM_ACCEL -> when(device){
                    "fx" -> formatMenu(fxConfig.MotionMagic.MotionMagicAcceleration)
                    "fxs" -> formatMenu(fxsConfig.MotionMagic.MotionMagicAcceleration)
                    else -> throw IllegalArgumentException()
                }
                MM_JERK -> when(device){
                    "fx" -> formatMenu(fxConfig.MotionMagic.MotionMagicJerk)
                    "fxs" -> formatMenu(fxsConfig.MotionMagic.MotionMagicJerk)
                    else -> throw IllegalArgumentException()
                }
                MM_EXPO_KA -> when(device){
                    "fx" -> formatMenu(fxConfig.MotionMagic.MotionMagicExpo_kA)
                    "fxs" -> formatMenu(fxsConfig.MotionMagic.MotionMagicExpo_kA)
                    else -> throw IllegalArgumentException()
                }
                MM_EXPO_KV -> when(device){
                    "fx" -> formatMenu(fxConfig.MotionMagic.MotionMagicExpo_kV)
                    "fxs" -> formatMenu(fxsConfig.MotionMagic.MotionMagicExpo_kV)
                    else -> throw IllegalArgumentException()
                }

                PEAK_DIFF_DC -> when(device){
                    "fx" -> formatMenu(fxConfig.DifferentialConstants.PeakDifferentialDutyCycle)
                    "fxs" -> formatMenu(fxsConfig.DifferentialConstants.PeakDifferentialDutyCycle)
                    else -> throw IllegalArgumentException()
                }
                PEAK_DIFF_VOLT -> when(device){
                    "fx" -> formatMenu(fxConfig.DifferentialConstants.PeakDifferentialVoltage)
                    "fxs" -> formatMenu(fxsConfig.DifferentialConstants.PeakDifferentialVoltage)
                    else -> throw IllegalArgumentException()
                }
                PEAK_DIFF_TORQUE -> when(device){
                    "fx" -> formatMenu(fxConfig.DifferentialConstants.PeakDifferentialTorqueCurrent)
                    "fxs" -> formatMenu(fxsConfig.DifferentialConstants.PeakDifferentialTorqueCurrent)
                    else -> throw IllegalArgumentException()
                }
                DIFF_SENSOR_REMOTE_ID -> when(device){
                    "fx" -> formatMenu(fxConfig.DifferentialSensors.DifferentialRemoteSensorID)
                    "fxs" -> formatMenu(fxsConfig.DifferentialSensors.DifferentialRemoteSensorID)
                    else -> throw IllegalArgumentException()
                }
                DIFF_FX_ID -> when(device){
                    "fx" -> formatMenu(fxConfig.DifferentialSensors.DifferentialTalonFXSensorID)
                    "fxs" -> formatMenu(fxsConfig.DifferentialSensors.DifferentialTalonFXSensorID)
                    else -> throw IllegalArgumentException()
                }

                STATOR_LIM_EN -> when(device){
                    "fx" -> formatMenu(fxConfig.CurrentLimits.StatorCurrentLimitEnable)
                    "fxs" -> formatMenu(fxsConfig.CurrentLimits.StatorCurrentLimitEnable)
                    else -> throw IllegalArgumentException()
                }
                STATOR_LIM -> when(device){
                    "fx" -> formatMenu(fxConfig.CurrentLimits.StatorCurrentLimit)
                    "fxs" -> formatMenu(fxsConfig.CurrentLimits.StatorCurrentLimit)
                    else -> throw IllegalArgumentException()
                }
                SUPP_LIM_EN -> when(device){
                    "fx" -> formatMenu(fxConfig.CurrentLimits.SupplyCurrentLimitEnable)
                    "fxs" -> formatMenu(fxsConfig.CurrentLimits.SupplyCurrentLimitEnable)
                    else -> throw IllegalArgumentException()
                }
                SUPP_LOWER_LIM -> when(device){
                    "fx" -> formatMenu(fxConfig.CurrentLimits.SupplyCurrentLowerLimit)
                    "fxs" -> formatMenu(fxsConfig.CurrentLimits.SupplyCurrentLowerLimit)
                    else -> throw IllegalArgumentException()
                }
                SUPP_LIM -> when(device){
                    "fx" -> formatMenu(fxConfig.CurrentLimits.SupplyCurrentLimit)
                    "fxs" -> formatMenu(fxsConfig.CurrentLimits.SupplyCurrentLimit)
                    else -> throw IllegalArgumentException()
                }
                SUPP_TRIP_TIME -> when(device){
                    "fx" -> formatMenu(fxConfig.CurrentLimits.SupplyCurrentLowerTime)
                    "fxs" -> formatMenu(fxsConfig.CurrentLimits.SupplyCurrentLowerTime)
                    else -> throw IllegalArgumentException()
                }

                CLOSED_LOOP_RAMP_DC -> when(device){
                    "fx" -> formatMenu(fxConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod)
                    "fxs" -> formatMenu(fxsConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod)
                    else -> throw IllegalArgumentException()
                }
                PEAK_FWD_DC -> when(device){
                    "fx" -> formatMenu(fxConfig.MotorOutput.PeakForwardDutyCycle)
                    "fxs" -> formatMenu(fxsConfig.MotorOutput.PeakForwardDutyCycle)
                    else -> throw IllegalArgumentException()
                }
                PEAK_REV_DC -> when(device){
                    "fx" -> formatMenu(fxConfig.MotorOutput.PeakReverseDutyCycle)
                    "fxs" -> formatMenu(fxsConfig.MotorOutput.PeakReverseDutyCycle)
                    else -> throw IllegalArgumentException()
                }
                NEUTRAL_DEADBAND_DC -> when(device){
                    "fx" -> formatMenu(fxConfig.MotorOutput.DutyCycleNeutralDeadband)
                    "fxs" -> formatMenu(fxsConfig.MotorOutput.DutyCycleNeutralDeadband)
                    else -> throw IllegalArgumentException()
                }
                OPEN_LOOP_RAMP_DC -> when(device){
                    "fx" -> formatMenu(fxConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod)
                    "fxs" -> formatMenu(fxsConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod)
                    else -> throw IllegalArgumentException()
                }
                PEAK_FWD_V -> when(device){
                    "fx" -> formatMenu(fxConfig.Voltage.PeakForwardVoltage)
                    "fxs" -> formatMenu(fxsConfig.Voltage.PeakForwardVoltage)
                    else -> throw IllegalArgumentException()
                }
                PEAK_REV_V -> when(device){
                    "fx" -> formatMenu(fxConfig.Voltage.PeakReverseVoltage)
                    "fxs" -> formatMenu(fxsConfig.Voltage.PeakReverseVoltage)
                    else -> throw IllegalArgumentException()
                }
                SUPPLY_V_TIME_CONST -> when(device){
                    "fx" -> formatMenu(fxConfig.Voltage.SupplyVoltageTimeConstant, DOUBLE_FORMAT_4)
                    "fxs" -> formatMenu(fxsConfig.Voltage.SupplyVoltageTimeConstant, DOUBLE_FORMAT_4)
                    else -> throw IllegalArgumentException()
                }
                OPEN_LOOP_RAMP_V -> when(device){
                    "fx" -> formatMenu(fxConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod)
                    "fxs" -> formatMenu(fxsConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod)
                    else -> throw IllegalArgumentException()
                }
                CLOSED_LOOP_RAMP_V -> when(device){
                    "fx" -> formatMenu(fxConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod)
                    "fxs" -> formatMenu(fxsConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod)
                    else -> throw IllegalArgumentException()
                }
                PEAK_FWD_I -> when(device){
                    "fx" -> formatMenu(fxConfig.TorqueCurrent.PeakForwardTorqueCurrent)
                    else -> throw IllegalArgumentException()
                }
                PEAK_REV_I -> when(device){
                    "fx" -> formatMenu(fxConfig.TorqueCurrent.PeakReverseTorqueCurrent)
                    else -> throw IllegalArgumentException()
                }
                TORQUE_NEUTRAL_DEADBAND -> when(device){
                    "fx" -> formatMenu(fxConfig.TorqueCurrent.TorqueNeutralDeadband)
                    else -> throw  IllegalArgumentException()
                }
                OPEN_LOOP_RAMP_I -> when(device){
                    "fx" -> formatMenu(fxConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod)
                    "fxs" -> formatMenu(fxsConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod)
                    else -> throw IllegalArgumentException()
                }
                CLOSED_LOOP_RAMP_I -> when(device){
                    "fx" -> formatMenu(fxConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod)
                    "fxs" -> formatMenu(fxsConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod)
                    else -> throw IllegalArgumentException()
                }

                FWD_SOFT_EN -> when(device){
                    "fx" -> formatMenu(fxConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable)
                    "fxs" -> formatMenu(fxsConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable)
                    else -> throw IllegalArgumentException()
                }
                FWD_SOFT_THRES -> when(device){
                    "fx" -> formatMenu(fxConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold)
                    "fxs" -> formatMenu(fxsConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold)
                    else -> throw IllegalArgumentException()
                }
                REV_SOFT_EN -> when(device){
                    "fx" -> formatMenu(fxConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable)
                    "fxs" -> formatMenu(fxsConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable)
                    else -> throw IllegalArgumentException()
                }
                REV_SOFT_THRES -> when(device){
                    "fx" -> formatMenu(fxConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold)
                    "fxs" -> formatMenu(fxsConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold)
                    else -> throw IllegalArgumentException()
                }

                FWD_HARD_EN -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ForwardLimitEnable)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ForwardLimitEnable)
                    else -> throw IllegalArgumentException()
                }
                FWD_REMOTE_ID -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ForwardLimitRemoteSensorID)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ForwardLimitRemoteSensorID)
                    else -> throw IllegalArgumentException()
                }
                FWD_AUTOSET_POS -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable)
                    else -> throw IllegalArgumentException()
                }
                FWD_AUTOSET_POS_VALUE -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionValue)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionValue)
                    else -> throw IllegalArgumentException()
                }
                REV_HARD_EN -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ReverseLimitEnable)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ReverseLimitEnable)
                    else -> throw IllegalArgumentException()
                }
                REV_REMOTE_ID -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ReverseLimitRemoteSensorID)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ReverseLimitRemoteSensorID)
                    else -> throw IllegalArgumentException()
                }
                REV_AUTOSET_POS -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable)
                    else -> throw IllegalArgumentException()
                }
                REV_AUTOSET_POS_VALUE -> when(device){
                    "fx" -> formatMenu(fxConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionValue)
                    "fxs" -> formatMenu(fxsConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionValue)
                    else -> throw IllegalArgumentException()
                }

                ALLOW_MUSIC_DIS -> when(device){
                    "fx" -> formatMenu(fxConfig.Audio.AllowMusicDurDisable)
                    "fxs" -> formatMenu(fxsConfig.Audio.AllowMusicDurDisable)
                    else -> throw IllegalArgumentException()
                }
                BEEP_ON_BOOT -> when(device){
                    "fx" -> formatMenu(fxConfig.Audio.BeepOnBoot)
                    "fxs" -> formatMenu(fxsConfig.Audio.BeepOnBoot)
                    else -> throw IllegalArgumentException()
                }
                BEEP_ON_CONFIG -> when(device){
                    "fx" -> formatMenu(fxConfig.Audio.BeepOnConfig)
                    "fxs" -> formatMenu(fxsConfig.Audio.BeepOnConfig)
                    else -> throw IllegalArgumentException()
                }
                POSITION -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0
                        else talonFxFDService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0, DOUBLE_FORMAT_4
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0
                        else talonFxsFDService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0, DOUBLE_FORMAT_4
                    )
                    else -> throw IllegalArgumentException()
                }
                VELOCITY -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeVelocity
                        else talonFxFDService.activeVelocity
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0
                        else talonFxsFDService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0, DOUBLE_FORMAT_4
                    )
                    else -> throw IllegalArgumentException()
                }
                ACCELERATION -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeAcceleration
                        else talonFxFDService.activeAcceleration
                    )
                    "fxs" -> formatMenu(
                        if(bus =="rio") talonFxsService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0
                        else talonFxsFDService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0, DOUBLE_FORMAT_4
                    )
                    else -> throw IllegalArgumentException()
                }
                JERK -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeJerk
                        else talonFxFDService.activeJerk
                    )
                    "fxs" -> formatMenu(
                        if(bus =="rio") talonFxsService.activeJerk
                        else talonFxsFDService.activeJerk
                    )
                    else -> throw IllegalArgumentException()
                }
                TORQUE_CURRENT_DEADBAND -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeTorqueCurrentDeadband
                        else talonFxFDService.activeTorqueCurrentDeadband
                    )
                    "fxs" -> formatMenu(
                        if(bus =="rio") talonFxsService.activeTorqueCurrentDeadband
                        else talonFxsFDService.activeTorqueCurrentDeadband
                    )
                    else -> throw IllegalArgumentException()
                }
                TORQUE_CURRENT_MAX -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeTorqueCurrentMaxOut
                        else talonFxFDService.activeTorqueCurrentMaxOut
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeTorqueCurrentMaxOut
                        else talonFxsFDService.activeTorqueCurrentMaxOut
                    )
                    else -> throw IllegalArgumentException()
                }
                FEED_FORWARD -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeFeedForward
                        else talonFxFDService.activeFeedForward
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeFeedForward
                        else talonFxsFDService.activeFeedForward
                    )
                    else -> throw IllegalArgumentException()
                }
                OPPOSE_MAIN -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeOpposeMain
                        else talonFxFDService.activeOpposeMain
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeOpposeMain
                        else talonFxsFDService.activeOpposeMain
                    )
                    else -> throw IllegalArgumentException()
                }
                DIFFERENTIAL_SLOT -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeDifferentialSlot
                        else talonFxFDService.activeDifferentialSlot
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeDifferentialSlot
                        else talonFxsFDService.activeDifferentialSlot
                    )
                    else -> throw IllegalArgumentException()
                }
                DIFFERENTIAL_TARGET -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeDifferentialTarget
                        else talonFxFDService.activeDifferentialTarget
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeDifferentialTarget
                        else talonFxsFDService.activeDifferentialTarget
                    )
                    else -> throw IllegalArgumentException()
                }
                FOC -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeFOC
                        else talonFxFDService.activeFOC
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeFOC
                        else talonFxsFDService.activeFOC
                    )
                    else -> throw IllegalArgumentException()
                }
                OVERRIDE_NEUTRAL -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.activeOverrideNeutral
                        else talonFxFDService.activeOverrideNeutral
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeOverrideNeutral
                        else talonFxsFDService.activeOverrideNeutral
                    )
                    else -> throw IllegalArgumentException()
                }
                LIM_FWD_MOT -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.limFwdMotion
                        else talonFxFDService.limFwdMotion
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.limFwdMotion
                        else talonFxsFDService.limFwdMotion
                    )
                    else -> throw IllegalArgumentException()
                }
                LIM_REV_MOT -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.limRevMotion
                        else talonFxFDService.limRevMotion
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.limRevMotion
                        else talonFxsFDService.limRevMotion
                    )
                    else -> throw IllegalArgumentException()
                }
                GRAPHER_FRAME -> when(device){
                    "fx" -> formatMenu(
                        if(bus == "rio") talonFxService.grapherStatusFrameHz
                        else talonFxFDService.grapherStatusFrameHz
                    )
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.grapherStatusFrameHz
                        else talonFxsFDService.grapherStatusFrameHz
                    )
                    else -> throw IllegalArgumentException()
                }

                ABS_SENSOR_DISCONTINUITY -> when(device) {
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeConfiguration.ExternalFeedback.AbsoluteSensorDiscontinuityPoint
                        else talonFxsFDService.activeConfiguration.ExternalFeedback.AbsoluteSensorDiscontinuityPoint
                    )
                    else -> throw IllegalArgumentException()
                }
                ABS_SENSOR_OFFSET -> when(device) {
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeConfiguration.ExternalFeedback.AbsoluteSensorOffset
                        else talonFxsFDService.activeConfiguration.ExternalFeedback.AbsoluteSensorOffset
                    )
                    else -> throw IllegalArgumentException()
                }
                QUAD_EDGE_PER_ROT -> when(device) {
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeConfiguration.ExternalFeedback.QuadratureEdgesPerRotation
                        else talonFxsFDService.activeConfiguration.ExternalFeedback.QuadratureEdgesPerRotation
                    )
                    else -> throw IllegalArgumentException()
                }
                VELOCITY_FILTER_TIME_CONST -> when(device) {
                    "fxs" -> formatMenu(
                        if(bus=="rio") talonFxsService.activeConfiguration.ExternalFeedback.VelocityFilterTimeConstant
                        else talonFxsFDService.activeConfiguration.ExternalFeedback.VelocityFilterTimeConstant
                    )
                    else -> throw IllegalArgumentException()
                }

                else -> TODO("${param.enum} not implemented")
            }
        }

    override fun execute(): Command {
        val timeout = talonFxService.timeout

        var fxConfig: TalonFXConfiguration = TalonFXConfiguration()
        var activeSlotIndex: Int = 0
        var fxsConfig: TalonFXSConfiguration = TalonFXSConfiguration()
        if(device == "fx") {
            fxConfig = if(bus == "rio") talonFxService.activeConfiguration else talonFxFDService.activeConfiguration
            activeSlotIndex = if(bus == "rio") talonFxService.activeSlotIndex else talonFxFDService.activeSlotIndex
            logger.info { "Current Param: ${param.name}, ${param.prompt}" }
        } else if (device == "fxs") {
            fxsConfig = if(bus == "rio") talonFxsService.activeConfiguration else talonFxsFDService.activeConfiguration
            activeSlotIndex = if(bus == "rio") talonFxsService.activeSlotIndex else talonFxsFDService.activeSlotIndex
            logger.info { "Current Param: ${param.name}, ${param.prompt}" }
        } else throw IllegalArgumentException("Invalid device type")

        when(param.enum) {
            ROTOR_OFFSET -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.Feedback.FeedbackRotorOffset) {
                        talonFx, value ->
                    fxConfig.Feedback.FeedbackRotorOffset = value
                    talonFx.configurator.apply(fxConfig.Feedback)
                }
                else -> throw IllegalArgumentException()
            }
            SENSOR_TO_MECH_RATIO -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.Feedback.SensorToMechanismRatio) {
                        talonFx, value ->
                    fxConfig.Feedback.SensorToMechanismRatio = value
                    talonFx.configurator.apply(fxConfig.Feedback)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.ExternalFeedback.SensorToMechanismRatio) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.SensorToMechanismRatio = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }
            ROTOR_TO_SENSOR_RATIO -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.Feedback.RotorToSensorRatio) {
                        talonFx, value ->
                    fxConfig.Feedback.RotorToSensorRatio = value
                    talonFx.configurator.apply(fxConfig.Feedback)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.ExternalFeedback.RotorToSensorRatio) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.RotorToSensorRatio = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }
            REMOTE_SENSOR_ID -> when(device){
                "fx" -> configFXIntParam(fxConfig.Feedback.FeedbackRemoteSensorID) {
                        talonFx, value ->
                    fxConfig.Feedback.FeedbackRemoteSensorID = value
                    talonFx.configurator.apply(fxConfig.Feedback)
                }
                "fxs" -> configFXSIntParam(fxsConfig.ExternalFeedback.FeedbackRemoteSensorID) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.FeedbackRemoteSensorID = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }

            SLOT_KP -> when(device){
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kP) { talonFx, value ->
                            fxConfig.Slot0.kP = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kP) { talonFx, value ->
                            fxConfig.Slot1.kP = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot2.kP) { talonFx, value ->
                            fxConfig.Slot2.kP = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kP) { talonFXS, value ->
                            fxsConfig.Slot0.kP = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kP) { talonFXS, value ->
                            fxsConfig.Slot1.kP = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kP) { talonFXS, value ->
                            fxsConfig.Slot2.kP = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw IllegalArgumentException()
            }
            SLOT_KI -> when(device) {
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kI) { talonFx, value ->
                            fxConfig.Slot0.kI = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kI) { talonFx, value ->
                            fxConfig.Slot1.kI = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot0.kI) { talonFx, value ->
                            fxConfig.Slot2.kI = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kI) { talonFXS, value ->
                            fxsConfig.Slot0.kI = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kI) { talonFXS, value ->
                            fxsConfig.Slot1.kI = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kI) { talonFXS, value ->
                            fxsConfig.Slot2.kI = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw IllegalArgumentException()
            }
            SLOT_KD -> when(device) {
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kD) { talonFx, value ->
                            fxConfig.Slot0.kD = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kD) { talonFx, value ->
                            fxConfig.Slot1.kD = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot2.kD) { talonFx, value ->
                            fxConfig.Slot2.kD = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kD) { talonFXS, value ->
                            fxsConfig.Slot0.kD = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kD) { talonFXS, value ->
                            fxsConfig.Slot1.kD = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kD) { talonFXS, value ->
                            fxsConfig.Slot2.kD = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw IllegalArgumentException()
            }
            SLOT_KS -> when(device) {
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kS) { talonFx, value ->
                            fxConfig.Slot0.kS = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kS) { talonFx, value ->
                            fxConfig.Slot1.kS = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot2.kS) { talonFx, value ->
                            fxConfig.Slot2.kS = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kS) { talonFXS, value ->
                            fxsConfig.Slot0.kS = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kS) { talonFXS, value ->
                            fxsConfig.Slot1.kS = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kS) { talonFXS, value ->
                            fxsConfig.Slot2.kS = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw  IllegalArgumentException()
            }
            SLOT_KV -> when(device) {
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kV) { talonFx, value ->
                            fxConfig.Slot0.kV = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kV) { talonFx, value ->
                            fxConfig.Slot1.kV = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot2.kV) { talonFx, value ->
                            fxConfig.Slot2.kV = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kV) { talonFXS, value ->
                            fxsConfig.Slot0.kV = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kV) { talonFXS, value ->
                            fxsConfig.Slot1.kV = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kV) { talonFXS, value ->
                            fxsConfig.Slot2.kV = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw IllegalArgumentException()
            }
            SLOT_KA -> when(device) {
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kA) { talonFx, value ->
                            fxConfig.Slot0.kA = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kA) { talonFx, value ->
                            fxConfig.Slot1.kA = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot2.kA) { talonFx, value ->
                            fxConfig.Slot2.kA = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kA) { talonFXS, value ->
                            fxsConfig.Slot0.kA = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kA) { talonFXS, value ->
                            fxsConfig.Slot1.kA = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kA) { talonFXS, value ->
                            fxsConfig.Slot2.kA = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw IllegalArgumentException()
            }
            SLOT_KG -> when(device) {
                "fx" -> {
                    when (activeSlotIndex) {
                        0 -> configFXDoubleParam(fxConfig.Slot0.kG) { talonFx, value ->
                            fxConfig.Slot0.kG = value
                            talonFx.configurator.apply(fxConfig.Slot0)
                        }
                        1 -> configFXDoubleParam(fxConfig.Slot1.kG) { talonFx, value ->
                            fxConfig.Slot1.kG = value
                            talonFx.configurator.apply(fxConfig.Slot1)
                        }
                        2 -> configFXDoubleParam(fxConfig.Slot2.kG) { talonFx, value ->
                            fxConfig.Slot2.kG = value
                            talonFx.configurator.apply(fxConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                "fxs" -> {
                    when(activeSlotIndex) {
                        0 -> configFXSDoubleParam(fxsConfig.Slot0.kG) { talonFXS, value ->
                            fxsConfig.Slot0.kG = value
                            talonFXS.configurator.apply(fxsConfig.Slot0)
                        }
                        1 -> configFXSDoubleParam(fxsConfig.Slot1.kG) { talonFXS, value ->
                            fxsConfig.Slot1.kG = value
                            talonFXS.configurator.apply(fxsConfig.Slot1)
                        }
                        2 -> configFXSDoubleParam(fxsConfig.Slot2.kG) { talonFXS, value ->
                            fxsConfig.Slot2.kG = value
                            talonFXS.configurator.apply(fxsConfig.Slot2)
                        }
                        else -> TODO("$activeSlotIndex is not a valid slot")
                    }
                }
                else -> throw IllegalArgumentException()
            }

            MM_ACCEL -> when(device) {
                "fx" -> configFXDoubleParam(fxConfig.MotionMagic.MotionMagicAcceleration) {
                        talonFx, value ->
                    fxConfig.MotionMagic.MotionMagicAcceleration = value
                    talonFx.configurator.apply(fxConfig.MotionMagic)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotionMagic.MotionMagicAcceleration) { talonFXS, value ->
                    fxsConfig.MotionMagic.MotionMagicAcceleration = value
                    talonFXS.configurator.apply(fxsConfig.MotionMagic)
                }
                else -> throw IllegalArgumentException()
            }
            MM_CRUISE_VEL -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotionMagic.MotionMagicCruiseVelocity) {
                        talonFx, value ->
                    fxConfig.MotionMagic.MotionMagicCruiseVelocity = value
                    talonFx.configurator.apply(fxConfig.MotionMagic)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotionMagic.MotionMagicCruiseVelocity) { talonFXS, value ->
                    fxsConfig.MotionMagic.MotionMagicCruiseVelocity = value
                    talonFXS.configurator.apply(fxsConfig.MotionMagic)
                }
                else -> throw IllegalArgumentException()
            }
            MM_JERK -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotionMagic.MotionMagicJerk) {
                        talonFx, value ->
                    fxConfig.MotionMagic.MotionMagicJerk = value
                    talonFx.configurator.apply(fxConfig.MotionMagic)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotionMagic.MotionMagicJerk) { talonFXS, value ->
                    fxsConfig.MotionMagic.MotionMagicJerk = value
                    talonFXS.configurator.apply(fxsConfig.MotionMagic)
                }
                else -> throw IllegalArgumentException()
            }
            MM_EXPO_KA -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotionMagic.MotionMagicExpo_kA) {
                        talonFx, value ->
                    fxConfig.MotionMagic.MotionMagicExpo_kA = value
                    talonFx.configurator.apply(fxConfig.MotionMagic)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotionMagic.MotionMagicExpo_kA) { talonFXS, value ->
                    fxsConfig.MotionMagic.MotionMagicExpo_kA = value
                    talonFXS.configurator.apply(fxsConfig.MotionMagic)
                }
                else -> throw IllegalArgumentException()
            }
            MM_EXPO_KV -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotionMagic.MotionMagicExpo_kV) {
                        talonFx, value ->
                    fxConfig.MotionMagic.MotionMagicExpo_kV = value
                    talonFx.configurator.apply(fxConfig.MotionMagic)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotionMagic.MotionMagicExpo_kV) { talonFXS, value ->
                    fxsConfig.MotionMagic.MotionMagicExpo_kV = value
                    talonFXS.configurator.apply(fxsConfig.MotionMagic)
                }
                else -> throw IllegalArgumentException()
            }

            PEAK_DIFF_DC -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.DifferentialConstants.PeakDifferentialDutyCycle) {
                        talonFx, value ->
                    fxConfig.DifferentialConstants.PeakDifferentialDutyCycle = value
                    talonFx.configurator.apply(fxConfig.DifferentialConstants)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.DifferentialConstants.PeakDifferentialDutyCycle) { talonFXS, value ->
                    fxsConfig.DifferentialConstants.PeakDifferentialDutyCycle = value
                    talonFXS.configurator.apply(fxsConfig.DifferentialConstants)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_DIFF_TORQUE -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.DifferentialConstants.PeakDifferentialTorqueCurrent) {
                        talonFx, value ->
                    fxConfig.DifferentialConstants.PeakDifferentialTorqueCurrent = value
                    talonFx.configurator.apply(fxConfig.DifferentialConstants)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.DifferentialConstants.PeakDifferentialTorqueCurrent) { talonFXS, value ->
                    fxsConfig.DifferentialConstants.PeakDifferentialTorqueCurrent = value
                    talonFXS.configurator.apply(fxsConfig.DifferentialConstants)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_DIFF_VOLT -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.DifferentialConstants.PeakDifferentialVoltage) {
                        talonFx, value ->
                    fxConfig.DifferentialConstants.PeakDifferentialVoltage = value
                    talonFx.configurator.apply(fxConfig.DifferentialConstants)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.DifferentialConstants.PeakDifferentialVoltage) { talonFXS, value ->
                    fxsConfig.DifferentialConstants.PeakDifferentialVoltage = value
                    talonFXS.configurator.apply(fxsConfig.DifferentialConstants)
                }
                else -> throw IllegalArgumentException()
            }
            DIFF_SENSOR_REMOTE_ID -> when(device){
                "fx" -> configFXIntParam(fxConfig.DifferentialSensors.DifferentialRemoteSensorID) {
                        talonFx, value ->
                    fxConfig.DifferentialSensors.DifferentialRemoteSensorID = value
                    talonFx.configurator.apply(fxConfig.DifferentialSensors)
                }
                "fxs" -> configFXSIntParam(fxsConfig.DifferentialSensors.DifferentialRemoteSensorID) { talonFXS, value ->
                    fxsConfig.DifferentialSensors.DifferentialRemoteSensorID = value
                    talonFXS.configurator.apply(fxsConfig.DifferentialSensors)
                }
                else -> throw IllegalArgumentException()
            }
            DIFF_FX_ID -> when(device){
                "fx" -> configFXIntParam(fxConfig.DifferentialSensors.DifferentialTalonFXSensorID) {
                        talonFx, value ->
                    fxConfig.DifferentialSensors.DifferentialTalonFXSensorID = value
                    talonFx.configurator.apply(fxConfig.DifferentialSensors)
                }
                "fxs" -> configFXSIntParam(fxsConfig.DifferentialSensors.DifferentialTalonFXSensorID) { talonFXS, value ->
                    fxsConfig.DifferentialSensors.DifferentialTalonFXSensorID = value
                    talonFXS.configurator.apply(fxsConfig.DifferentialSensors)
                }
                else -> throw IllegalArgumentException()
            }

            STATOR_LIM_EN -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.CurrentLimits.StatorCurrentLimitEnable){
                        talonFx, value ->
                    fxConfig.CurrentLimits.StatorCurrentLimitEnable = value
                    talonFx.configurator.apply(fxConfig.CurrentLimits)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.CurrentLimits.StatorCurrentLimitEnable) { talonFXS, value ->
                    fxsConfig.CurrentLimits.StatorCurrentLimitEnable = value
                    talonFXS.configurator.apply(fxsConfig.CurrentLimits)
                }
                else -> throw IllegalArgumentException()
            }
            STATOR_LIM -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.CurrentLimits.StatorCurrentLimit) {
                        talonFx, value ->
                    fxConfig.CurrentLimits.StatorCurrentLimit = value
                    talonFx.configurator.apply(fxConfig.CurrentLimits)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.CurrentLimits.StatorCurrentLimit) { talonFXS, value ->
                    fxsConfig.CurrentLimits.StatorCurrentLimit = value
                    talonFXS.configurator.apply(fxsConfig.CurrentLimits)
                }
                else -> throw IllegalArgumentException()
            }
            SUPP_LIM_EN -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.CurrentLimits.SupplyCurrentLimitEnable) {
                        talonFx, value ->
                    fxConfig.CurrentLimits.SupplyCurrentLimitEnable = value
                    talonFx.configurator.apply(fxConfig.CurrentLimits)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.CurrentLimits.SupplyCurrentLimitEnable) { talonFXS, value ->
                    fxsConfig.CurrentLimits.SupplyCurrentLimitEnable = value
                    talonFXS.configurator.apply(fxsConfig.CurrentLimits)
                }
                else -> throw IllegalArgumentException()
            }
            SUPP_LOWER_LIM -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.CurrentLimits.SupplyCurrentLowerLimit) {
                        talonFx, value ->
                    fxConfig.CurrentLimits.SupplyCurrentLowerLimit = value
                    talonFx.configurator.apply(fxConfig.CurrentLimits)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.CurrentLimits.SupplyCurrentLowerLimit) { talonFXS, value ->
                    fxsConfig.CurrentLimits.SupplyCurrentLowerLimit = value
                    talonFXS.configurator.apply(fxsConfig.CurrentLimits)
                }
                else -> throw IllegalArgumentException()
            }
            SUPP_LIM -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.CurrentLimits.SupplyCurrentLimit) {
                        talonFx, value ->
                    fxConfig.CurrentLimits.SupplyCurrentLimit = value
                    talonFx.configurator.apply(fxConfig.CurrentLimits)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.CurrentLimits.SupplyCurrentLimit) { talonFXS, value ->
                    fxsConfig.CurrentLimits.SupplyCurrentLimit = value
                    talonFXS.configurator.apply(fxsConfig.CurrentLimits)
                }
                else -> throw IllegalArgumentException()
            }
            SUPP_TRIP_TIME -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.CurrentLimits.SupplyCurrentLowerTime) {
                        talonFx, value ->
                    fxConfig.CurrentLimits.SupplyCurrentLowerTime = value
                    talonFx.configurator.apply(fxConfig.CurrentLimits)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.CurrentLimits.SupplyCurrentLowerTime) { talonFXS, value ->
                    fxsConfig.CurrentLimits.SupplyCurrentLowerTime = value
                    talonFXS.configurator.apply(fxsConfig.CurrentLimits)
                }
                else -> throw IllegalArgumentException()
            }

            CLOSED_LOOP_RAMP_DC -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod) {
                        talonFx, value ->
                    fxConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = value
                    talonFx.configurator.apply(fxConfig.ClosedLoopRamps)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod) { talonFXS, value ->
                    fxsConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = value
                    talonFXS.configurator.apply(fxsConfig.ClosedLoopRamps)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_FWD_DC -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotorOutput.PeakForwardDutyCycle) {
                        talonFx, value ->
                    fxConfig.MotorOutput.PeakForwardDutyCycle = value
                    talonFx.configurator.apply(fxConfig.MotorOutput)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotorOutput.PeakForwardDutyCycle) { talonFXS, value ->
                    fxsConfig.MotorOutput.PeakForwardDutyCycle = value
                    talonFXS.configurator.apply(fxsConfig.MotorOutput)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_REV_DC -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotorOutput.PeakReverseDutyCycle) {
                        talonFx, value ->
                    fxConfig.MotorOutput.PeakReverseDutyCycle = value
                    talonFx.configurator.apply(fxConfig.MotorOutput)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotorOutput.PeakReverseDutyCycle) { talonFXS, value ->
                    fxsConfig.MotorOutput.PeakReverseDutyCycle = value
                    talonFXS.configurator.apply(fxsConfig.MotorOutput)
                }
                else -> throw IllegalArgumentException()
            }
            NEUTRAL_DEADBAND_DC -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.MotorOutput.DutyCycleNeutralDeadband) {
                        talonFx, value ->
                    fxConfig.MotorOutput.DutyCycleNeutralDeadband = value
                    talonFx.configurator.apply(fxConfig.MotorOutput)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.MotorOutput.DutyCycleNeutralDeadband) { talonFXS, value ->
                    fxsConfig.MotorOutput.DutyCycleNeutralDeadband = value
                    talonFXS.configurator.apply(fxsConfig.MotorOutput)
                }
                else -> throw IllegalArgumentException()
            }
            OPEN_LOOP_RAMP_DC -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod) {
                        talonFx, value ->
                    fxConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = value
                    talonFx.configurator.apply(fxConfig.OpenLoopRamps)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod) { talonFXS, value ->
                    fxsConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = value
                    talonFXS.configurator.apply(fxsConfig.OpenLoopRamps)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_FWD_V -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.Voltage.PeakForwardVoltage) {
                        talonFx, value ->
                    fxConfig.Voltage.PeakForwardVoltage = value
                    talonFx.configurator.apply(fxConfig.Voltage)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.Voltage.PeakForwardVoltage) { talonFXS, value ->
                    fxsConfig.Voltage.PeakForwardVoltage = value
                    talonFXS.configurator.apply(fxsConfig.Voltage)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_REV_V -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.Voltage.PeakReverseVoltage) {
                        talonFx, value ->
                    fxConfig.Voltage.PeakReverseVoltage = value
                    talonFx.configurator.apply(fxConfig.Voltage)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.Voltage.PeakReverseVoltage) { talonFXS, value ->
                    fxsConfig.Voltage.PeakReverseVoltage = value
                    talonFXS.configurator.apply(fxsConfig.Voltage)
                }
                else -> throw IllegalArgumentException()
            }
            SUPPLY_V_TIME_CONST -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.Voltage.SupplyVoltageTimeConstant) {
                        talonFx, value ->
                    fxConfig.Voltage.SupplyVoltageTimeConstant = value
                    talonFx.configurator.apply(fxConfig.Voltage)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.Voltage.SupplyVoltageTimeConstant) { talonFXS, value ->
                    fxsConfig.Voltage.SupplyVoltageTimeConstant = value
                    talonFXS.configurator.apply(fxsConfig.Voltage)
                }
                else -> throw IllegalArgumentException()
            }
            OPEN_LOOP_RAMP_V -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod) {
                        talonFx, value ->
                    fxConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = value
                    talonFx.configurator.apply(fxConfig.OpenLoopRamps)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod) { talonFXS, value ->
                    fxsConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = value
                    talonFXS.configurator.apply(fxsConfig.OpenLoopRamps)
                }
                else -> throw IllegalArgumentException()
            }
            CLOSED_LOOP_RAMP_V -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod) {
                        talonFx, value ->
                    fxConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = value
                    talonFx.configurator.apply(fxConfig.ClosedLoopRamps)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod) { talonFXS, value ->
                    fxsConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = value
                    talonFXS.configurator.apply(fxsConfig.ClosedLoopRamps)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_FWD_I -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.TorqueCurrent.PeakForwardTorqueCurrent) {
                        talonFx, value ->
                    fxConfig.TorqueCurrent.PeakForwardTorqueCurrent = value
                    talonFx.configurator.apply(fxConfig.TorqueCurrent)
                }
                else -> throw IllegalArgumentException()
            }
            PEAK_REV_I -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.TorqueCurrent.PeakReverseTorqueCurrent) {
                        talonFx, value ->
                    fxConfig.TorqueCurrent.PeakReverseTorqueCurrent = value
                    talonFx.configurator.apply(fxConfig.TorqueCurrent)
                }
                else -> throw IllegalArgumentException()
            }
            TORQUE_NEUTRAL_DEADBAND -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.TorqueCurrent.TorqueNeutralDeadband) {
                        talonFx, value ->
                    fxConfig.TorqueCurrent.TorqueNeutralDeadband = value
                    talonFx.configurator.apply(fxConfig.TorqueCurrent)
                }
                else -> throw IllegalArgumentException()
            }
            OPEN_LOOP_RAMP_I -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod) {
                        talonFx, value ->
                    fxConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod = value
                    talonFx.configurator.apply(fxConfig.OpenLoopRamps)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod) { talonFXS, value ->
                    fxsConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod = value
                    talonFXS.configurator.apply(fxsConfig.OpenLoopRamps)
                }
                else -> throw IllegalArgumentException()
            }
            CLOSED_LOOP_RAMP_I -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod) {
                        talonFx, value ->
                    fxConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod = value
                    talonFx.configurator.apply(fxConfig.ClosedLoopRamps)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod) { talonFXS, value ->
                    fxsConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod = value
                    talonFXS.configurator.apply(fxsConfig.ClosedLoopRamps)
                }
                else -> throw IllegalArgumentException()
            }
            CONTINUOUS_WRAP -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.ClosedLoopGeneral.ContinuousWrap){
                        talonFx, value ->
                    fxConfig.ClosedLoopGeneral.ContinuousWrap = value
                    talonFx.configurator.apply(fxConfig.ClosedLoopGeneral)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.ClosedLoopGeneral.ContinuousWrap) { talonFXS, value ->
                    fxsConfig.ClosedLoopGeneral.ContinuousWrap = value
                    talonFXS.configurator.apply(fxsConfig.ClosedLoopGeneral)
                }
                else -> throw IllegalArgumentException()
            }

            FWD_SOFT_EN -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable) {
                        talonFx, value ->
                    fxConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = value
                    talonFx.configurator.apply(fxConfig.SoftwareLimitSwitch)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable) { talonFXS, value ->
                    fxsConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = value
                    talonFXS.configurator.apply(fxsConfig.SoftwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            FWD_SOFT_THRES -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold) {
                        talonFx, value ->
                    fxConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = value
                    talonFx.configurator.apply(fxConfig.SoftwareLimitSwitch)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold) { talonFXS, value ->
                    fxsConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = value
                    talonFXS.configurator.apply(fxsConfig.SoftwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            REV_SOFT_EN -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable) {
                        talonFx, value ->
                    fxConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = value
                    talonFx.configurator.apply(fxConfig.SoftwareLimitSwitch)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable) { talonFXS, value ->
                    fxsConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = value
                    talonFXS.configurator.apply(fxsConfig.SoftwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            REV_SOFT_THRES -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold) {
                        talonFx, value ->
                    fxConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = value
                    talonFx.configurator.apply(fxConfig.SoftwareLimitSwitch)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold) { talonFXS, value ->
                    fxsConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = value
                    talonFXS.configurator.apply(fxsConfig.SoftwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }

            FWD_HARD_EN -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.HardwareLimitSwitch.ForwardLimitEnable) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ForwardLimitEnable = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.HardwareLimitSwitch.ForwardLimitEnable) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ForwardLimitEnable = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            FWD_REMOTE_ID -> when(device){
                "fx" -> configFXIntParam(fxConfig.HardwareLimitSwitch.ForwardLimitRemoteSensorID) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ForwardLimitRemoteSensorID = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSIntParam(fxsConfig.HardwareLimitSwitch.ForwardLimitRemoteSensorID) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ForwardLimitRemoteSensorID = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            FWD_AUTOSET_POS -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            FWD_AUTOSET_POS_VALUE -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionValue) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionValue = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionValue) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ForwardLimitAutosetPositionValue = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            REV_HARD_EN -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.HardwareLimitSwitch.ReverseLimitEnable) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ReverseLimitEnable = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.HardwareLimitSwitch.ReverseLimitEnable) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ReverseLimitEnable = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            REV_REMOTE_ID -> when(device){
                "fx" -> configFXIntParam(fxConfig.HardwareLimitSwitch.ReverseLimitRemoteSensorID) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ReverseLimitRemoteSensorID = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSIntParam(fxsConfig.HardwareLimitSwitch.ReverseLimitRemoteSensorID) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ReverseLimitRemoteSensorID = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            REV_AUTOSET_POS -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }
            REV_AUTOSET_POS_VALUE -> when(device){
                "fx" -> configFXDoubleParam(fxConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionValue) {
                        talonFx, value ->
                    fxConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionValue = value
                    talonFx.configurator.apply(fxConfig.HardwareLimitSwitch)
                }
                "fxs" -> configFXSDoubleParam(fxsConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionValue) { talonFXS, value ->
                    fxsConfig.HardwareLimitSwitch.ReverseLimitAutosetPositionValue = value
                    talonFXS.configurator.apply(fxsConfig.HardwareLimitSwitch)
                }
                else -> throw IllegalArgumentException()
            }

            ALLOW_MUSIC_DIS -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.Audio.AllowMusicDurDisable) {
                        talonFx, value ->
                    fxConfig.Audio.AllowMusicDurDisable = value
                    talonFx.configurator.apply(fxConfig.Audio)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.Audio.AllowMusicDurDisable) { talonFXS, value ->
                    fxsConfig.Audio.AllowMusicDurDisable = value
                    talonFXS.configurator.apply(fxsConfig.Audio)
                }
                else -> throw IllegalArgumentException()
            }
            BEEP_ON_BOOT -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.Audio.BeepOnBoot) {
                        talonFx, value ->
                    fxConfig.Audio.BeepOnBoot = value
                    talonFx.configurator.apply(fxConfig.Audio)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.Audio.BeepOnBoot) { talonFXS, value ->
                    fxsConfig.Audio.BeepOnBoot = value
                    talonFXS.configurator.apply(fxsConfig.Audio)
                }
                else -> throw IllegalArgumentException()
            }
            BEEP_ON_CONFIG -> when(device){
                "fx" -> configFXBooleanParam(fxConfig.Audio.BeepOnConfig) {
                        talonFx, value ->
                    fxConfig.Audio.BeepOnConfig = value
                    talonFx.configurator.apply(fxConfig.Audio)
                }
                "fxs" -> configFXSBooleanParam(fxsConfig.Audio.BeepOnConfig) { talonFXS, value ->
                    fxsConfig.Audio.BeepOnConfig = value
                    talonFXS.configurator.apply(fxsConfig.Audio)
                }
                else -> throw IllegalArgumentException()
            }
            POSITION -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0
                    else talonFxFDService.active.firstOrNull()?.position?.valueAsDouble?: 2767.0) {
                        talonFx, value ->
                    if(bus=="rio") talonFxService.active.forEach{
                        it.setPosition(value, timeout)
                    }
                    else talonFxFDService.active.forEach {
                        it.setPosition(value, timeout)
                    }
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0
                    else talonFxsFDService.active.firstOrNull()?.position?.valueAsDouble ?: 2767.0) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.active.forEach {
                        it.setPosition(value, timeout)
                    } else talonFxsFDService.active.forEach {
                        it.setPosition(value, timeout)
                    }
                }
                else -> throw IllegalArgumentException()
            }
            VELOCITY -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeVelocity
                    else talonFxFDService.activeVelocity) { talonFx, value ->
                    if(bus=="rio") talonFxService.activeVelocity = value
                    else talonFxFDService.activeVelocity = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeVelocity
                    else talonFxsFDService.activeVelocity) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeVelocity = value
                    else talonFxsFDService.activeVelocity = value
                }
                else -> throw IllegalArgumentException()
            }
            ACCELERATION -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeAcceleration
                    else talonFxFDService.activeAcceleration){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeAcceleration = value
                    else talonFxFDService.activeAcceleration = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeAcceleration
                    else talonFxsFDService.activeAcceleration) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeAcceleration = value
                    else talonFxsFDService.activeAcceleration = value
                }
                else -> throw IllegalArgumentException()
            }
            JERK -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeJerk
                    else talonFxFDService.activeJerk){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeJerk = value
                    else talonFxFDService.activeJerk = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeJerk
                    else talonFxsFDService.activeJerk) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeJerk = value
                    else talonFxsFDService.activeJerk = value
                }
                else -> throw IllegalArgumentException()
            }
            TORQUE_CURRENT_DEADBAND -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeTorqueCurrentDeadband
                    else talonFxFDService.activeTorqueCurrentDeadband){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeTorqueCurrentDeadband = value
                    else talonFxFDService.activeTorqueCurrentDeadband = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeTorqueCurrentDeadband
                    else talonFxsFDService.activeTorqueCurrentDeadband) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeTorqueCurrentDeadband = value
                    else talonFxsFDService.activeTorqueCurrentDeadband = value
                }
                else -> throw IllegalArgumentException()
            }
            TORQUE_CURRENT_MAX -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeTorqueCurrentMaxOut
                    else talonFxsFDService.activeTorqueCurrentMaxOut){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeTorqueCurrentMaxOut = value
                    talonFxFDService.activeTorqueCurrentMaxOut = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeTorqueCurrentMaxOut
                    else talonFxsFDService.activeTorqueCurrentMaxOut) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeTorqueCurrentMaxOut = value
                    else talonFxsFDService.activeTorqueCurrentMaxOut = value
                }
                else -> throw IllegalArgumentException()
            }
            FEED_FORWARD -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeFeedForward
                    else talonFxFDService.activeFeedForward){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeFeedForward = value
                    talonFxFDService.activeFeedForward = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeFeedForward
                    else talonFxsFDService.activeFeedForward) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeFeedForward = value
                    else talonFxsFDService.activeFeedForward = value
                }
                else -> throw IllegalArgumentException()
            }
            OPPOSE_MAIN -> when(device){
                "fx" -> configFXBooleanParam(
                    if(bus=="rio") talonFxService.activeOpposeMain
                    else talonFxFDService.activeOpposeMain){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeOpposeMain = value
                    else talonFxFDService.activeOpposeMain = value
                }
                "fxs" -> configFXSBooleanParam(
                    if(bus=="rio") talonFxsService.activeOpposeMain
                    else talonFxsFDService.activeOpposeMain) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeOpposeMain = value
                    else talonFxsFDService.activeOpposeMain = value
                }
                else -> throw IllegalArgumentException()
            }
            DIFFERENTIAL_SLOT -> when(device){
                "fx" -> configFXIntParam(
                    if(bus=="rio") talonFxService.activeDifferentialSlot
                    else talonFxFDService.activeDifferentialSlot){ talonFx, value ->
                    if(value >= 0 && value <= 2){
                        if(bus=="rio") talonFxService.activeDifferentialSlot = value
                        else talonFxFDService.activeDifferentialSlot = value
                    } else terminal.warn("${value} is not a valid slot index, must be 0-2")
                }
                "fxs" -> configFXSIntParam(
                    if(bus=="rio") talonFxsService.activeDifferentialSlot
                    else talonFxsFDService.activeDifferentialSlot) { talonFXS, value ->
                    if(value >= 0 && value <= 2) {
                        if(bus=="rio") talonFxsService.activeDifferentialSlot = value
                        else talonFxsFDService.activeDifferentialSlot = value
                    } else terminal.warn("${value} is not a valid slot index, must be 0-2")
                }
                else -> throw IllegalArgumentException()
            }
            DIFFERENTIAL_TARGET -> when(device){
                "fx" -> configFXDoubleParam(
                    if(bus=="rio") talonFxService.activeDifferentialTarget
                    else talonFxFDService.activeDifferentialTarget){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeDifferentialTarget = value
                    else talonFxFDService.activeDifferentialTarget = value
                }
                "fxs" -> configFXSDoubleParam(
                    if(bus=="rio") talonFxsService.activeDifferentialTarget
                    else talonFxsFDService.activeDifferentialTarget) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeDifferentialTarget = value
                    else talonFxsFDService.activeDifferentialTarget = value
                }
                else -> throw IllegalArgumentException()
            }
            FOC -> when(device){
                "fx" -> configFXBooleanParam(
                    if(bus=="rio") talonFxService.activeFOC
                    else talonFxFDService.activeFOC){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeFOC = value
                    else talonFxFDService.activeFOC = value
                }
                "fxs" -> configFXSBooleanParam(
                    if(bus=="rio") talonFxsService.activeFOC
                    else talonFxsFDService.activeFOC) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeFOC = value
                    else talonFxsFDService.activeFOC = value
                }
                else -> throw IllegalArgumentException()
            }
            OVERRIDE_NEUTRAL -> when(device){
                "fx" -> configFXBooleanParam(
                    if(bus=="rio") talonFxService.activeOverrideNeutral
                    else talonFxFDService.activeOverrideNeutral){ talonFx, value ->
                    if(bus=="rio") talonFxService.activeOverrideNeutral = value
                    else talonFxFDService.activeOverrideNeutral = value
                }
                "fxs" -> configFXSBooleanParam(
                    if(bus=="rio") talonFxsService.activeOverrideNeutral
                    else talonFxsFDService.activeOverrideNeutral) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.activeOverrideNeutral = value
                    else talonFxsFDService.activeOverrideNeutral = value
                }
                else -> throw IllegalArgumentException()
            }
            LIM_FWD_MOT -> when(device){
                "fx" -> configFXBooleanParam(
                    if(bus=="rio") talonFxService.limFwdMotion
                    else talonFxFDService.limFwdMotion) { talonFx, value ->
                    if(bus=="rio") talonFxService.limFwdMotion = value
                    else talonFxFDService.limFwdMotion = value
                }
                "fxs" -> configFXSBooleanParam(
                    if(bus=="rio") talonFxsService.limFwdMotion
                    else talonFxsFDService.limFwdMotion) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.limFwdMotion = value
                    else talonFxsFDService.limFwdMotion = value
                }
                else -> throw IllegalArgumentException()
            }
            LIM_REV_MOT -> when(device){
                "fx" -> configFXBooleanParam(
                    if(bus=="rio") talonFxService.limRevMotion
                    else talonFxFDService.limRevMotion) { talonFx, value ->
                    if(bus=="rio") talonFxService.limRevMotion = value
                    else talonFxFDService.limRevMotion = value
                }
                "fxs" -> configFXSBooleanParam(
                    if(bus=="rio") talonFxsService.limRevMotion
                    else talonFxsFDService.limRevMotion) { talonFXS, value ->
                    if(bus=="rio") talonFxsService.limRevMotion = value
                    else talonFxsFDService.limRevMotion = value
                }
                else -> throw IllegalArgumentException()
            }
            GRAPHER_FRAME -> when(device) {
                "fx" -> configFXDoubleParam(
                    if (bus == "rio") talonFxService.grapherStatusFrameHz
                    else talonFxFDService.grapherStatusFrameHz
                ) { talonFx, value ->
                    if (bus == "rio") talonFxService.grapherStatusFrameHz = value
                    else talonFxFDService.grapherStatusFrameHz = value

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

                "fxs" -> configFXSDoubleParam(
                    if (bus == "rio") talonFxsService.grapherStatusFrameHz
                    else talonFxsFDService.grapherStatusFrameHz
                ) { talonFXS, value ->
                    if (bus == "rio") talonFxsService.grapherStatusFrameHz = value
                    else talonFxsFDService.grapherStatusFrameHz = value
                    logger.info { "Grapher Frame Value: " + value }

                    talonFXS.acceleration.setUpdateFrequency(value, timeout)
                    talonFXS.bridgeOutput.setUpdateFrequency(value, timeout)
                    talonFXS.deviceTemp.setUpdateFrequency(value, timeout)
                    talonFXS.differentialAveragePosition.setUpdateFrequency(value, timeout)
                    talonFXS.differentialAverageVelocity.setUpdateFrequency(value, timeout)
                    talonFXS.differentialDifferencePosition.setUpdateFrequency(value, timeout)
                    talonFXS.differentialDifferenceVelocity.setUpdateFrequency(value, timeout)
                    talonFXS.dutyCycle.setUpdateFrequency(value, timeout)
                    talonFXS.forwardLimit.setUpdateFrequency(value, timeout)
                    talonFXS.reverseLimit.setUpdateFrequency(value, timeout)
                    talonFXS.isProLicensed.setUpdateFrequency(value, timeout)
                    talonFXS.motionMagicIsRunning.setUpdateFrequency(value, timeout)
                    talonFXS.motorVoltage.setUpdateFrequency(value, timeout)
                    talonFXS.position.setUpdateFrequency(value, timeout)
                    talonFXS.processorTemp.setUpdateFrequency(value, timeout)
                    talonFXS.rotorPosition.setUpdateFrequency(value, timeout)
                    talonFXS.rotorVelocity.setUpdateFrequency(value, timeout)
                    talonFXS.statorCurrent.setUpdateFrequency(value, timeout)
                    talonFXS.supplyCurrent.setUpdateFrequency(value, timeout)
                    talonFXS.supplyVoltage.setUpdateFrequency(value, timeout)
                    talonFXS.torqueCurrent.setUpdateFrequency(value, timeout)
                    talonFXS.velocity.setUpdateFrequency(value, timeout)

                    talonFXS.closedLoopDerivativeOutput.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopError.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopFeedForward.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopIntegratedOutput.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopOutput.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopProportionalOutput.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopReference.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopReferenceSlope.setUpdateFrequency(value, timeout)
                    talonFXS.closedLoopSlot.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopDerivativeOutput.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopError.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopFeedForward.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopIntegratedOutput.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopOutput.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopProportionalOutput.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopReference.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopReferenceSlope.setUpdateFrequency(value, timeout)
                    talonFXS.differentialClosedLoopSlot.setUpdateFrequency(value, timeout)

                    talonFXS.ancillaryDeviceTemp.setUpdateFrequency(value, timeout)
                    talonFXS.externalMotorTemp.setUpdateFrequency(value, timeout)


                    talonFXS.rawPulseWidthPosition.setUpdateFrequency(value,timeout)
                    logger.info { "Before new frames" }
                    talonFXS.rawQuadraturePosition.setUpdateFrequency(value, timeout)
                    talonFXS.rawPulseWidthVelocity.setUpdateFrequency(value, timeout)
                    talonFXS.rawQuadratureVelocity.setUpdateFrequency(value, timeout)
                }

                else -> throw IllegalArgumentException()
            }
            ABS_SENSOR_DISCONTINUITY -> when(device) {
                "fxs" -> configFXSDoubleParam(fxsConfig.ExternalFeedback.AbsoluteSensorDiscontinuityPoint) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.AbsoluteSensorDiscontinuityPoint = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }
            ABS_SENSOR_OFFSET -> when(device) {
                "fxs" -> configFXSDoubleParam(fxsConfig.ExternalFeedback.AbsoluteSensorOffset) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.AbsoluteSensorOffset = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }
            QUAD_EDGE_PER_ROT -> when(device) {
                "fxs" -> configFXSIntParam(fxsConfig.ExternalFeedback.QuadratureEdgesPerRotation) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.QuadratureEdgesPerRotation = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }
            VELOCITY_FILTER_TIME_CONST -> when(device) {
                "fxs" -> configFXSDoubleParam(fxsConfig.ExternalFeedback.VelocityFilterTimeConstant) { talonFxs, value ->
                    fxsConfig.ExternalFeedback.VelocityFilterTimeConstant = value
                    talonFxs.configurator.apply(fxsConfig.ExternalFeedback)
                }
                else -> throw IllegalArgumentException()
            }

            else -> TODO("${param.name} not implemented")

        }
        return super.execute()
    }

    private fun configFXDoubleParam(default: Double, configure: (TalonFX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        if(bus == "rio") {
            talonFxService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxService.active.size} talonFx's ${param.name}: $paramValue" }
        } else if(bus == "canivore") {
            talonFxFDService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxFDService.active.size} talonFx's ${param.name}: $paramValue" }
        } else throw  IllegalArgumentException()
    }

    private fun configFXSDoubleParam(default: Double, configure: (TalonFXS, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        if(bus == "rio") {
            talonFxsService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxsService.active.size} talonFXS's ${param.name}: $paramValue" }
        } else if(bus == "canivore") {
            talonFxsFDService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxsFDService.active.size} talonFXS's ${param.name}: $paramValue" }
        } else throw  IllegalArgumentException()
    }


    private fun configFXIntParam(default: Int, configure: (TalonFX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        if(bus == "rio") {
            talonFxService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxService.active.size} talonFx's  ${param.name}: $paramValue" }
        } else if(bus == "canivore") {
            talonFxFDService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxFDService.active.size} talonFx's ${param.name}: $paramValue" }
        } else throw  IllegalArgumentException()
    }

    private fun configFXSIntParam(default: Int, configure: (TalonFXS, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        if(bus == "rio") {
            talonFxsService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxsService.active.size} talonFXS's  ${param.name}: $paramValue" }
        } else if(bus == "canivore") {
            talonFxsFDService.active.forEach { configure(it, paramValue) }
            logger.info { "set ${talonFxsFDService.active.size} talonFXS's ${param.name}: $paramValue" }
        } else throw  IllegalArgumentException()
    }

    private fun configFXBooleanParam(default: Boolean, configure: (TalonFX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        if(bus == "rio") {
            talonFxService.active.forEach { configure(it, paramValue) }
            logger.info { "Set ${talonFxService.active.size} talonFx's ${param.name}: $paramValue" }
        } else if(bus == "canivore") {
            talonFxFDService.active.forEach { configure(it, paramValue) }
            logger.info { "Set ${talonFxFDService.active.size} talonFx's ${param.name}: $paramValue" }
        } else throw IllegalArgumentException()
    }

    private fun configFXSBooleanParam(default: Boolean, configure: (TalonFXS, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        if(bus == "rio") {
            talonFxsService.active.forEach { configure(it, paramValue) }
            logger.info { "Set ${talonFxsService.active.size} talonFXS's ${param.name}: $paramValue" }
        } else if(bus == "canivore") {
            talonFxsFDService.active.forEach { configure(it, paramValue) }
            logger.info { "Set ${talonFxsFDService.active.size} XS's ${param.name}: $paramValue" }
        } else throw IllegalArgumentException()
    }



}