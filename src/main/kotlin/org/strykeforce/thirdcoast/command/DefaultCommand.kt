package org.strykeforce.thirdcoast.command

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.terminal.Terminal

class DefaultCommand(parent: Command?, key: String, toml: TomlTable) : AbstractCommand(parent, key, toml) {
    private val logger = KotlinLogging.logger {}

    override fun execute(terminal: Terminal): Command {
        logger.debug("executing {}", key)
        return super.execute(terminal)
    }
}