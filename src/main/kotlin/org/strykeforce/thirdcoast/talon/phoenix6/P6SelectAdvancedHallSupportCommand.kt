package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.AdvancedHallSupportValue
import com.ctre.phoenix6.signals.AdvancedHallSupportValue.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject

private val SUPPORT = listOf(
    Disabled,
    Enabled
)

private val LABELS = listOf(
    "Disabled",
    "(Pro) Enabled"
)
class P6SelectAdvancedHallSupportCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<AdvancedHallSupportValue>(parent, key, toml, SUPPORT, LABELS) {
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val activeIndex: Int
        get() {
            if(bus=="rio") return talonFxsService.activeConfiguration.Commutation.AdvancedHallSupport.ordinal
            else return talonFxsFDService.activeConfiguration.Commutation.AdvancedHallSupport.ordinal
        }

    override fun setActive(index: Int) {
        val support = values[index]
        if(bus=="rio") {
            talonFxsService.activeConfiguration.Commutation.AdvancedHallSupport = support
            talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration.Commutation) }
        } else {
            talonFxsFDService.activeConfiguration.Commutation.AdvancedHallSupport = support
            talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration.Commutation) }
        }
    }
}