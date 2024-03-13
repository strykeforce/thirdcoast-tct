package org.strykeforce.thirdcoast.cancoder

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractParameter
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.parseResource

class CancoderParameter(
    command: Command,
    toml: TomlTable,
    val enum: Enum
): AbstractParameter(command, toml) {
    enum class Enum {
        POSITION,
        MAG_OFFSET
    }

    companion object {
        private  val tomlTable by lazy { parseResource("/cancoder.toml")}

        fun create(command: Command, param: String): CancoderParameter {
            val toml = tomlTable.getTable(param) ?: throw java.lang.IllegalArgumentException("missing param $param")
            return CancoderParameter(command, toml, Enum.valueOf(param))
        }
    }
}