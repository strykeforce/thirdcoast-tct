package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.talon.SlotParameterCommand

interface Command {
    val key: String
    val parent: Command?
    val menu: String
    val order: Int
    val children: List<Command>
    fun execute(terminal: Terminal): Command

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
                "slot.param" -> SlotParameterCommand(parent, key, toml)
                "test" -> TestCommand(parent, key, toml)
                else -> DefaultCommand(parent, key, toml)
            }
        }
    }
}
