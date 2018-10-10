package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.NeutralMode.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command


private val MODES = listOf(EEPROMSetting, Brake, Coast)
private val LABELS = listOf("B/C CAL Button", "Brake", "Coast")

class SelectBrakeModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<NeutralMode>(parent, key, toml, MODES, LABELS) {

    override fun activeIndex() = MODES.indexOf(talonService.neutralMode)

    override fun setActive(index: Int) {
        talonService.neutralMode = MODES[index]
        talonService.active.forEach { it.setNeutralMode(talonService.neutralMode) }
    }
}
