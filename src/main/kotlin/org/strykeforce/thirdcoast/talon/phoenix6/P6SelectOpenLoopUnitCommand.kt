package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.controller.CTRE_Units
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*

class P6SelectOpenLoopUnitCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<CTRE_Units>(parent, key, toml,
    listOf(CTRE_Units.Percent, CTRE_Units.Voltage, CTRE_Units.Torque_Current),
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
                    if(bus == "rio") return talonFxService.active.firstOrNull()?.getOpenLoopUnits()?.ordinal?:0
                    else if(bus == "canivore") return talonFxFDService.active.firstOrNull()?.getOpenLoopUnits()?.ordinal?:0
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus =="rio") return talonFxsService.active.firstOrNull()?.getOpenLoopUnits()?.ordinal?:0
                    else if(bus=="canivore") return talonFxsFDService.active.firstOrNull()?.getOpenLoopUnits()?.ordinal?:0
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }


        }

    override fun setActive(index: Int) {
        val units = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") talonFxService.active.forEach { it.setOpenLoopUnits(units)}
                else if(bus == "canivore") talonFxFDService.active.forEach { it.setOpenLoopUnits(units) }
                else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus=="rio") talonFxsService.active.forEach { it.setOpenLoopUnits(units) }
                else if(bus=="canivore") talonFxsFDService.active.forEach { it.setOpenLoopUnits(units) }
                else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}