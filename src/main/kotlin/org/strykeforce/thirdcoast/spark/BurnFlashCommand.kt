package org.strykeforce.thirdcoast.spark

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

private val logger = KotlinLogging.logger {}

class BurnFlashCommandparent(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val sparkMaxService: SparkMaxService by inject()

    override fun execute(): Command {
        sparkMaxService.active.forEach {
            logger.debug("Burning Flash")
            it.burnFlash()
            logger.debug("Done Burning Flash")
        }
        return super.execute()
    }
}