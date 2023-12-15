package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.InvertedValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectMotorInvertCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<InvertedValue>(parent, key, toml,
    listOf(CounterClockwise_Positive, Clockwise_Positive),
    listOf("CCW positive", "CW positive")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.MotorOutput.Inverted.ordinal

    override fun setActive(index: Int) {
        val invert = values[index]
        talonFxService.activeConfiguration.MotorOutput.Inverted = invert
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration.MotorOutput) }
    }
}