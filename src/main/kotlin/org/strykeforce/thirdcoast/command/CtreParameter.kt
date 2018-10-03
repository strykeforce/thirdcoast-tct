package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.parseResource

class CtreParameter(command: Command, toml: TomlTable) : AbstractParameter(command, toml) {
    companion object {
        private val tomlTable by lazy { parseResource("/ctre.toml") }

        fun create(command: Command, param: String): Parameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param: $param")
            return CtreParameter(command, toml)
        }
    }
}