package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.talon.CtreParameter.Enum.*

private val logger = KotlinLogging.logger {}

class ParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    companion object {
        var reset = true
    }

    private val timeout = talonService.timeout
    private val param = CtreParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    private var config = talonService.activeConfiguration

    override val menu: String
        get() {
            if (reset) {
                config = talonService.activeConfiguration
                logger.info { "reset active config to:\n${config.toString("active")}" }
                reset = false
            }
            return when (param.enum) {
                OUTPUT_REVERSED -> formatMenu(talonService.outputReverse)
                OPEN_LOOP_RAMP -> formatMenu(config.openloopRamp)
                CLOSED_LOOP_RAMP -> formatMenu(config.closedloopRamp)
                PEAK_OUTPUT_FORWARD -> formatMenu(config.peakOutputForward)
                PEAK_OUTPUT_REVERSE -> formatMenu(config.peakOutputReverse)
                NOMINAL_OUTPUT_FORWARD -> formatMenu(config.nominalOutputForward)
                NOMINAL_OUTPUT_REVERSE -> formatMenu(config.nominalOutputReverse)
                NEUTRAL_DEADBAND -> formatMenu(config.neutralDeadband)
                VOLTAGE_COMP_ENABLE -> formatMenu(talonService.voltageCompensation)
                VOLTAGE_COMP_SATURATION -> formatMenu(config.voltageCompSaturation)
                VOLTAGE_MEASUREMENT_FILTER -> formatMenu(config.voltageMeasurementFilter)
                MOTION_CRUISE_VELOCITY -> formatMenu(config.motionCruiseVelocity)
                MOTION_ACCELERATION -> formatMenu(config.motionAcceleration)
                SENSOR_PHASE -> formatMenu(talonService.sensorPhase)
                CURRENT_LIMIT_ENABLE -> formatMenu(talonService.currentLimit)
                CURRENT_LIMIT_CONT -> formatMenu(config.continuousCurrentLimit)
                CURRENT_LIMIT_PEAK -> formatMenu(config.peakCurrentLimit)
                CURRENT_LIMIT_PEAK_DURATION -> formatMenu(config.peakCurrentDuration)
                STATUS_GENERAL -> formatMenu(defaultFor(Status_1_General))
                STATUS_FEEDBACK0 -> formatMenu(defaultFor(Status_2_Feedback0))
                STATUS_QUAD_ENCODER -> formatMenu(defaultFor(Status_3_Quadrature))
                STATUS_AIN_TEMP_VBAT -> formatMenu(defaultFor(Status_4_AinTempVbat))
                STATUS_PULSE_WIDTH -> formatMenu(defaultFor(Status_8_PulseWidth))
                STATUS_MOTION -> formatMenu(defaultFor(Status_10_MotionMagic))
                STATUS_PIDF0 -> formatMenu(defaultFor(Status_13_Base_PIDF0))
                SOFT_LIMIT_ENABLE_FORWARD -> formatMenu(config.forwardSoftLimitEnable)
                SOFT_LIMIT_ENABLE_REVERSE -> formatMenu(config.reverseSoftLimitEnable)
                SOFT_LIMIT_THRESHOLD_FORWARD -> formatMenu(config.forwardSoftLimitThreshold)
                SOFT_LIMIT_THRESHOLD_REVERSE -> formatMenu(config.reverseSoftLimitThreshold)
                else -> TODO("${param.enum} not implemented")
            }
        }

    override fun execute(): Command {
        config = talonService.activeConfiguration

        when (param.enum) {
            OUTPUT_REVERSED -> configBooleanParam(talonService.outputReverse) { talon, value ->
                talon.inverted = value
            }
            OPEN_LOOP_RAMP -> configDoubleParam(config.openloopRamp) { talon, value ->
                talon.configOpenloopRamp(value, timeout)
                config.openloopRamp = value
            }
            CLOSED_LOOP_RAMP -> configDoubleParam(config.closedloopRamp) { talon, value ->
                talon.configClosedloopRamp(value, timeout)
                config.closedloopRamp = value
            }
            PEAK_OUTPUT_FORWARD -> configDoubleParam(config.peakOutputForward) { talon, value ->
                talon.configPeakOutputForward(value, timeout)
                config.peakOutputForward = value
            }
            PEAK_OUTPUT_REVERSE -> configDoubleParam(config.peakOutputReverse) { talon, value ->
                talon.configPeakOutputReverse(value, timeout)
                config.peakOutputReverse = value
            }
            NOMINAL_OUTPUT_FORWARD -> configDoubleParam(config.nominalOutputForward) { talon, value ->
                talon.configNominalOutputForward(value, timeout)
                config.nominalOutputForward = value
            }
            NOMINAL_OUTPUT_REVERSE -> configDoubleParam(config.nominalOutputReverse) { talon, value ->
                talon.configNominalOutputReverse(value, timeout)
                config.nominalOutputReverse = value
            }
            NEUTRAL_DEADBAND -> configDoubleParam(config.neutralDeadband) { talon, value ->
                talon.configNeutralDeadband(value, timeout)
                config.neutralDeadband = value
            }
            VOLTAGE_COMP_ENABLE -> configBooleanParam(talonService.voltageCompensation) { talon, value ->
                talon.enableVoltageCompensation(value)
                talonService.voltageCompensation = value
            }
            VOLTAGE_COMP_SATURATION -> configDoubleParam(config.voltageCompSaturation) { talon, value ->
                talon.configVoltageCompSaturation(value, timeout)
                config.voltageCompSaturation = value
            }
            VOLTAGE_MEASUREMENT_FILTER -> configIntParam(config.voltageMeasurementFilter) { talon, value ->
                talon.configVoltageMeasurementFilter(value, timeout)
                config.voltageMeasurementFilter = value
            }
            MOTION_CRUISE_VELOCITY -> configIntParam(config.motionCruiseVelocity) { talon, value ->
                talon.configMotionCruiseVelocity(value, timeout)
                config.motionCruiseVelocity = value
            }
            MOTION_ACCELERATION -> configIntParam(config.motionAcceleration) { talon, value ->
                talon.configMotionAcceleration(value, timeout)
                config.motionAcceleration = value
            }
            SENSOR_PHASE -> configBooleanParam(talonService.sensorPhase) { talon, value ->
                talon.setSensorPhase(value)
                talonService.sensorPhase = value
            }
            CURRENT_LIMIT_ENABLE -> configBooleanParam(talonService.currentLimit) { talon, value ->
                talon.enableCurrentLimit(value)
                talonService.currentLimit = value
            }
            CURRENT_LIMIT_CONT -> configIntParam(config.continuousCurrentLimit) { talon, value ->
                talon.configContinuousCurrentLimit(value, timeout)
                config.continuousCurrentLimit = value
            }
            CURRENT_LIMIT_PEAK -> configIntParam(config.peakCurrentLimit) { talon, value ->
                talon.configPeakCurrentLimit(value, timeout)
                config.peakCurrentLimit = value
            }
            CURRENT_LIMIT_PEAK_DURATION -> configIntParam(config.peakCurrentDuration) { talon, value ->
                talon.configPeakCurrentDuration(value, timeout)
                config.peakCurrentDuration = value
            }
            STATUS_GENERAL -> configIntParam(defaultFor(Status_1_General)) { talon, value ->
                talon.setStatusFramePeriod(Status_1_General, value, timeout)
            }
            STATUS_FEEDBACK0 -> configIntParam(defaultFor(Status_2_Feedback0)) { talon, value ->
                talon.setStatusFramePeriod(Status_2_Feedback0, value, timeout)
            }
            STATUS_QUAD_ENCODER -> configIntParam(defaultFor(Status_3_Quadrature)) { talon, value ->
                talon.setStatusFramePeriod(Status_3_Quadrature, value, timeout)
            }
            STATUS_AIN_TEMP_VBAT -> configIntParam(defaultFor(Status_4_AinTempVbat)) { talon, value ->
                talon.setStatusFramePeriod(Status_4_AinTempVbat, value, timeout)
            }
            STATUS_PULSE_WIDTH -> configIntParam(defaultFor(Status_8_PulseWidth)) { talon, value ->
                talon.setStatusFramePeriod(Status_8_PulseWidth, value, timeout)
            }
            STATUS_MOTION -> configIntParam(defaultFor(Status_10_MotionMagic)) { talon, value ->
                talon.setStatusFramePeriod(Status_10_MotionMagic, value, timeout)
            }
            STATUS_PIDF0 -> configIntParam(defaultFor(Status_13_Base_PIDF0)) { talon, value ->
                talon.setStatusFramePeriod(Status_13_Base_PIDF0, value, timeout)
            }
            SOFT_LIMIT_ENABLE_FORWARD -> configBooleanParam(config.forwardSoftLimitEnable) { talon, value ->
                talon.configForwardSoftLimitEnable(value, timeout)
                config.forwardSoftLimitEnable = value
            }
            SOFT_LIMIT_ENABLE_REVERSE -> configBooleanParam(config.reverseSoftLimitEnable) { talon, value ->
                talon.configReverseSoftLimitEnable(value, timeout)
                config.reverseSoftLimitEnable = value
            }
            SOFT_LIMIT_THRESHOLD_FORWARD -> configIntParam(config.forwardSoftLimitThreshold) { talon, value ->
                talon.configForwardSoftLimitThreshold(value, timeout)
                config.forwardSoftLimitThreshold = value
            }
            SOFT_LIMIT_THRESHOLD_REVERSE -> configIntParam(config.reverseSoftLimitThreshold) { talon, value ->
                talon.configReverseSoftLimitThreshold(value, timeout)
                config.reverseSoftLimitThreshold = value
            }
            else -> TODO("${param.enum} not implemented")
        }
        return super.execute()
    }

    private fun configBooleanParam(default: Boolean, config: (TalonSRX, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }


    private fun configIntParam(default: Int, config: (TalonSRX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleParam(default: Double, config: (TalonSRX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun defaultFor(frame: StatusFrameEnhanced): Int =
        talonService.active.first().getStatusFramePeriod(frame)
}

