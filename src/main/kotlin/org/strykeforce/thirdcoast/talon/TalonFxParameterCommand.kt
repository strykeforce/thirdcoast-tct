package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.can.TalonFX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.TalonFXService

private val logger = KotlinLogging.logger {}

class TalonFXParameterCommand(
        parent: Command?,
        key: String,
        toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFXService by inject()
    private val timeout = talonFxService.timeout
    private val param = TalonFxParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            val config = talonFxService.activeConfiguration
            val slot = talonFxService.activeSlot

            return when(param.enum){
                TalonFxParameter.fxEnum.SLOT_P -> formatMenu(slot.kP, DOUBLE_FORMAT_4)
                TalonFxParameter.fxEnum.SLOT_I -> formatMenu(slot.kI, DOUBLE_FORMAT_4)
                TalonFxParameter.fxEnum.SLOT_D -> formatMenu(slot.kD, DOUBLE_FORMAT_4)
                TalonFxParameter.fxEnum.SLOT_F -> formatMenu(slot.kF, DOUBLE_FORMAT_4)
                TalonFxParameter.fxEnum.SLOT_I_ZONE -> formatMenu(slot.integralZone)
                TalonFxParameter.fxEnum.SLOT_ALLOWABLE_ERR -> formatMenu(slot.allowableClosedloopError)
                TalonFxParameter.fxEnum.SLOT_MAX_I_ACCUM -> formatMenu(slot.maxIntegralAccumulator)
                TalonFxParameter.fxEnum.SLOT_PEAK_OUTPUT -> formatMenu(slot.closedLoopPeakOutput)
                TalonFxParameter.fxEnum.OUTPUT_REVERSED -> formatMenu(talonFxService.outputInverted)
                TalonFxParameter.fxEnum.OPEN_LOOP_RAMP -> formatMenu(config.openloopRamp)
                TalonFxParameter.fxEnum.CLOSED_LOOP_RAMP -> formatMenu(config.closedloopRamp)
                TalonFxParameter.fxEnum.PEAK_OUTPUT_FORWARD -> formatMenu(config.peakOutputForward)
                TalonFxParameter.fxEnum.PEAK_OUTPUT_REVERSE -> formatMenu(config.peakOutputReverse)
                TalonFxParameter.fxEnum.NOMINAL_OUTPUT_FORWARD -> formatMenu(config.nominalOutputForward)
                TalonFxParameter.fxEnum.NOMINAL_OUTPUT_REVERSE -> formatMenu(config.nominalOutputReverse)
                TalonFxParameter.fxEnum.NEUTRAL_DEADBAND -> formatMenu(config.neutralDeadband)
                TalonFxParameter.fxEnum.VOLTAGE_COMP_ENABLE -> formatMenu(talonFxService.voltageCompensation)
                TalonFxParameter.fxEnum.VOLTAGE_COMP_SATURATION -> formatMenu(config.voltageCompSaturation)
                TalonFxParameter.fxEnum.VOLTAGE_MEASUREMENT_FILTER -> formatMenu(config.voltageMeasurementFilter)
                TalonFxParameter.fxEnum.MOTION_CRUISE_VELOCITY -> formatMenu(config.motionCruiseVelocity)
                TalonFxParameter.fxEnum.MOTION_ACCELERATION -> formatMenu(config.motionAcceleration)
                TalonFxParameter.fxEnum.SENSOR_PHASE -> formatMenu(talonFxService.sensorPhase)
                TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT_ENABLE -> formatMenu(config.statorCurrLimit.enable)
                TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT -> formatMenu(config.statorCurrLimit.currentLimit)
                TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT_THRES_CURRENT -> formatMenu(config.statorCurrLimit.triggerThresholdCurrent)
                TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT_THRES_TIME -> formatMenu(config.statorCurrLimit.triggerThresholdTime)
                TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT_ENABLE -> formatMenu(config.supplyCurrLimit.enable)
                TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT -> formatMenu(config.supplyCurrLimit.currentLimit)
                TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT_THRES_CURRENT -> formatMenu(config.supplyCurrLimit.triggerThresholdCurrent)
                TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT_THRES_TIME -> formatMenu(config.supplyCurrLimit.triggerThresholdTime)
                TalonFxParameter.fxEnum.STATUS_GENERAL -> formatMenu(defaultFor(StatusFrameEnhanced.Status_1_General))
                TalonFxParameter.fxEnum.STATUS_FEEDBACK0 -> formatMenu(defaultFor(StatusFrameEnhanced.Status_2_Feedback0))
                TalonFxParameter.fxEnum.STATUS_QUAD_ENCODER -> formatMenu(defaultFor(StatusFrameEnhanced.Status_3_Quadrature))
                TalonFxParameter.fxEnum.STATUS_AIN_TEMP_VBAT -> formatMenu(defaultFor(StatusFrameEnhanced.Status_4_AinTempVbat))
                TalonFxParameter.fxEnum.STATUS_PULSE_WIDTH -> formatMenu(defaultFor(StatusFrameEnhanced.Status_8_PulseWidth))
                TalonFxParameter.fxEnum.STATUS_MOTION -> formatMenu(defaultFor(StatusFrameEnhanced.Status_10_MotionMagic))
                TalonFxParameter.fxEnum.STATUS_PIDF0 -> formatMenu(defaultFor(StatusFrameEnhanced.Status_13_Base_PIDF0))
                TalonFxParameter.fxEnum.SOFT_LIMIT_ENABLE_FORWARD -> formatMenu(config.forwardSoftLimitEnable)
                TalonFxParameter.fxEnum.SOFT_LIMIT_THRESHOLD_FORWARD -> formatMenu(config.forwardSoftLimitThreshold)
                TalonFxParameter.fxEnum.SOFT_LIMIT_ENABLE_REVERSE -> formatMenu(config.reverseSoftLimitEnable)
                TalonFxParameter.fxEnum.SOFT_LIMIT_THRESHOLD_REVERSE -> formatMenu(config.reverseSoftLimitThreshold)
                TalonFxParameter.fxEnum.VELOCITY_MEASUREMENT_WINDOW -> formatMenu(config.velocityMeasurementWindow)
                TalonFxParameter.fxEnum.INTEGRATED_SENSOR_OFFSET_DEGREES -> formatMenu(config.integratedSensorOffsetDegrees)
                TalonFxParameter.fxEnum.FACTORY_DEFAULTS -> tomlMenu
                else -> TODO("${param.enum} not implemented")

            }
        }

    override fun execute(): Command {
        val config = talonFxService.activeConfiguration
        val slot = talonFxService.activeSlot
        config.absoluteSensorRange
        config.initializationStrategy
        config.motorCommutation


        when(param.enum){
            TalonFxParameter.fxEnum.SLOT_P -> configDoubleParam(slot.kP){ talonFx, value ->
                talonFx.config_kP(talonFxService.activeSlotIndex, value, timeout)
                slot.kP = value
            }
            TalonFxParameter.fxEnum.SLOT_I -> configDoubleParam(slot.kI){ talonFX, value ->
                talonFX.config_kI(talonFxService.activeSlotIndex, value, timeout)
                talonFX.setIntegralAccumulator(0.0, 0, timeout)
                slot.kI = value
            }
            TalonFxParameter.fxEnum.SLOT_D -> configDoubleParam(slot.kD){ talonFx, value ->
                talonFx.config_kD(talonFxService.activeSlotIndex, value, timeout)
                slot.kD = value
            }
            TalonFxParameter.fxEnum.SLOT_I_ZONE -> configIntParam(slot.integralZone){ talonFx, value ->
                talonFx.config_IntegralZone(talonFxService.activeSlotIndex, value, timeout)
                slot.integralZone = value
            }
            TalonFxParameter.fxEnum.SLOT_ALLOWABLE_ERR -> configIntParam(slot.allowableClosedloopError){ talonFx, value ->
                talonFx.configAllowableClosedloopError(talonFxService.activeSlotIndex, value, timeout)
                slot.allowableClosedloopError = value
            }
            TalonFxParameter.fxEnum.SLOT_MAX_I_ACCUM -> configDoubleParam(slot.maxIntegralAccumulator){ talonFx, value ->
                talonFx.configMaxIntegralAccumulator(talonFxService.activeSlotIndex, value, timeout)
                slot.maxIntegralAccumulator = value
            }
            TalonFxParameter.fxEnum.SLOT_PEAK_OUTPUT -> configDoubleParam(slot.closedLoopPeakOutput){ talonFx, value ->
                talonFx.configClosedLoopPeakOutput(talonFxService.activeSlotIndex, value, timeout)
                slot.closedLoopPeakOutput = value
            }
            TalonFxParameter.fxEnum.OUTPUT_REVERSED -> configBooleanParam(talonFxService.outputInverted){ talonFx, value ->
                talonFx.inverted = value
            }
            TalonFxParameter.fxEnum.OPEN_LOOP_RAMP -> configDoubleParam(config.openloopRamp){ talonFx, value ->
                talonFx.configOpenloopRamp(value, timeout)
                config.openloopRamp = value
            }
            TalonFxParameter.fxEnum.CLOSED_LOOP_RAMP -> configDoubleParam(config.closedloopRamp){ talonFx, value ->
                talonFx.configClosedloopRamp(value, timeout)
                config.closedloopRamp = value
            }
            TalonFxParameter.fxEnum.PEAK_OUTPUT_FORWARD -> configDoubleParam(config.peakOutputForward){ talonFx, value ->
                talonFx.configPeakOutputForward(value, timeout)
                config.peakOutputForward = value
            }
            TalonFxParameter.fxEnum.PEAK_OUTPUT_REVERSE -> configDoubleParam(config.peakOutputReverse){ talonFx, value ->
                talonFx.configPeakOutputReverse(value, timeout)
                config.peakOutputReverse = value
            }
            TalonFxParameter.fxEnum.NOMINAL_OUTPUT_FORWARD -> configDoubleParam(config.nominalOutputForward){ talonFx, value ->
                talonFx.configNominalOutputForward(value, timeout)
                config.nominalOutputForward = value
            }
            TalonFxParameter.fxEnum.NOMINAL_OUTPUT_REVERSE -> configDoubleParam(config.nominalOutputReverse){ talonFx, value ->
                talonFx.configNominalOutputReverse(value, timeout)
                config.nominalOutputReverse = value
            }
            TalonFxParameter.fxEnum.NEUTRAL_DEADBAND -> configDoubleParam(config.neutralDeadband){ talonFx, value ->
                talonFx.configNeutralDeadband(value, timeout)
                config.neutralDeadband = value
            }
            TalonFxParameter.fxEnum.VOLTAGE_COMP_ENABLE -> configBooleanParam(talonFxService.voltageCompensation){ talonFx, value ->
                talonFx.enableVoltageCompensation(value)
                talonFxService.voltageCompensation = value
            }
            TalonFxParameter.fxEnum.VOLTAGE_COMP_SATURATION -> configDoubleParam(config.voltageCompSaturation){ talonFx, value ->
                talonFx.configVoltageCompSaturation(value, timeout)
                config.voltageCompSaturation = value
            }
            TalonFxParameter.fxEnum.VOLTAGE_MEASUREMENT_FILTER -> configIntParam(config.voltageMeasurementFilter){ talonFx, value ->
                talonFx.configVoltageMeasurementFilter(value, timeout)
                config.voltageMeasurementFilter = value
            }
            TalonFxParameter.fxEnum.MOTION_CRUISE_VELOCITY -> configIntParam(config.motionCruiseVelocity){ talonFx, value ->
                talonFx.configMotionCruiseVelocity(value, timeout)
                config.motionCruiseVelocity = value
            }
            TalonFxParameter.fxEnum.MOTION_ACCELERATION -> configIntParam(config.motionAcceleration){ talonFx, value ->
                talonFx.configMotionAcceleration(value, timeout)
                config.motionAcceleration = value
            }
            TalonFxParameter.fxEnum.SENSOR_PHASE -> configBooleanParam(talonFxService.sensorPhase){ talonFx, value ->
                talonFx.setSensorPhase(value)
                talonFxService.sensorPhase = value
            }
            TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT_ENABLE -> configBooleanParam(config.statorCurrLimit.enable){ talonFx, value ->
                config.statorCurrLimit.enable = value
                talonFx.configStatorCurrentLimit(config.statorCurrLimit,timeout)
            }
            TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT -> configDoubleParam(config.statorCurrLimit.currentLimit){ talonFx, value ->
                config.statorCurrLimit.currentLimit = value
                talonFx.configStatorCurrentLimit(config.statorCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT_THRES_CURRENT -> configDoubleParam(config.statorCurrLimit.triggerThresholdCurrent){ talonFx, value ->
                config.statorCurrLimit.triggerThresholdCurrent = value
                talonFx.configStatorCurrentLimit(config.statorCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.STATOR_CURRENT_LIMIT_THRES_TIME -> configDoubleParam(config.statorCurrLimit.triggerThresholdTime){ talonFx, value ->
                config.statorCurrLimit.triggerThresholdTime = value
                talonFx.configStatorCurrentLimit(config.statorCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT_ENABLE -> configBooleanParam(config.supplyCurrLimit.enable){ talonFx, value ->
                config.supplyCurrLimit.enable = value
                talonFx.configSupplyCurrentLimit(config.supplyCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT -> configDoubleParam(config.supplyCurrLimit.currentLimit){ talonFx, value ->
                config.supplyCurrLimit.currentLimit = value
                talonFx.configSupplyCurrentLimit(config.supplyCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT_THRES_CURRENT -> configDoubleParam(config.supplyCurrLimit.triggerThresholdCurrent){ talonFx, value ->
                config.supplyCurrLimit.triggerThresholdCurrent = value
                talonFx.configSupplyCurrentLimit(config.supplyCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.SUPPLY_CURRENT_LIMIT_THRES_TIME -> configDoubleParam(config.supplyCurrLimit.triggerThresholdTime){ talonFx, value ->
                config.supplyCurrLimit.triggerThresholdTime = value
                talonFx.configSupplyCurrentLimit(config.supplyCurrLimit, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_GENERAL -> configIntParam(defaultFor(StatusFrameEnhanced.Status_1_General)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, value, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_FEEDBACK0 -> configIntParam(defaultFor(StatusFrameEnhanced.Status_2_Feedback0)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, value, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_QUAD_ENCODER -> configIntParam(defaultFor(StatusFrameEnhanced.Status_3_Quadrature)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, value, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_AIN_TEMP_VBAT -> configIntParam(defaultFor(StatusFrameEnhanced.Status_4_AinTempVbat)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, value, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_PULSE_WIDTH -> configIntParam(defaultFor(StatusFrameEnhanced.Status_8_PulseWidth)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, value, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_MOTION -> configIntParam(defaultFor(StatusFrameEnhanced.Status_10_MotionMagic)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, value, timeout)
            }
            TalonFxParameter.fxEnum.STATUS_PIDF0 -> configIntParam(defaultFor(StatusFrameEnhanced.Status_13_Base_PIDF0)){ talonFx, value ->
                talonFx.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, value, timeout)
            }
            TalonFxParameter.fxEnum.SOFT_LIMIT_ENABLE_FORWARD -> configBooleanParam(config.forwardSoftLimitEnable){ talonFx, value ->
                talonFx.configForwardSoftLimitEnable(value, timeout)
                config.forwardSoftLimitEnable = value
            }
            TalonFxParameter.fxEnum.SOFT_LIMIT_THRESHOLD_FORWARD -> configIntParam(config.forwardSoftLimitThreshold){ talonFx, value ->
                talonFx.configForwardSoftLimitThreshold(value, timeout)
                config.forwardSoftLimitThreshold = value
            }
            TalonFxParameter.fxEnum.SOFT_LIMIT_ENABLE_REVERSE -> configBooleanParam(config.reverseSoftLimitEnable){ talonFx, value ->
                talonFx.configReverseSoftLimitEnable(value, timeout)
                config.reverseSoftLimitEnable = value
            }
            TalonFxParameter.fxEnum.SOFT_LIMIT_THRESHOLD_REVERSE -> configIntParam(config.reverseSoftLimitThreshold){ talonFx, value ->
                talonFx.configReverseSoftLimitThreshold(value, timeout)
                config.reverseSoftLimitThreshold = value
            }
            TalonFxParameter.fxEnum.VELOCITY_MEASUREMENT_WINDOW -> configIntParam(config.velocityMeasurementWindow){ talonFx, value ->
                talonFx.configVelocityMeasurementWindow(value, timeout)
                config.velocityMeasurementWindow = value
            }
            TalonFxParameter.fxEnum.INTEGRATED_SENSOR_OFFSET_DEGREES -> configDoubleParam(config.integratedSensorOffsetDegrees){ talonFx, value ->
                talonFx.configIntegratedSensorOffset(value, timeout)
                config.integratedSensorOffsetDegrees = value
            }
            TalonFxParameter.fxEnum.FACTORY_DEFAULTS -> configBooleanParam(false){ talonFx, value ->
                if(value) talonFx.configFactoryDefault(timeout)
            }
            else -> TODO("${param.enum} not implemented")
        }
        return super.execute()
    }


    private fun configDoubleParam(default: Double, config: (TalonFX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonFxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
    }

    private fun configIntParam(default: Int, config: (TalonFX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonFxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
    }

    private fun configBooleanParam(default: Boolean, config: (TalonFX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        talonFxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
    }

    private fun defaultFor(frame: StatusFrameEnhanced): Int =
            talonFxService.active.first().getStatusFramePeriod(frame)

}