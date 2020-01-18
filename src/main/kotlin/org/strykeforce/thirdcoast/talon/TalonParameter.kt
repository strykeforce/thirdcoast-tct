package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class TalonParameter(command: Command, toml: TomlTable, val enum: Enum) : AbstractParameter(command, toml) {

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
        STATOR_CURRENT_LIMIT_ENABLE,
        STATOR_CURRENT_LIMIT,
        STATOR_CURRENT_LIMIT_THRES_CURRENT,
        STATOR_CURRENT_LIMIT_THRES_TIME,
        SUPPLY_CURRENT_LIMIT_ENABLE,
        SUPPLY_CURRENT_LIMIT,
        SUPPLY_CURRENT_LIMIT_THRES_CURRENT,
        SUPPLY_CURRENT_LIMIT_THRES_TIME,
        STATUS_GENERAL,
        STATUS_FEEDBACK0,
        STATUS_QUAD_ENCODER,
        STATUS_AIN_TEMP_VBAT,
        STATUS_PULSE_WIDTH,
        STATUS_MOTION,
        STATUS_PIDF0,
        STATUS_MISC,
        STATUS_COMM,
        STATUS_MOTION_BUFF,
        STATUS_FEEDBACK1,
        STATUS_PIDF1,
        STATUS_FIRMWARE_API,
        STATUS_UART_GADGETEER,
        SOFT_LIMIT_ENABLE_FORWARD,
        SOFT_LIMIT_ENABLE_REVERSE,
        SOFT_LIMIT_THRESHOLD_FORWARD,
        SOFT_LIMIT_THRESHOLD_REVERSE,
        VELOCITY_MEASUREMENT_WINDOW,
        INTEGRATED_SENSOR_OFFSET_DEGREES,
        FACTORY_DEFAULTS,
    }

    companion object {
        private val tomlTable by lazy { parseResource("/talon.toml") }

        fun create(command: Command, param: String): TalonParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return TalonParameter(command, toml, Enum.valueOf(param))
        }
    }

}