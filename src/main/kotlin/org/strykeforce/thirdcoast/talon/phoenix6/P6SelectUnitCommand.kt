package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*

class P6SelectUnitCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<Units>(parent, key, toml,
    listOf(Units.PERCENT, Units.VOLTAGE, Units.TORQUE_CURRENT),
    listOf("Percent", "Voltage", "(Pro) TorqueCurrent")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.activeUnits.ordinal
                    else if(bus == "canivore") return talonFxFDService.activeUnits.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus =="rio") return talonFxService.activeUnits.ordinal
                    else if(bus=="canivore") return talonFxFDService.activeUnits.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }


        }

    override fun setActive(index: Int) {
        val units = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") talonFxService.activeUnits = units
                else if(bus == "canivore") talonFxFDService.activeUnits = units
                else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus=="rio") talonFxsService.activeUnits = units
                else if(bus=="canivore") talonFxsFDService.activeUnits = units
                else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}