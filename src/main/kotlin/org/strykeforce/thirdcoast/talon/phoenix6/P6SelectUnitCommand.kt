package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
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
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeUnits.ordinal
            else if(bus == "canivore") return talonFxFDService.activeUnits.ordinal
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        val units = values[index]
        if(bus == "rio") talonFxService.activeUnits = units
        else if(bus == "canivore") talonFxFDService.activeUnits = units
        else throw IllegalArgumentException()
    }
}