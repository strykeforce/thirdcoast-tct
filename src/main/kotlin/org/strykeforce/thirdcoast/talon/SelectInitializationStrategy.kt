package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.MotorCommutation
import com.ctre.phoenix.sensors.SensorInitializationStrategy
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

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

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = values.indexOf(talonFxService.activeConfiguration.initializationStrategy)

    override fun setActive(index: Int) {
        val mode = values[index]
        talonFxService.active.forEach { it.configIntegratedSensorInitializationStrategy(mode, talonFxService.timeout) }
        talonFxService.activeConfiguration.initializationStrategy = mode
    }
}