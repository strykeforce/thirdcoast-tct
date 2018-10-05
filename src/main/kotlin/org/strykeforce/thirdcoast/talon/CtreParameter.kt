package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.ParamEnum
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class CtreParameter(command: Command, toml: TomlTable, val enum: ParamEnum) : AbstractParameter(command, toml) {
    companion object {
        private val tomlTable by lazy { parseResource("/ctre.toml") }

        fun create(command: Command, param: String) : CtreParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return CtreParameter(command, toml, ParamEnum.valueOf(param))
        }
    }
}