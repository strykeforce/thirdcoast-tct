package org.strykeforce.thirdcoast.spark

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command

private val logger = KotlinLogging.logger {}

class HelloCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        logger.debug { "Hello from Spark Max" }
        return super.execute()
    }
}
