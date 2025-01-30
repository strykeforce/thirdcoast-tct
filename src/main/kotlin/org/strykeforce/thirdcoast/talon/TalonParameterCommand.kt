package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.*
import com.ctre.phoenix.motorcontrol.can.*
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.talon.TalonParameter.Enum.*

private val logger = KotlinLogging.logger {}


class TalonParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonService.timeout
    private val param = TalonParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            val baseConfig: BaseTalonConfiguration
            val slot: SlotConfiguration
            baseConfig = talonService.activeConfiguration
            slot = talonService.activeSlot
            val srxConfig = talonService.activeConfiguration
            return when (param.enum) {
                SLOT_P -> formatMenu(slot.kP, DOUBLE_FORMAT_4)
                SLOT_I -> formatMenu(slot.kI, DOUBLE_FORMAT_4)
                SLOT_D -> formatMenu(slot.kD, DOUBLE_FORMAT_4)
                SLOT_F -> formatMenu(slot.kF, DOUBLE_FORMAT_4)
                SLOT_I_ZONE -> formatMenu(slot.integralZone)
                SLOT_ALLOWABLE_ERR -> formatMenu(slot.allowableClosedloopError)
                SLOT_MAX_I_ACCUM -> formatMenu(slot.maxIntegralAccumulator)
                SLOT_PEAK_OUTPUT -> formatMenu(slot.closedLoopPeakOutput)
                OUTPUT_REVERSED -> formatMenu(talonService.outputInverted)
                OPEN_LOOP_RAMP -> formatMenu(baseConfig.openloopRamp)
                CLOSED_LOOP_RAMP -> formatMenu(baseConfig.closedloopRamp)
                PEAK_OUTPUT_FORWARD -> formatMenu(baseConfig.peakOutputForward)
                PEAK_OUTPUT_REVERSE -> formatMenu(baseConfig.peakOutputReverse)
                NOMINAL_OUTPUT_FORWARD -> formatMenu(baseConfig.nominalOutputForward)
                NOMINAL_OUTPUT_REVERSE -> formatMenu(baseConfig.nominalOutputReverse)
                NEUTRAL_DEADBAND -> formatMenu(baseConfig.neutralDeadband)
                VOLTAGE_COMP_ENABLE -> formatMenu(talonService.voltageCompensation)
                VOLTAGE_COMP_SATURATION -> formatMenu(baseConfig.voltageCompSaturation)
                VOLTAGE_MEASUREMENT_FILTER -> formatMenu(baseConfig.voltageMeasurementFilter)
                MOTION_CRUISE_VELOCITY -> formatMenu(baseConfig.motionCruiseVelocity)
                MOTION_ACCELERATION -> formatMenu(baseConfig.motionAcceleration)
                SENSOR_PHASE -> formatMenu(talonService.sensorPhase)
                CURRENT_LIMIT_ENABLE -> formatMenu(talonService.currentLimit)
                CURRENT_LIMIT_CONT -> formatMenu(srxConfig.continuousCurrentLimit)
                CURRENT_LIMIT_PEAK -> formatMenu(srxConfig.peakCurrentLimit)
                CURRENT_LIMIT_PEAK_DURATION -> formatMenu(srxConfig.peakCurrentDuration)
                SUPPLY_CURRENT_LIMIT_ENABLE -> formatMenu(talonService.supplyCurrentLimit.enable)
                SUPPLY_CURRENT_LIMIT -> formatMenu(talonService.supplyCurrentLimit.currentLimit)
                SUPPLY_CURRENT_LIMIT_THRES_CURRENT -> formatMenu(talonService.supplyCurrentLimit.triggerThresholdCurrent)
                SUPPLY_CURRENT_LIMIT_THRES_TIME -> formatMenu(talonService.supplyCurrentLimit.triggerThresholdTime)
                STATUS_GENERAL -> formatMenu(defaultFor(Status_1_General))
                STATUS_FEEDBACK0 -> formatMenu(defaultFor(Status_2_Feedback0))
                STATUS_QUAD_ENCODER -> formatMenu(defaultFor(Status_3_Quadrature))
                STATUS_AIN_TEMP_VBAT -> formatMenu(defaultFor(Status_4_AinTempVbat))
                STATUS_PULSE_WIDTH -> formatMenu(defaultFor(Status_8_PulseWidth))
                STATUS_MOTION -> formatMenu(defaultFor(Status_10_MotionMagic))
                STATUS_PIDF0 -> formatMenu(defaultFor(Status_13_Base_PIDF0))
                STATUS_MISC -> formatMenu(defaultFor(Status_6_Misc))
                STATUS_COMM -> formatMenu(defaultFor(Status_7_CommStatus))
                STATUS_MOTION_BUFF -> formatMenu(defaultFor(Status_9_MotProfBuffer))
                STATUS_FEEDBACK1 -> formatMenu(defaultFor(Status_12_Feedback1))
                STATUS_PIDF1 -> formatMenu(defaultFor(Status_14_Turn_PIDF1))
                STATUS_FIRMWARE_API -> formatMenu(defaultFor(Status_15_FirmwareApiStatus))
                STATUS_UART_GADGETEER -> formatMenu(defaultFor(Status_11_UartGadgeteer))
                SOFT_LIMIT_ENABLE_FORWARD -> formatMenu(baseConfig.forwardSoftLimitEnable)
                SOFT_LIMIT_ENABLE_REVERSE -> formatMenu(baseConfig.reverseSoftLimitEnable)
                SOFT_LIMIT_THRESHOLD_FORWARD -> formatMenu(baseConfig.forwardSoftLimitThreshold)
                SOFT_LIMIT_THRESHOLD_REVERSE -> formatMenu(baseConfig.reverseSoftLimitThreshold)
                VELOCITY_MEASUREMENT_WINDOW -> formatMenu(baseConfig.velocityMeasurementWindow)
                FACTORY_DEFAULTS -> tomlMenu
                else -> TODO("${param.enum} not implemented")
            }
        }

    override fun execute(): Command {
        val config: BaseTalonConfiguration
        val slot: SlotConfiguration
        val activeSlotIndex: Int
        val timeout: Int
        config = talonService.activeConfiguration
        slot = talonService.activeSlot
        activeSlotIndex = talonService.activeSlotIndex
        timeout = talonService.timeout
        val srxConfig = talonService.activeConfiguration

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
            SLOT_I_ZONE -> configIntParam(slot.integralZone.toInt()) { baseTalon, value ->
                baseTalon.config_IntegralZone(activeSlotIndex, value.toDouble(), timeout)
                slot.integralZone = value.toDouble()
            }
            SLOT_ALLOWABLE_ERR -> configIntParam(slot.allowableClosedloopError.toInt()) { baseTalon, value ->
                baseTalon.configAllowableClosedloopError(activeSlotIndex, value.toDouble(), timeout)
                slot.allowableClosedloopError = value.toDouble()
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
                talonService.voltageCompensation = value
            }
            VOLTAGE_COMP_SATURATION -> configDoubleParam(config.voltageCompSaturation) { baseTalon, value ->
                baseTalon.configVoltageCompSaturation(value, timeout)
                config.voltageCompSaturation = value
            }
            VOLTAGE_MEASUREMENT_FILTER -> configIntParam(config.voltageMeasurementFilter) { baseTalon, value ->
                baseTalon.configVoltageMeasurementFilter(value, timeout)
                config.voltageMeasurementFilter = value
            }
            MOTION_CRUISE_VELOCITY -> configIntParam(config.motionCruiseVelocity.toInt()) { baseTalon, value ->
                baseTalon.configMotionCruiseVelocity(value.toDouble(), timeout)
                config.motionCruiseVelocity = value.toDouble()
            }
            MOTION_ACCELERATION -> configIntParam(config.motionAcceleration.toInt()) { baseTalon, value ->
                baseTalon.configMotionAcceleration(value.toDouble(), timeout)
                config.motionAcceleration = value.toDouble()
            }
            SENSOR_PHASE -> configBooleanParam(talonService.sensorPhase) { baseTalon, value ->
                baseTalon.setSensorPhase(value)
                talonService.sensorPhase = value
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
            SUPPLY_CURRENT_LIMIT_ENABLE -> {
                configBooleanSrxParam(talonService.supplyCurrentLimit.enable) { talon, value ->
                    talonService.supplyCurrentLimit.enable = value
                    talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                }
            }
            SUPPLY_CURRENT_LIMIT -> {
                configDoubleSrxParam(talonService.supplyCurrentLimit.currentLimit) { talon, value ->
                    talonService.supplyCurrentLimit.currentLimit = value
                    talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                }
            }
            SUPPLY_CURRENT_LIMIT_THRES_CURRENT -> {
                configDoubleSrxParam(talonService.supplyCurrentLimit.triggerThresholdCurrent) { talon, value ->
                    talonService.supplyCurrentLimit.triggerThresholdCurrent = value
                    talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
                }
            }
            SUPPLY_CURRENT_LIMIT_THRES_TIME -> {
                configDoubleSrxParam(talonService.supplyCurrentLimit.triggerThresholdTime) { talon, value ->
                    talonService.supplyCurrentLimit.triggerThresholdTime = value
                    talon.configSupplyCurrentLimit(talonService.supplyCurrentLimit, timeout)
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
            STATUS_MISC -> configIntParam(defaultFor(Status_6_Misc)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_6_Misc, value, timeout)
            }
            STATUS_COMM -> configIntParam(defaultFor(Status_7_CommStatus)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_7_CommStatus, value, timeout)
            }
            STATUS_MOTION_BUFF -> configIntParam(defaultFor(Status_9_MotProfBuffer)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_9_MotProfBuffer, value, timeout)
            }
            STATUS_FEEDBACK1 -> configIntParam(defaultFor(Status_12_Feedback1)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_12_Feedback1, value, timeout)
            }
            STATUS_PIDF1 -> configIntParam(defaultFor(Status_14_Turn_PIDF1)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_14_Turn_PIDF1, value, timeout)
            }
            STATUS_FIRMWARE_API -> configIntParam(defaultFor(Status_15_FirmwareApiStatus)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_15_FirmwareApiStatus, value, timeout)
            }
            STATUS_UART_GADGETEER -> configIntParam(defaultFor(Status_11_UartGadgeteer)){ baseTalon, value ->
                baseTalon.setStatusFramePeriod(Status_11_UartGadgeteer, value, timeout)
            }
            SOFT_LIMIT_ENABLE_FORWARD -> configBooleanParam(config.forwardSoftLimitEnable) { baseTalon, value ->
                baseTalon.configForwardSoftLimitEnable(value, timeout)
                config.forwardSoftLimitEnable = value
            }
            SOFT_LIMIT_ENABLE_REVERSE -> configBooleanParam(config.reverseSoftLimitEnable) { baseTalon, value ->
                baseTalon.configReverseSoftLimitEnable(value, timeout)
                config.reverseSoftLimitEnable = value
            }
            SOFT_LIMIT_THRESHOLD_FORWARD -> configIntParam(config.forwardSoftLimitThreshold.toInt()) { baseTalon, value ->
                baseTalon.configForwardSoftLimitThreshold(value.toDouble(), timeout)
                config.forwardSoftLimitThreshold = value.toDouble()
            }
            SOFT_LIMIT_THRESHOLD_REVERSE -> configIntParam(config.reverseSoftLimitThreshold.toInt()) { baseTalon, value ->
                baseTalon.configReverseSoftLimitThreshold(value.toDouble(), timeout)
                config.reverseSoftLimitThreshold = value.toDouble()
            }
            VELOCITY_MEASUREMENT_WINDOW -> configIntParam(config.velocityMeasurementWindow) { baseTalon, value ->
                baseTalon.configVelocityMeasurementWindow(value, timeout)
                // Let the Talon round down then number to a legal value
                talonService.dirty = true
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
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configBooleanSrxParam(default: Boolean, config: (TalonSRX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }


    private fun configIntParam(default: Int, config: (BaseTalon, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configIntSrxParam(default: Int, config: (TalonSRX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleParam(default: Double, config: (BaseTalon, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleSrxParam(default: Double, config: (TalonSRX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun defaultFor(frame: StatusFrameEnhanced): Int {
        return talonService.active.first().getStatusFramePeriod(frame)
    }

}

