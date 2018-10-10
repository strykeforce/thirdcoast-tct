package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class CtreParameter(command: Command, toml: TomlTable, val enum: Enum) : AbstractParameter(command, toml) {
    companion object {
        private val tomlTable by lazy { parseResource("/ctre.toml") }

        fun create(command: Command, param: String): CtreParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return CtreParameter(command, toml, Enum.valueOf(param))
        }
    }

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
        NEUTRAL_DEADBAND
    }
}