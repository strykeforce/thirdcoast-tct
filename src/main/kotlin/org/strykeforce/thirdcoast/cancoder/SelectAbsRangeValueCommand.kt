package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
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

    override val activeIndex: Int
        get() = cancoderService.activeConfiguration.MagnetSensor.AbsoluteSensorRange.ordinal

    override fun setActive(index: Int) {
        val range = values[index]
        cancoderService.activeConfiguration.MagnetSensor.AbsoluteSensorRange = range
        cancoderService.active.forEach{it.configurator.apply(cancoderService.activeConfiguration)}
    }
}