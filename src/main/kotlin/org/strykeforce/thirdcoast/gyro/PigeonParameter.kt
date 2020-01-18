package org.strykeforce.thirdcoast.gyro

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource
import org.strykeforce.thirdcoast.talon.TalonParameter

class PigeonParameter(command: Command, toml: TomlTable, val enum: PigeonParameter.Enum) : AbstractParameter(command, toml) {

    enum class Enum {
        ACCUM_Z_ANGLE,
        FUSED_HEADING,
        TEMP_COMP_DISABLE,
        YAW,
        GENERAL_STATUS,
        SIX_DEG_STATUS,
        FUSED_STATUS,
        GYRO_ACCUM_STATUS,
        GEN_COMPASS_STATUS,
        GEN_ACCEL_STATUS,
        SIX_QUAT_STATUS,
        MAG_STATUS,
        BIAS_GYRO_STATUS,
        BIAS_MAG_STATUS,
        BIAS_ACCEL_STATUS,
        FACTORY_DEFAULT,
    }

    companion object {
        private val tomlTable by lazy { parseResource("/pigeon.toml") }

        fun create(command: Command, param: String): PigeonParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return PigeonParameter(command, toml, Enum.valueOf(param))
        }
    }
}