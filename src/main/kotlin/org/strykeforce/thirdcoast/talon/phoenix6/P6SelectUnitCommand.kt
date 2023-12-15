package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.Units

class P6SelectUnitCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<Units>(parent, key, toml,
    listOf(Units.PERCENT, Units.VOLTAGE, Units.TORQUE_CURRENT),
    listOf("Percent", "Voltage", "(Pro) TorqueCurrent")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeUnits.ordinal

    override fun setActive(index: Int) {
        val units = values[index]
        talonFxService.activeUnits = units
    }
}