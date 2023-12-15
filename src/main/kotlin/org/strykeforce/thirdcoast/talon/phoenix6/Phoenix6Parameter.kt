package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class Phoenix6Parameter(command: Command, toml: TomlTable, val enum: P6Enum): AbstractParameter(command, toml) {

    enum class P6Enum {
        ROTOR_OFFSET,
        SENSOR_TO_MECH_RATIO,
        ROTOR_TO_SENSOR_RATIO,
        REMOTE_SENSOR_ID,

        SLOT_KP,
        SLOT_KI,
        SLOT_KD,
        SLOT_KS,
        SLOT_KV,
        SLOT_KA,
        SLOT_KG,

        MM_ACCEL,
        MM_CRUISE_VEL,
        MM_JERK,

        PEAK_DIFF_DC,
        PEAK_DIFF_TORQUE,
        PEAK_DIFF_VOLT,
        DIFF_SENSOR_REMOTE_ID,
        DIFF_FX_ID,

        STATOR_LIM_EN,
        STATOR_LIM,
        SUPP_LIM_EN,
        SUPP_LIM,
        SUPP_TRIP_THRES,
        SUPP_TRIP_TIME,

        CLOSED_LOOP_RAMP_DC,
        PEAK_FWD_DC,
        PEAK_REV_DC,
        NEUTRAL_DEADBAND_DC,
        OPEN_LOOP_RAMP_DC,
        PEAK_FWD_V,
        PEAK_REV_V,
        SUPPLY_V_TIME_CONST,
        OPEN_LOOP_RAMP_V,
        CLOSED_LOOP_RAMP_V,
        PEAK_FWD_I,
        PEAK_REV_I,
        TORQUE_NEUTRAL_DEADBAND,
        OPEN_LOOP_RAMP_I,
        CLOSED_LOOP_RAMP_I,
        CONTINUOUS_WRAP,

        FWD_SOFT_EN,
        FWD_SOFT_THRES,
        REV_SOFT_EN,
        REV_SOFT_THRES,

        FWD_HARD_EN,
        FWD_REMOTE_ID,
        FWD_AUTOSET_POS,
        FWD_AUTOSET_POS_VALUE,
        REV_HARD_EN,
        REV_REMOTE_ID,
        REV_AUTOSET_POS,
        REV_AUTOSET_POS_VALUE,

        ALLOW_MUSIC_DIS,
        BEEP_ON_BOOT,
        BEEP_ON_CONFIG,

        VELOCITY,
        ACCELERATION,
        JERK,
        TORQUE_CURRENT_DEADBAND,
        TORQUE_CURRENT_MAX,
        FEED_FORWARD,
        OPPOSE_MAIN,
        DIFFERENTIAL_SLOT,
        DIFFERENTIAL_TARGET,
        FOC,
        OVERRIDE_NEUTRAL

    }

    companion object {
        private val tomlTable by lazy { parseResource("/phoenix6.toml") }

        fun create(command: Command, param: String): Phoenix6Parameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return Phoenix6Parameter(command, toml, P6Enum.valueOf(param))
        }
    }
}