package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderFDService
import org.strykeforce.thirdcoast.device.CancoderService

private val RANGE_VALUE = listOf(
    Unsigned_0To1,
    Signed_PlusMinusHalf
)

private val LABELS = listOf(
    "0 to 1",
    "-0.5 to 0.5"
)

class SelectAbsRangeValueCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<AbsoluteSensorRangeValue>(parent, key, toml, RANGE_VALUE, LABELS) {
    private val cancoderService: CancoderService by inject()
    private val cancoderFDService: CancoderFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus == "rio") return cancoderService.activeConfiguration.MagnetSensor.AbsoluteSensorRange.ordinal
            else if(bus == "canivore") return cancoderFDService.activeConfiguration.MagnetSensor.AbsoluteSensorRange.ordinal
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        val range = values[index]
        if(bus == "rio") {
            cancoderService.activeConfiguration.MagnetSensor.AbsoluteSensorRange = range
            cancoderService.active.forEach{it.configurator.apply(cancoderService.activeConfiguration)}
        } else if(bus == "canivore") {
            cancoderFDService.activeConfiguration.MagnetSensor.AbsoluteSensorRange = range
            cancoderFDService.active.forEach { it.configurator.apply(cancoderFDService.activeConfiguration.MagnetSensor) }
        }
    }
}