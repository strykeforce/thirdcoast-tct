package org.strykeforce.thirdcoast.spark

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

private val logger = KotlinLogging.logger {}

class SetSensorPositionCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val sparkMaxService: SparkMaxService by inject()
    private val param = SparkMaxParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() = formatMenu(sparkMaxService.active.map { it.getEncoder(sparkMaxService.feedbackSensor.sensorType,
            sparkMaxService.feedbackSensor.cpr).position }.joinToString())

    override fun execute(): Command {
        val default = 0.0
        val paramValue = param.readDouble(reader, default)
        sparkMaxService.active.forEach { it.getEncoder(sparkMaxService.feedbackSensor.sensorType,
            sparkMaxService.feedbackSensor.cpr).setPosition(paramValue) }
        logger.debug { "set ${sparkMaxService.active.size} spark max ${param.name}: $paramValue" }

        return super.execute()
    }
}