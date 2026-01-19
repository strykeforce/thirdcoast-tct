package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.BrushedMotorWiringValue
import com.ctre.phoenix6.signals.BrushedMotorWiringValue.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject

private val WIRING = listOf(
    Leads_A_and_B,
    Leads_A_and_C,
    Leads_B_and_C
)

private val LABELS = listOf(
    "Leads A & B",
    "Leads A & C",
    "Leads B & C"
)
class P6SelectBrushedWiringCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<BrushedMotorWiringValue>(parent, key, toml, WIRING, LABELS) {
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus=="rio") return talonFxsService.activeConfiguration.Commutation.BrushedMotorWiring.ordinal
            else return talonFxsFDService.activeConfiguration.Commutation.BrushedMotorWiring.ordinal
        }

    override fun setActive(index: Int) {
        val wiring = values[index]
        if(bus=="rio") {
            talonFxsService.activeConfiguration.Commutation.BrushedMotorWiring = wiring
            talonFxsService.active.forEach { it.talonFXS.configurator.apply(talonFxsService.activeConfiguration.Commutation) }
        } else {
            talonFxsFDService.activeConfiguration.Commutation.BrushedMotorWiring = wiring
            talonFxsFDService.active.forEach { it.talonFXS.configurator.apply(talonFxsFDService.activeConfiguration.Commutation) }
        }
    }


}