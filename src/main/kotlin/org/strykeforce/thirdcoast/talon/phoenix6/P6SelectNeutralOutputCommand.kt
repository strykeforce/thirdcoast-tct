package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.NeutralModeValue
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectNeutralOutputCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<NeutralModeValue>(parent, key, toml,
    listOf(NeutralModeValue.Brake, NeutralModeValue.Coast),
    listOf("Brake", "Coast")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeNeutralOut.ordinal

    override fun setActive(index: Int) {
        val neutral = values[index]
        talonFxService.activeNeutralOut = neutral
    }
}