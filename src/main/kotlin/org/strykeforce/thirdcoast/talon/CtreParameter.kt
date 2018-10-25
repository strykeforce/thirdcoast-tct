package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class CtreParameter(command: Command, toml: TomlTable, val enum: Enum) : AbstractParameter(command, toml) {

    override val hasSlot = setOf(
        Enum.SLOT_P,
        Enum.SLOT_I,
        Enum.SLOT_D,
        Enum.SLOT_F,
        Enum.SLOT_I_ZONE,
        Enum.SLOT_ALLOWABLE_ERR,
        Enum.SLOT_MAX_I_ACCUM,
        Enum.SLOT_PEAK_OUTPUT
    ).contains(enum)

    enum class Enum {
        SLOT_P,
        SLOT_I,
        SLOT_D,
        SLOT_F,
        SLOT_I_ZONE,
        SLOT_ALLOWABLE_ERR,
        SLOT_MAX_I_ACCUM,
        SLOT_PEAK_OUTPUT,
        OUTPUT_REVERSED,
        OPEN_LOOP_RAMP,
        CLOSED_LOOP_RAMP,
        PEAK_OUTPUT_FORWARD,
        PEAK_OUTPUT_REVERSE,
        NOMINAL_OUTPUT_FORWARD,
        NOMINAL_OUTPUT_REVERSE,
        NEUTRAL_DEADBAND,
        VOLTAGE_COMP_ENABLE,
        VOLTAGE_COMP_SATURATION,
        VOLTAGE_MEASUREMENT_FILTER,
        MOTION_CRUISE_VELOCITY,
        MOTION_ACCELERATION,
        FEEDBACK_COEFFICIENT,
        SENSOR_PHASE,
        SENSOR_POSITION,
        CURRENT_LIMIT_ENABLE,
        CURRENT_LIMIT_CONT,
        CURRENT_LIMIT_PEAK,
        CURRENT_LIMIT_PEAK_DURATION,
        STATUS_GENERAL,
        STATUS_FEEDBACK0,
        STATUS_QUAD_ENCODER,
        STATUS_AIN_TEMP_VBAT,
        STATUS_PULSE_WIDTH,
        STATUS_MOTION,
        STATUS_PIDF0,
        SOFT_LIMIT_ENABLE_FORWARD,
        SOFT_LIMIT_ENABLE_REVERSE,
        SOFT_LIMIT_THRESHOLD_FORWARD,
        SOFT_LIMIT_THRESHOLD_REVERSE,
        FACTORY_DEFAULTS,
    }

    companion object {
        private val tomlTable by lazy { parseResource("/ctre.toml") }

        fun create(command: Command, param: String): CtreParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return CtreParameter(command, toml, Enum.valueOf(param))
        }
    }

}