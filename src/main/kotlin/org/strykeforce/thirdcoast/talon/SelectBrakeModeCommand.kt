package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.NeutralMode.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.LegacyTalonFxService
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
  private val legacyTalonFxService: LegacyTalonFxService by inject()

  val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

  override val activeIndex: Int
    get() {
      if(type == "srx") return values.indexOf(talonService.neutralMode)
      else if(type == "fx") return values.indexOf(legacyTalonFxService.neutralMode)
      else throw IllegalArgumentException()
    }

  override fun setActive(index: Int) {
    if(type == "srx"){
      talonService.neutralMode = values[index]
      talonService.active.forEach { it.setNeutralMode(talonService.neutralMode) }
    } else if(type == "fx"){
      legacyTalonFxService.neutralMode = values[index]
      legacyTalonFxService.active.forEach { it.setNeutralMode(legacyTalonFxService.neutralMode) }
    }
  }
}
