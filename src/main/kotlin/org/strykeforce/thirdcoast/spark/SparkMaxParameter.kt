package org.strykeforce.thirdcoast.spark

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource


class SparkMaxParameter(command: Command, toml: TomlTable, val enum: Enum) : AbstractParameter(command, toml) {
    enum class Enum{
        SLOT_P,
        SLOT_I,
        SLOT_D,
        SLOT_D_FILTER,
        SLOT_FF,
        SLOT_I_MAX_ACCUM,
        SLOT_I_ZONE,
        SLOT_OUTPUT_MIN,
        SLOT_OUTPUT_MAX,
        SLOT_MOTION_MAX_ACCEL,
        SLOT_MOTION_MAX_VEL,
        SLOT_MOTION_MIN_VEL,
        SLOT_MOTION_ALLOWED_ERROR,
        VOLTAGE_COMP_ENABLE,
        VOLTAGE_COMP_VOLTAGE,
        OPEN_LOOP_RAMP,
        CLOSED_LOOP_RAMP,
        INVERTED,
        SECONDARY_CURRENT_LIMIT,
        SECONDARY_CURRENT_LIMIT_CHOP_CYCLES,
        SMART_CURRENT_STALL_LIMIT,
        SMART_CURRENT_FREE_LIMIT,
        SMART_CURRENT_RPM_LIMIT,
        SENSOR_POSITION,
        POSITION_CONV_FACTOR,
        VELOCITY_CONV_FACTOR,
        MEASUREMENT_PERIOD,
        SENSOR_INVERTED,
        SENSOR_MIN,
        SENSOR_MAX,
        SENSOR_AVG_DEPTH,
        SENSOR_CPR,
        FWD_HARD_LIMIT_EN,
        REV_HARD_LIMIT_EN,
        FWD_SOFT_LIMIT_EN,
        FWD_SOFT_LIMIT,
        REV_SOFT_LIMIT_EN,
        REV_SOFT_LIMIT
    }

    companion object {
        private val tomlTable by lazy { parseResource("/sparkMax.toml") }

        fun create(command: Command, param: String): SparkMaxParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return SparkMaxParameter(command, toml, SparkMaxParameter.Enum.valueOf(param))
        }
    }
}