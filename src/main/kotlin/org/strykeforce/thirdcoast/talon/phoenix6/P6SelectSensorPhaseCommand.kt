package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.SensorPhaseValue
import com.ctre.phoenix6.signals.SensorPhaseValue.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject

private val PHASE = listOf(
    Aligned,
    Opposed
)

private val LABELS = listOf(
    "Aligned",
    "Opposed"
)
class P6SelectSensorPhaseCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<SensorPhaseValue>(parent, key, toml, PHASE, LABELS) {
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus=="rio") return talonFxsService.activeConfiguration.ExternalFeedback.SensorPhase.ordinal
            else return talonFxsFDService.activeConfiguration.ExternalFeedback.SensorPhase.ordinal
        }

    override fun setActive(index: Int) {
        val phase = values[index]
        if(bus=="rio") {
            talonFxsService.activeConfiguration.ExternalFeedback.SensorPhase = phase
            talonFxsService.active.forEach { it.talonFXS.configurator.apply(talonFxsService.activeConfiguration.ExternalFeedback) }
        } else {
            talonFxsFDService.activeConfiguration.ExternalFeedback.SensorPhase = phase
            talonFxsFDService.active.forEach { it.talonFXS.configurator.apply(talonFxsFDService.activeConfiguration.ExternalFeedback) }
        }
    }
}