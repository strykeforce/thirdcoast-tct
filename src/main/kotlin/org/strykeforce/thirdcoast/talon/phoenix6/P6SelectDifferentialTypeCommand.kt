package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.controller.CTRE_DifferentialType
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*
import org.strykeforce.thirdcoast.device.DifferentialType.*

class P6SelectDifferentialTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<CTRE_DifferentialType>(parent, key, toml,
    listOf(CTRE_DifferentialType.Open_Loop, CTRE_DifferentialType.Position, CTRE_DifferentialType.Velocity, CTRE_DifferentialType.Motion_Magic, CTRE_DifferentialType.Follower),
    listOf("Open Loop", "Position", "Velocity", "Motion Magic", "Follower")) {

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
                    if(bus == "rio") return talonFxService.active.firstOrNull()?.getFollowerConfig()?.ordinal?:CTRE_DifferentialType.Open_Loop.ordinal
                    else if(bus == "canivore") return talonFxFDService.active.firstOrNull()?.getFollowerConfig()?.ordinal?:CTRE_DifferentialType.Open_Loop.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.active.firstOrNull()?.getFollowerConfig()?.ordinal?:CTRE_DifferentialType.Open_Loop.ordinal
                    else if(bus == "canivore") return talonFxsFDService.active.firstOrNull()?.getFollowerConfig()?.ordinal?:CTRE_DifferentialType.Open_Loop.ordinal
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
                    talonFxService.differentialType = type
                    talonFxService.active.forEach { it.differentialType = type }
                } else if(bus == "canivore") {
                    talonFxFDService.differentialType = type
                    talonFxFDService.active.forEach { it.differentialType = type }
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.differentialType = type
                    talonFxsService.active.forEach { it.differentialType = type }
                } else if(bus == "canivore") {
                    talonFxsFDService.differentialType = type
                    talonFxsFDService.active.forEach { it.differentialType = type }
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }
    }
}