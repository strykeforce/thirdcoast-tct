package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.NeutralModeValue
import com.ctre.phoenix6.signals.NeutralModeValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService


private val MODE = listOf(
    Coast,
    Brake
)

private val LABELS = listOf(
    "Coast",
    "Brake"
)

class P6SelectNeutralModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<NeutralModeValue>(parent, key, toml, MODE, LABELS) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.MotorOutput.NeutralMode.ordinal

    override fun setActive(index: Int) {
        val neutral = values[index]
        talonFxService.activeConfiguration.MotorOutput.NeutralMode = neutral
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
    }
}