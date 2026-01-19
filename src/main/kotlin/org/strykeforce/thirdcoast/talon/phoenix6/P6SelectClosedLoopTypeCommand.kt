package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.strykeforce.controller.CTRE_ClosedLoopType
import org.strykeforce.controller.CTRE_FollowerType
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject

class P6SelectClosedLoopTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<CTRE_ClosedLoopType>(parent, key, toml,
    listOf(CTRE_ClosedLoopType.Position, CTRE_ClosedLoopType.Velocity, CTRE_ClosedLoopType.Motion_Magic),
    listOf("Position", "Velocity", "Motion Magic")) {

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
                    if(bus == "rio") return talonFxService.active.firstOrNull()?.getClosedLoopType()?.ordinal ?: CTRE_ClosedLoopType.Position.ordinal
                    else if(bus == "canivore") return talonFxFDService.active.firstOrNull()?.getClosedLoopType()?.ordinal ?: CTRE_ClosedLoopType.Position.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.active.firstOrNull()?.getClosedLoopType()?.ordinal ?: CTRE_ClosedLoopType.Position.ordinal
                    else if(bus == "canivore") return talonFxsFDService.active.firstOrNull()?.getClosedLoopType()?.ordinal ?: CTRE_ClosedLoopType.Position.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }
        }

    override fun setActive(index: Int) {
        val type = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.active.forEach { it.closedLoopType = type }
                }
                else if(bus == "canivore"){
                    talonFxFDService.active.forEach { it.closedLoopType = type }
                }
                else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.active.forEach { it.closedLoopType = type }
                }
                else if(bus == "canivore") {
                    talonFxsFDService.active.forEach { it.closedLoopType = type }
                }
                else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }
    }

}