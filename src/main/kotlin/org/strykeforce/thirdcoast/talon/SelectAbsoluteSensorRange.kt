package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.sensors.AbsoluteSensorRange
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.LegacyTalonFxService

private val RANGE = listOf(
    AbsoluteSensorRange.Signed_PlusMinus180,
    AbsoluteSensorRange.Unsigned_0_to_360
)

private val LABELS = listOf(
    "-180 - +180 degrees",
    "0 - 360 degrees"
)

class SelectAbsoluteSensorRange(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<AbsoluteSensorRange>(parent, key, toml, RANGE, LABELS) {

    private val legacyTalonFxService: LegacyTalonFxService by inject()

    override val activeIndex: Int
        get() = values.indexOf(legacyTalonFxService.activeConfiguration.absoluteSensorRange)

    override fun setActive(index: Int) {
        val range = values[index]
        legacyTalonFxService.active.forEach { it.configIntegratedSensorAbsoluteRange(range, legacyTalonFxService.timeout) }
        legacyTalonFxService.activeConfiguration.absoluteSensorRange = range
    }
}