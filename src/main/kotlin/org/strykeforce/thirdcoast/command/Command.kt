package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.talon.SelectTalonsCommand
import org.strykeforce.thirdcoast.talon.SlotParameterCommand
import org.strykeforce.thirdcoast.talon.StatusCommand

interface Command {
    val key: String
    val parent: Command?
    var menu: String
    val order: Int
    val children: List<Command>
    fun execute(): Command

    companion object {
        fun createFromToml(toml: TomlTable, parent: MenuCommand? = null, key: String = "ROOT"): Command {

            val type = toml.getString("type") ?: throw Exception("$key: type missing")

            return when (type) {
                "menu" -> {
                    val command = MenuCommand(parent, key, toml)
                    toml.keySet().filter(toml::isTable).forEach { k ->
                        val child =
                            createFromToml(
                                toml.getTable(k)!!,
                                command,
                                k
                            )
                        command.addChild(child)
                    }
                    command
                }
                "talon.select" -> SelectTalonsCommand(parent, key, toml)
                "talon.status" -> StatusCommand(parent, key, toml)
                "talon.slot.param" -> SlotParameterCommand(parent, key, toml)
                "test" -> TestCommand(parent, key, toml)
                else -> DefaultCommand(parent, key, toml)
            }
        }
    }
}


abstract class AbstractCommand(
    final override val parent: Command?,
    final override val key: String,
    toml: TomlTable
) : Command, KoinComponent {
    override val order = toml.getLong("order")?.toInt() ?: 0
    override var menu = toml.getString("menu") ?: key
    override val children = emptyList<Command>()

    override fun execute() = parent ?: throw IllegalStateException("parent should not be null")

    val talonService: TalonService by inject()

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
