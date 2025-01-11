package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.FeedbackSensorSourceValue
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue.*
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

private val SENSORS = listOf(
    FusedCANcoder,
    RemoteCANcoder,
    RemotePigeon2_Pitch,
    RemotePigeon2_Roll,
    RemotePigeon2_Yaw,
    RotorSensor,
    SyncCANcoder
)

private val LABELS = listOf(
    "(Pro) Fused CANcoder",
    "Remote CANcoder",
    "Remote Pigeon2 Pitch",
    "Remote Pigeon2 Roll",
    "Remote Pigeon2 Yaw",
    "Rotor Sensor",
    "(Pro) Sync CANcoder"
)

class P6SelectFeedbackSensorCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<FeedbackSensorSourceValue>(parent, key, toml, SENSORS, LABELS) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") {
                val sensor = talonFxService.activeConfiguration.Feedback.FeedbackSensorSource
                return values.indexOf(sensor)
            } else if(bus == "canivore") {
                val sensor = talonFxFDService.activeConfiguration.Feedback.FeedbackSensorSource
                return values.indexOf(sensor)
            }else throw IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val sensor = values[index]
        if(bus == "rio") {
            talonFxService.activeConfiguration.Feedback.FeedbackSensorSource = sensor
            talonFxService.active.forEach {
                it.configurator.apply(talonFxService.activeConfiguration)
            }
        } else if(bus == "canivore") {
            talonFxFDService.activeConfiguration.Feedback.FeedbackSensorSource = sensor
            talonFxService.active.forEach {
                it.configurator.apply(talonFxFDService.activeConfiguration)
            }
        } else throw  IllegalArgumentException()
    }

}