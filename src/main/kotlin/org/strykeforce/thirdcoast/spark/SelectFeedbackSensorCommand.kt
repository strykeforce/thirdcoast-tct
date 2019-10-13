package org.strykeforce.thirdcoast.spark

import com.revrobotics.SensorType
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

private val SENSORS = listOf(
    SensorType.kHallSensor
)
private val LABELS = listOf(
    "Hall Sensor"
)

class SelectFeedbackSensorCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<SensorType>(parent, key, toml, SENSORS, LABELS) {

    private val sparkMaxService: SparkMaxService by inject()

    override val activeIndex: Int
        get() = values.indexOf(sparkMaxService.feedbackSensor.sensorType)

    override fun setActive(index: Int) {
        sparkMaxService.feedbackSensor.sensorType = values[index]
    }
}