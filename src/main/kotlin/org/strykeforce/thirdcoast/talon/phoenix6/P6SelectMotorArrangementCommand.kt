package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.MotorArrangementValue
import com.ctre.phoenix6.signals.MotorArrangementValue.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject

private val MOTOR_TYPE = listOf(
    Disabled,
    Minion_JST,
    Brushed_DC,
    NEO_JST,
    NEO550_JST,
    VORTEX_JST
)

private val LABELS = listOf(
    "Disabled",
    "Minion JST",
    "Brushed DC",
    "NEO JST",
    "NEO 550 JST",
    "Vortex JST"
)
class P6SelectMotorArrangementCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<MotorArrangementValue>(parent, key, toml, MOTOR_TYPE, LABELS) {
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus=="rio") return talonFxsService.activeConfiguration.Commutation.MotorArrangement.ordinal
            else return talonFxsFDService.activeConfiguration.Commutation.MotorArrangement.ordinal
        }

    override fun setActive(index: Int) {
        val type = values[index]
        if(bus=="rio") {
            talonFxsService.activeConfiguration.Commutation.MotorArrangement = type
            talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration.Commutation) }
        } else {
            talonFxsFDService.activeConfiguration.Commutation.MotorArrangement = type
            talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration.Commutation) }
        }
    }
}