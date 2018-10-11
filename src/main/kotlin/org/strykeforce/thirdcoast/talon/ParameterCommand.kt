package org.strykeforce.thirdcoast.talon

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
                else -> throw IllegalStateException(param.enum.name)
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

            else -> throw IllegalStateException(param.enum.name)
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
}

