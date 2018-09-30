package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.readBoolean
import org.strykeforce.thirdcoast.readDouble
import org.strykeforce.thirdcoast.readInt


interface Parameter {
    val name: String
    val type: String
    val desc: String
    fun readInt(terminal: Terminal, default: Int): Int
    fun readDouble(terminal: Terminal, default: Double): Double
    fun readBoolean(terminal: Terminal, default: Boolean): Boolean
}

class ParameterImpl(command: Command, toml: TomlTable) : Parameter {
    override val name = toml.getString("name") ?: "NO NAME"
    override val desc = toml.getString("desc") ?: "NO DESCRIPTION"
    override val type = toml.getString("type") ?: throw IllegalArgumentException("type missing")
    val prompt = command.prompt(name)

    override fun readInt(terminal: Terminal, default: Int): Int = terminal.lineReader().readInt(prompt, default)


    override fun readDouble(terminal: Terminal, default: Double): Double =
        terminal.lineReader().readDouble(prompt, default)

    override fun readBoolean(terminal: Terminal, default: Boolean): Boolean =
        terminal.lineReader().readBoolean(prompt, default)

}

private fun Terminal.lineReader(): LineReader = LineReaderBuilder.builder().terminal(this).build()
