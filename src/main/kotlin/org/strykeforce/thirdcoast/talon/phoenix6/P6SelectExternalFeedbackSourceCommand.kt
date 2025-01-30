package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.ExternalFeedbackSensorSourceValue
import com.ctre.phoenix6.signals.ExternalFeedbackSensorSourceValue.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject

private val FEEDBACK = listOf(
    Commutation,
    RemoteCANcoder,
    RemotePigeon2_Yaw,
    RemotePigeon2_Pitch,
    RemotePigeon2_Roll,
    FusedCANcoder,
    SyncCANcoder,
    Quadrature,
    PulseWidth,
    RemoteCANdiPWM1,
    RemoteCANdiPWM2,
    RemoteCANdiQuadrature,
    FusedCANdiPWM1,
    FusedCANdiPWM2,
    FusedCANdiQuadrature,
    SyncCANdiPWM1,
    SyncCANdiPWM2
)

private val LABELS = listOf(
    "Commutation",
    "Remote CANcoder",
    "Remote Pigeon2 Yaw",
    "Remote Pigeon2 Pitch",
    "Remote Pigeon2 Roll",
    "(Pro) Fused CANcoder",
    "(Pro) Synced CANcoder",
    "Quadrature",
    "Pulse Width",
    "Remote CANdi PWM1",
    "Remote CANdi PWM2",
    "Remote CANdi Quadrature",
    "(Pro) Fused CANdi PWM1",
    "(Pro) Fused CANdi PWM2",
    "(Pro) Fused CANdi Quadrature",
    "(Pro) Synced CANdi PWM1",
    "(Pro) Synced CANdi PWM2"
)
class P6SelectExternalFeedbackSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ExternalFeedbackSensorSourceValue>(parent, key, toml, FEEDBACK, LABELS) {
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus=="rio") return talonFxsService.activeConfiguration.ExternalFeedback.ExternalFeedbackSensorSource.ordinal
            else return talonFxsFDService.activeConfiguration.ExternalFeedback.ExternalFeedbackSensorSource.ordinal
        }

    override fun setActive(index: Int) {
        val source = values[index]
        if(bus=="rio") {
            talonFxsService.activeConfiguration.ExternalFeedback.ExternalFeedbackSensorSource = source
            talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration.ExternalFeedback) }
        } else {
            talonFxsFDService.activeConfiguration.ExternalFeedback.ExternalFeedbackSensorSource = source
            talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration.ExternalFeedback) }
        }
    }
}