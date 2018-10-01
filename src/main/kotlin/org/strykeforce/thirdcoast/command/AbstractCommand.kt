package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.ServoService
import org.strykeforce.thirdcoast.TalonService

abstract class AbstractCommand(final override val parent: Command?, final override val key: String, toml: TomlTable) :
    Command, KoinComponent {
    override val order = toml.getLong("order")?.toInt() ?: 0
    override val menu = toml.getString("menu") ?: key
    override val children = emptyList<Command>()
    override fun execute() = parent ?: throw IllegalStateException("parent should not be null")

    val talonService: TalonService by inject(name = "Talon")
    val servoService: ServoService by inject(name = "Servo")

    val terminal: Terminal by inject()
    val reader: LineReader by inject()

    override fun toString(): String {
        var s = key
        val depth = depth()

        if (!children.isEmpty()) {
            s += " {\n"
            for (child in children) {
                s += "${indent(depth + 1)}$child\n"
            }
            s += "${indent(depth)}}"
        }
        return s
    }

    private fun depth(): Int {
        var depth = 0
        var arg = this.parent
        while (arg != null) {
            ++depth
            arg = arg.parent
        }
        return depth
    }

    private fun indent(nb: Int) = "  ".repeat(nb)

}
