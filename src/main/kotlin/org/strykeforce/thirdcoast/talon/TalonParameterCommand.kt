package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.*
import com.ctre.phoenix.motorcontrol.can.*
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.talon.TalonParameter.Enum.*

private val logger = KotlinLogging.logger {}

class TalonParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()
    private val talonFxService: TalonFxService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonService.timeout
    private val param = TalonParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            val baseConfig: BaseTalonConfiguration
            val slot: SlotConfiguration
            when (type) {
                "srx" -> {
                    baseConfig = talonService.activeConfiguration
                    slot = talonService.activeSlot
                }
                "fx" -> {
                    baseConfig = talonFxService.activeConfiguration
                    slot = talonFxService.activeSlot
                }
                else -> throw IllegalArgumentException()
            }
            val srxConfig = talonService.activeConfiguration
            val fxConfig = talonFxService.activeConfiguration
            return when (param.enum) {
                SLOT_P -> formatMenu(slot.kP, DOUBLE_FORMAT_4)
                SLOT_I -> formatMenu(slot.kI, DOUBLE_FORMAT_4)
                SLOT_D -> formatMenu(slot.kD, DOUBLE_FORMAT_4)
                SLOT_F -> formatMenu(slot.kF, DOUBLE_FORMAT_4)
                SLOT_I_ZONE -> formatMenu(slot.integralZone)
                SLOT_ALLOWABLE_ERR -> formatMenu(slot.allowableClosedloopError)
                SLOT_MAX_I_ACCUM -> formatMenu(slot.maxIntegralAccumulator)
                SLOT_PEAK_OUTPUT -> formatMenu(slot.closedLoopPeakOutput)
                OUTPUT_REVERSED -> when(type){
                    "srx" -> formatMenu(talonService.outputInverted)
                    "fx" -> formatMenu(talonFxService.outputInverted)
                    else -> throw IllegalArgumentException()
                }
                OPEN_LOOP_RAMP -> formatMenu(baseConfig.openloopRamp)
                CLOSED_LOOP_RAMP -> formatMenu(baseConfig.closedloopRamp)
                PEAK_OUTPUT_FORWARD -> formatMenu(baseConfig.peakOutputForward)
                PEAK_OUTPUT_REVERSE -> formatMenu(baseConfig.peakOutputReverse)
                NOMINAL_OUTPUT_FORWARD -> formatMenu(baseConfig.nominalOutputForward)
                NOMINAL_OUTPUT_REVERSE -> formatMenu(baseConfig.nominalOutputReverse)
                NEUTRAL_DEADBAND -> formatMenu(baseConfig.neutralDeadband)
                VOLTAGE_COMP_ENABLE -> when (type) {
                    "srx" -> formatMenu(talonService.voltageCompensation)
                    "fx" -> formatMenu(talonFxService.voltageCompensation)
                    else -> throw IllegalArgumentException()
                }
                VOLTAGE_COMP_SATURATION -> formatMenu(baseConfig.voltageCompSaturation)
                VOLTAGE_MEASUREMENT_FILTER -> formatMenu(baseConfig.voltageMeasurementFilter)
                MOTION_CRUISE_VELOCITY -> formatMenu(baseConfig.motionCruiseVelocity)
                MOTION_ACCELERATION -> formatMenu(baseConfig.motionAcceleration)
                SENSOR_PHASE -> when (type) {
                    "srx" -> formatMenu(talonService.sensorPhase)
                    "fx" -> formatMenu(talonFxService.sensorPhase)
                    else -> throw IllegalArgumentException()
                }
                CURRENT_LIMIT_ENABLE -> formatMenu(talonService.currentLimit)
                CURRENT_LIMIT_CONT -> formatMenu(srxConfig.continuousCurrentLimit)
                CURRENT_LIMIT_PEAK -> formatMenu(srxConfig.peakCurrentLimit)
                CURRENT_LIMIT_PEAK_DURATION -> formatMenu(srxConfig.peakCurrentDuration)
                STATOR_CURRENT_LIMIT_ENABLE -> formatMenu(fxConfig.statorCurrLimit.enable)
                STATOR_CURRENT_LIMIT -> formatMenu(fxConfig.statorCurrLimit.currentLimit)
                STATOR_CURRENT_LIMIT_THRES_CURRENT -> formatMenu(fxConfig.statorCurrLimit.triggerThresholdCurrent)
                STATOR_CURRENT_LIMIT_THRES_TIME -> formatMenu(fxConfig.statorCurrLimit.triggerThresholdTime)
                SUPPLY_CURRENT_LIMIT_ENABLE -> when (type) {
                    "srx" -> formatMenu(talonService.supplyCurrentLimit.enable)
                    "fx" -> formatMenu(fxConfig.supplyCurrLimit.enable)
                    else -> throw  IllegalArgumentException()
                }
                SUPPLY_CURRENT_LIMIT -> when (type) {
                    "srx" -> formatMenu(talonService.supplyCurrentLimit.currentLimit)
                    "fx" -> formatMenu(fxConfig.supplyCurrLimit.currentLimit)
                    else -> throw IllegalArgumentException()
                }
                SUPPLY_CURRENT_LIMIT_THRES_CURRENT -> when (type) {
                    "srx" -> formatMenu(talonService.supplyCurrentLimit.triggerThresholdCurrent)
                    "fx" -> formatMenu(fxConfig.supplyCurrLimit.triggerThresholdCurrent)
                    else -> throw IllegalArgumentException()
                }
                SUPPLY_CURRENT_LIMIT_THRES_TIME -> when (type) {
                    "srx" -> formatMenu(talonService.supplyCurrentLimit.triggerThresholdTime)
                    "fx" -> formatMenu(fxConfig.supplyCurrLimit.triggerThresholdTime)
                    else -> throw IllegalArgumentException()
                }
                STATUS_GENERAL -> formatMenu(defaultFor(Status_1_General))
                STATUS_FEEDBACK0 -> formatMenu(defaultFor(Status_2_Feedback0))
                STATUS_QUAD_ENCODER -> formatMenu(defaultFor(Status_3_Quadrature))
                STATUS_AIN_TEMP_VBAT -> formatMenu(defaultFor(Status_4_AinTempVbat))
                STATUS_PULSE_WIDTH -> formatMenu(defaultFor(Status_8_PulseWidth))
                STATUS_MOTION -> formatMenu(defaultFor(Status_10_MotionMagic))
                STATUS_PIDF0 -> formatMenu(defaultFor(Status_13_Base_PIDF0))
                SOFT_LIMIT_ENABLE_FORWARD -> formatMenu(baseConfig.forwardSoftLimitEnable)
                SOFT_LIMIT_ENABLE_REVERSE -> formatMenu(baseConfig.reverseSoftLimitEnable)
                SOFT_LIMIT_THRESHOLD_FORWARD -> formatMenu(baseConfig.forwardSoftLimitThreshold)
                SOFT_LIMIT_THRESHOLD_REVERSE -> formatMenu(baseConfig.reverseSoftLimitThreshold)
                VELOCITY_MEASUREMENT_WINDOW -> formatMenu(baseConfig.velocityMeasurementWindow)
                INTEGRATED_SENSOR_OFFSET_DEGREES -> formatMenu(fxConfig.integratedSensorOffsetDegrees)
                FACTORY_DEFAULTS -> tomlMenu
                else -> TODO("${param.enum} not implemented")
            }
        }

    override fun execute(): Command {
        val config: BaseTalonConfiguration
        val slot: SlotConfiguration
        val activeSlotIndex: Int
        val timeout: Int
        when (type) {
            "srx" -> {
                config = talonService.activeConfiguration
                slot = talonService.activeSlot
                activeSlotIndex = talonService.activeSlotIndex
                timeout = talonService.timeout
            }
            "fx" -> {
                config = talonFxService.activeConfiguration
                slot = talonFxService.activeSlot
                activeSlotIndex = talonFxService.activeSlotIndex
                timeout = talonFxService.timeout
            }
            else -> throw IllegalArgumentException()
        }
        val srxConfig = talonService.activeConfiguration
        val fxConfig = talonFxService.activeConfiguration

        when (param.enum) {
            SLOT_P -> configDoubleParam(slot.kP) { baseTalon, value ->
                baseTalon.config_kP(activeSlotIndex, value, timeout)
                slot.kP = value
            }
            SLOT_I -> configDoubleParam(slot.kI) { baseTalon, value ->
                baseTalon.config_kI(activeSlotIndex, value, timeout)
                baseTalon.setIntegralAccumulator(0.0, 0, timeout)
                slot.kI = value
            }
            SLOT_D -> configDoubleParam(slot.kD) { baseTalon, value ->
                baseTalon.config_kD(activeSlotIndex, value, timeout)
                slot.kD = value
            }
            SLOT_F -> configDoubleParam(slot.kF) { baseTalon, value ->
                baseTalon.config_kF(activeSlotIndex, value, timeout)
                slot.kF = value
            }
            SLOT_I_ZONE -> configIntParam(slot.integralZone) { baseTalon, value ->
                baseTalon.config_IntegralZone(activeSlotIndex, value, timeout)
                slot.integralZone = value
            }
            SLOT_ALLOWABLE_ERR -> configIntParam(slot.allowableClosedloopError) { baseTalon, value ->
                baseTalon.configAllowableClosedloopError(activeSlotIndex, value, timeout)
                slot.allowableClosedloopError = value
            }
            SLOT_MAX_I_ACCUM -> configDoubleParam(slot.maxIntegralAccumulator) { baseTalon, value ->
                baseTalon.configMaxIntegralAccumulator(activeSlotIndex, value, timeout)
                slot.maxIntegralAccumulator = value
            }
            SLOT_PEAK_OUTPUT -> configDoubleParam(slot.closedLoopPeakOutput) { baseTalon, value ->
                baseTalon.configClosedLoopPeakOutput(activeSlotIndex, value, timeout)
                slot.closedLoopPeakOutput = value
            }
            OUTPUT_REVERSED -> configBooleanParam(talonService.outputInverted) { baseTalon, value ->
                baseTalon.inverted = value
            }
            OPEN_LOOP_RAMP -> configDoubleParam(config.openloopRamp) { baseTalon, value ->
                baseTalon.configOpenloopRamp(value, timeout)
                config.openloopRamp = value
            }
            CLOSED_LOOP_RAMP -> configDoubleParam(config.closedloopRamp) { baseTalon, value ->
                baseTalon.configClosedloopRamp(value, timeout)
                config.closedloopRamp = value
            }
            PEAK_OUTPUT_FORWARD -> configDoubleParam(config.peakOutputForward) { baseTalon, value ->
                baseTalon.configPeakOutputForward(value, timeout)
                config.peakOutputForward = value
            }
            PEAK_OUTPUT_REVERSE -> configDoubleParam(config.peakOutputReverse) { baseTalon, value ->
                baseTalon.configPeakOutputReverse(value, timeout)
                config.peakOutputReverse = value
            }
            NOMINAL_OUTPUT_FORWARD -> configDoubleParam(config.nominalOutputForward) { baseTalon, value ->
                baseTalon.configNominalOutputForward(value, timeout)
                config.nominalOutputForward = value
            }
            NOMINAL_OUTPUT_REVERSE -> configDoubleParam(config.nominalOutputReverse) { baseTalon, value ->
                baseTalon.configNominalOutputReverse(value, timeout)
                config.nominalOutputReverse = value
            }
            NEUTRAL_DEADBAND -> configDoubleParam(config.neutralDeadband) { baseTalon, value ->
                baseTalon.configNeutralDeadband(value, timeout)
                config.neutralDeadband = value
            }
            VOLTAGE_COMP_ENABLE -> configBooleanParam(talonService.voltageCompensation) { baseTalon, value ->
                baseTalon.enableVoltageCompensation(value)
                when (type) {
                    "srx" -> talonService.voltageCompensation = value
                    "fx" -> talonFxService.voltageCompensation = value
                    else -> throw IllegalArgumentException()
                }
            }
            VOLTAGE_COMP_SATURATION -> configDoubleParam(config.voltageCompSaturation) { baseTalon, value ->
                baseTalon.configVoltageCompSaturation(value, timeout)
                config.voltageCompSaturation = value
            }
            VOLTAGE_MEASUREMENT_FILTER -> configIntParam(config.voltageMeasurementFilter) { baseTalon, value ->
                baseTalon.configVoltageMeasurementFilter(value, timeout)
                config.voltageMeasurementFilter = value
            }
            MOTION_CRUISE_VELOCITY -> configIntParam(config.motionCruiseVelocity) { baseTalon, value ->
                baseTalon.configMotionCruiseVelocity(value, timeout)
                config.motionCruiseVelocity = value
            }
            MOTION_ACCELERATION -> configIntParam(config.motionAcceleration) { baseTalon, value ->
                baseTalon.configMotionAcceleration(value, timeout)
                config.motionAcceleration = value
            }
            SENSOR_PHASE -> configBooleanParam(talonService.sensorPhase) { baseTalon, value ->
                baseTalon.setSensorPhase(value)
                when (type) {
                    "srx" -> talonService.sensorPhase = value
                    "fx" -> talonFxService.sensorPhase = value
                    else -> throw IllegalArgumentException()
                }
            }
            CURRENT_LIMIT_ENABLE -> configBooleanSrxParam(talonService.currentLimit) { talon, value ->
                talon.enableCurrentLimit(value)
                talonService.currentLimit = value
            }
            CURRENT_LIMIT_CONT -> configIntSrxParam(srxConfig.continuousCurrentLimit) { talon, value ->
                talon.configContinuousCurrentLimit(value, timeout)
                srxConfig.continuousCurrentLimit = value
            }
            CURRENT_LIMIT_PEAK -> configIntSrxParam(srxConfig.peakCurrentLimit) { talon, value ->
                talon.configPeakCurrentLimit(value, timeout)
                srxConfig.peakCurrentLimit = value
            }
            CURRENT_LIMIT_PEAK_DURATION -> configIntSrxParam(srxConfig.peakCurrentDuration) { talon, value ->
                talon.configPeakCurrentDuration(value, timeout)
                srxConfig.peakCurrentDuration = value
            }
            STATOR_CURRENT_LIMIT_ENABLE -> configBooleanFxParam(fxConfig.statorCurrLimit.enable) { talonFx, value ->
                fxConfig.statorCurrLimit.enable = value
                talonFx.configStatorCurrentLimit(fxConfig.statorCurrLimit, timeout)
            }
            STATOR_CURRENT_LIMIT -> configDoubleFxParam(fxConfig.statorCurrLimit.currentLimit) { talonFx, value ->
                fxConfig.statorCurrLimit.currentLimit = value
                talonFx.configStatorCurrentLimit(fxConfig.statorCurrLimit, timeout)
            }
            STATOR_CURRENT_LIMIT_THRES_CURRENT -> configDoubleFxParam(fxConfig.statorCurrLimit.triggerThresholdCurrent) { talonFx, value ->
                fxConfig.statorCurrLimit.triggerThresholdCurrent = value
                talonFx.configStatorCurrentLimit(fxConfig.statorCurrLimit, timeout)
            }
            STATOR_CURRENT_LIMIT_THRES_TIME -> configDoubleFxParam(fxConfig.statorCurrLimit.triggerThresholdTime) { talonFx, value ->
                fxConfig.statorCurrLimit.triggerThresholdTime = value
                talonFx.configStatorCurrentLimit(fxConfig.statorCurrLimit, timeout)
            }
            SUPPLY_CURRENT_LIMIT_ENABLE -> {
                when (type) {
                    "srx" -> configBooleanSrxParam(talonService.supplyCurrentLimit.enable) { talon, value ->
                        talonService.supplyCurrentLimit.enable = value
                        talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                    }
                    "fx" -> configBooleanFxParam(fxConfig.supplyCurrLimit.enable) { talonFx, value ->
                        fxConfig.supplyCurrLimit.enable = value
                        talonFx.configSupplyCurrentLimit(fxConfig.supplyCurrLimit, timeout)
                    }
                    else -> throw IllegalArgumentException()
                }
            }
            SUPPLY_CURRENT_LIMIT -> {
                when (type) {
                    "srx" -> configDoubleSrxParam(talonService.supplyCurrentLimit.currentLimit) { talon, value ->
                        talonService.supplyCurrentLimit.currentLimit = value
                        talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                    }
                    "fx" -> configDoubleFxParam(fxConfig.supplyCurrLimit.currentLimit) { talonFx, value ->
                        fxConfig.supplyCurrLimit.currentLimit = value
                        talonFx.configSupplyCurrentLimit(fxConfig.supplyCurrLimit, timeout)
                    }
                    else -> throw IllegalArgumentException()
                }
            }
            SUPPLY_CURRENT_LIMIT_THRES_CURRENT -> {
                when(type){
                    "srx" -> configDoubleSrxParam(talonService.supplyCurrentLimit.triggerThresholdCurrent) { talon, value ->
                        talonService.supplyCurrentLimit.triggerThresholdCurrent = value
                        talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                    }
                    "fx" -> configDoubleFxParam(fxConfig.supplyCurrLimit.triggerThresholdCurrent) { talonFx, value ->
                        fxConfig.supplyCurrLimit.triggerThresholdCurrent = value
                        talonFx.configSupplyCurrentLimit(fxConfig.supplyCurrLimit, timeout)
                    }
                    else -> throw IllegalArgumentException()
                }
            }
            SUPPLY_CURRENT_LIMIT_THRES_TIME -> {
                when(type){
                    "srx" -> configDoubleSrxParam(talonService.supplyCurrentLimit.triggerThresholdTime) { talon, value ->
                        talonService.supplyCurrentLimit.triggerThresholdTime = value
                        talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                    }
                    "fx" -> configDoubleFxParam(fxConfig.supplyCurrLimit.triggerThresholdTime) { talonFx, value ->
                        fxConfig.supplyCurrLimit.triggerThresholdTime = value
                        talonFx.configSupplyCurrentLimit(fxConfig.supplyCurrLimit, timeout)
                    }
                    else -> throw IllegalArgumentException()
                }
            }
            STATUS_GENERAL -> configIntParam(defaultFor(Status_1_General)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_1_General, value, timeout)
            }
            STATUS_FEEDBACK0 -> configIntParam(defaultFor(Status_2_Feedback0)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_2_Feedback0, value, timeout)
            }
            STATUS_QUAD_ENCODER -> configIntParam(defaultFor(Status_3_Quadrature)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_3_Quadrature, value, timeout)
            }
            STATUS_AIN_TEMP_VBAT -> configIntParam(defaultFor(Status_4_AinTempVbat)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_4_AinTempVbat, value, timeout)
            }
            STATUS_PULSE_WIDTH -> configIntParam(defaultFor(Status_8_PulseWidth)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_8_PulseWidth, value, timeout)
            }
            STATUS_MOTION -> configIntParam(defaultFor(Status_10_MotionMagic)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_10_MotionMagic, value, timeout)
            }
            STATUS_PIDF0 -> configIntParam(defaultFor(Status_13_Base_PIDF0)) { baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_13_Base_PIDF0, value, timeout)
            }
            SOFT_LIMIT_ENABLE_FORWARD -> configBooleanParam(config.forwardSoftLimitEnable) { baseTalon, value ->
                baseTalon.configForwardSoftLimitEnable(value, timeout)
                config.forwardSoftLimitEnable = value
            }
            SOFT_LIMIT_ENABLE_REVERSE -> configBooleanParam(config.reverseSoftLimitEnable) { baseTalon, value ->
                baseTalon.configReverseSoftLimitEnable(value, timeout)
                config.reverseSoftLimitEnable = value
            }
            SOFT_LIMIT_THRESHOLD_FORWARD -> configIntParam(config.forwardSoftLimitThreshold) { baseTalon, value ->
                baseTalon.configForwardSoftLimitThreshold(value, timeout)
                config.forwardSoftLimitThreshold = value
            }
            SOFT_LIMIT_THRESHOLD_REVERSE -> configIntParam(config.reverseSoftLimitThreshold) { baseTalon, value ->
                baseTalon.configReverseSoftLimitThreshold(value, timeout)
                config.reverseSoftLimitThreshold = value
            }
            VELOCITY_MEASUREMENT_WINDOW -> configIntParam(config.velocityMeasurementWindow) { baseTalon, value ->
                baseTalon.configVelocityMeasurementWindow(value, timeout)
                // Let the Talon round down then number to a legal value
                when (type) {
                    "srx" -> talonService.dirty = true
                    "fx" -> talonFxService.dirty = true
                    else -> throw IllegalArgumentException()
                }
            }
            INTEGRATED_SENSOR_OFFSET_DEGREES -> configDoubleFxParam(fxConfig.integratedSensorOffsetDegrees) { talonFx, value ->
                talonFx.configIntegratedSensorOffset(value, timeout)
                fxConfig.integratedSensorOffsetDegrees = value
            }
            FACTORY_DEFAULTS -> configBooleanParam(false) { baseTalon, value ->
                if (value) baseTalon.configFactoryDefault(timeout)
            }
            else -> TODO("${param.enum} not implemented")
        }
        return super.execute()
    }

    private fun configBooleanParam(default: Boolean, config: (BaseTalon, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        when (type) {
            "srx" -> {
                talonService.active.forEach { config(it, paramValue) }
                logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
            }
            "fx" -> {
                talonFxService.active.forEach { config(it, paramValue) }
                logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun configBooleanSrxParam(default: Boolean, config: (TalonSRX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configBooleanFxParam(default: Boolean, config: (TalonFX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        talonFxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
    }

    private fun configIntParam(default: Int, config: (BaseTalon, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        when (type) {
            "srx" -> {
                talonService.active.forEach { config(it, paramValue) }
                logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
            }
            "fx" -> {
                talonFxService.active.forEach { config(it, paramValue) }
                logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun configIntSrxParam(default: Int, config: (TalonSRX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleParam(default: Double, config: (BaseTalon, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        when (type) {
            "srx" -> {
                talonService.active.forEach { config(it, paramValue) }
                logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
            }
            "fx" -> {
                talonFxService.active.forEach { config(it, paramValue) }
                logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun configDoubleSrxParam(default: Double, config: (TalonSRX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleFxParam(default: Double, config: (TalonFX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonFxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
    }

    private fun defaultFor(frame: StatusFrameEnhanced): Int {
        when (type) {
            "srx" -> return talonService.active.first().getStatusFramePeriod(frame)
            "fx" -> return talonFxService.active.first().getStatusFramePeriod(frame)
            else -> throw IllegalArgumentException()
        }
    }

}

