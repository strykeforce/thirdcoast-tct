package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class TalonFXParameter(command: Command, toml: TomlTable, val enum: Enum) : AbstractParameter(command, toml) {

    enum class Enum{
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
        STATOR_CURRENT_LIMIT_ENABLE,
        STATOR_CURRENT_LIMIT,
        STATOR_CURRENT_LIMIT_THRES_CURRENT,
        STATOR_CURRENT_LIMIT_THRES_TIME,
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
        VELOCITY_MEASUREMENT_WINDOW,
        FACTORY_DEFAULTS,
    }

    companion object {
        private val tomlTable by lazy { parseResource("/talonFx.toml") }

        fun create(command: Command, param: String): TalonFXParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return TalonFXParameter(command, toml, Enum.valueOf(param))
        }
    }
}