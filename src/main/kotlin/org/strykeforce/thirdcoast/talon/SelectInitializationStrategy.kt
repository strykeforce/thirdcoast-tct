package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.sensors.SensorInitializationStrategy
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.LegacyTalonFxService

private val STRATEGY = listOf(
    SensorInitializationStrategy.BootToAbsolutePosition,
    SensorInitializationStrategy.BootToZero
)

private val LABELS = listOf(
    "Absolute Position",
    "Zero"
)

class SelectInitializationStrategy(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<SensorInitializationStrategy>(parent, key, toml, STRATEGY, LABELS) {

    private val legacyTalonFxService: LegacyTalonFxService by inject()

    override val activeIndex: Int
        get() = values.indexOf(legacyTalonFxService.activeConfiguration.initializationStrategy)

    override fun setActive(index: Int) {
        val mode = values[index]
        legacyTalonFxService.active.forEach { it.configIntegratedSensorInitializationStrategy(mode, legacyTalonFxService.timeout) }
        legacyTalonFxService.activeConfiguration.initializationStrategy = mode
    }
}