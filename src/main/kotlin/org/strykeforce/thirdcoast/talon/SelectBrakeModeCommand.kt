package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.NeutralMode.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

class SelectBrakeModeCommand(
  parent: Command?,
  key: String,
  toml: TomlTable
) : AbstractSelectCommand<NeutralMode>(
  parent,
  key,
  toml,
  listOf(EEPROMSetting, Brake, Coast),
  listOf("B/C CAL Button", "Brake", "Coast")
) {
  private val talonService: TalonService by inject()
  override val activeIndex
    get() = values.indexOf(talonService.neutralMode)

  override fun setActive(index: Int) {
    talonService.neutralMode = values[index]
    talonService.active.forEach { it.setNeutralMode(talonService.neutralMode) }
  }
}
