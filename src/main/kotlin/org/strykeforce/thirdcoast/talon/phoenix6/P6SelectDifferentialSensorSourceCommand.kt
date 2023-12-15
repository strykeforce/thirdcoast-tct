package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.DifferentialSensorSourceValue
import com.ctre.phoenix6.signals.DifferentialSensorSourceValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

private val SENSOR = listOf(
    Disabled,
    RemoteCANcoder,
    RemotePigeon2_Pitch,
    RemotePigeon2_Roll,
    RemotePigeon2_Yaw,
    RemoteTalonFX_Diff
)

private val LABELS = listOf(
    "Disabled",
    "Remote CANcoder",
    "Remote Pigeon2 Pitch",
    "Remote Pigeon2 Roll",
    "Remote Pigeon2 Yaw",
    "Remote TalonFX Differential"
)

class P6SelectDifferentialSensorSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<DifferentialSensorSourceValue>(parent, key, toml, SENSOR, LABELS) {

    private  val  talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.DifferentialSensors.DifferentialSensorSource.ordinal

    override fun setActive(index: Int) {
        val sensor = values[index]
        talonFxService.activeConfiguration.DifferentialSensors.DifferentialSensorSource = sensor
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
    }
}