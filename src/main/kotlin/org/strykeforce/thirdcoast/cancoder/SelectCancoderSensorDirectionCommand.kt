package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.signals.SensorDirectionValue
import com.ctre.phoenix6.signals.SensorDirectionValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderService

private val DIRECTION = listOf(
    Clockwise_Positive,
    CounterClockwise_Positive
)

private val LABELS = listOf(
    "Clockwise Positive",
    "Counter-clockwise Positive"
)

class SelectCancoderSensorDirectionCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<SensorDirectionValue>(parent, key, toml, DIRECTION, LABELS) {

    private val cancoderService: CancoderService by inject()

    override val activeIndex: Int
        get() = cancoderService.activeConfiguration.MagnetSensor.SensorDirection.ordinal

    override fun setActive(index: Int) {
        val direction = values[index]
        cancoderService.activeConfiguration.MagnetSensor.SensorDirection = direction
        cancoderService.active.forEach{it.configurator.apply(cancoderService.activeConfiguration)}
    }
}