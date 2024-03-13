package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.signals.SensorDirectionValue
import com.ctre.phoenix6.signals.SensorDirectionValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderFDService
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
    private val cancoderFDService: CancoderFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus == "rio") return cancoderService.activeConfiguration.MagnetSensor.SensorDirection.ordinal
            else if(bus == "canivore") return cancoderFDService.activeConfiguration.MagnetSensor.SensorDirection.ordinal
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        val direction = values[index]
        if(bus == "rio") {
            cancoderService.activeConfiguration.MagnetSensor.SensorDirection = direction
            cancoderService.active.forEach{it.configurator.apply(cancoderService.activeConfiguration)}
        } else if(bus == "canivore") {
            cancoderFDService.activeConfiguration.MagnetSensor.SensorDirection = direction
            cancoderFDService.active.forEach { it.configurator.apply(cancoderFDService.activeConfiguration.MagnetSensor) }
        } else throw IllegalArgumentException()
    }
}