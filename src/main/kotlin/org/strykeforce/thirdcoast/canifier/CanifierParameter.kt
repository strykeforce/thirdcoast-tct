package org.strykeforce.thirdcoast.canifier

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class CanifierParameter(command: Command, toml: TomlTable, val enum: Enum) : AbstractParameter(command, toml) {

    enum class Enum {
        QUAD_POSITION,
        STATUS_GENERAL1,
        STATUS_GENERAL2,
        STATUS_PWM_INPUTS0,
        STATUS_PWM_INPUTS1,
        STATUS_PWM_INPUTS2,
        STATUS_PWM_INPUTS3,
        FACTORY_DEFAULTS,
    }

    companion object {
        private val tomlTable by lazy { parseResource("/canifier.toml") }

        fun create(command: Command, param: String): CanifierParameter {
            val toml = tomlTable.getTable(param) ?: throw IllegalArgumentException("missing param: $param")
            return CanifierParameter(command, toml, Enum.valueOf(param))
        }
    }

}