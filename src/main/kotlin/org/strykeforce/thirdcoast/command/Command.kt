package org.strykeforce.thirdcoast.command

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.talon.RunTalonsCommand
import org.strykeforce.thirdcoast.talon.SelectTalonsCommand
import org.strykeforce.thirdcoast.talon.SlotParameterCommand
import org.strykeforce.thirdcoast.talon.StatusCommand

private val logger = KotlinLogging.logger {}

interface Command {
    val key: String
    val parent: Command?
    val menu: String
    val order: Int
    val children: Collection<Command>
    fun execute(): Command

    companion object {
        const val MENU_TYPE = "menu"
        const val MENU_KEY = "menu"
        const val TYPE_KEY = "type"
        const val ORDER_KEY = "order"

        fun createFromToml(toml: TomlTable, parent: MenuCommand? = null, key: String = "ROOT"): Command {

            val type = toml.getString(TYPE_KEY) ?: throw Exception("$key: $TYPE_KEY missing")

            return when (type) {
                MENU_TYPE -> {
                    val command = MenuCommand(parent, key, toml)
                    toml.keySet().filter(toml::isTable).forEach { k ->
                        val child =
                            createFromToml(
                                toml.getTable(k)!!,
                                command,
                                k
                            )
                        logger.debug { "child hashcode = ${child.hashCode()}" }
                        command.children.add(child)
                    }
                    command
                }
                "talon.select" -> SelectTalonsCommand(parent, key, toml)
                "talon.run" -> RunTalonsCommand(parent, key, toml)
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
    override val order = toml.getLong(Command.ORDER_KEY)?.toInt() ?: 0
    override val menu = toml.getString(Command.MENU_KEY) ?: key
    override val children = emptySet<Command>()

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
