package org.strykeforce.thirdcoast.spark

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

class SparkMaxStatusCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val sparkMaxService: SparkMaxService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        sparkMaxService.active.forEach {
            writer.println(sparkMaxService.activeSlot.toString())
            writer.println(sparkMaxService.feedbackSensor.toString())
            writer.println(sparkMaxService.smartCurrentLimit.toString())
            writer.println(sparkMaxService.secondaryCurrentLimit.toString())
            writer.println(sparkMaxService.active.forEach { it.lastError.toString() })
        }
        return super.execute()
    }

}