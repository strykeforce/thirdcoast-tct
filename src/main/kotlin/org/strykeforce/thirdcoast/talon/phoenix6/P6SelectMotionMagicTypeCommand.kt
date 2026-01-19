package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.controller.MotionMagicType
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*

class P6SelectMotionMagicTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<MotionMagicType>(parent, key, toml,
    listOf(MotionMagicType.Standard, MotionMagicType.Velocity, MotionMagicType.Dynamic, MotionMagicType.Exponential, MotionMagicType.DynamicExponential),
    listOf("Standard (Position)", "Velocity", "(FD, Pro) Dynamic", "Exponential", "(FD, Pro) Dynamic Exponential")) {

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
                    if(bus == "rio") return talonFxService.active.firstOrNull()?.getMotionMagicType()?.ordinal?:MotionMagicType.Standard.ordinal
                    else if(bus == "canivore") return talonFxFDService.active.firstOrNull()?.getMotionMagicType()?.ordinal?:MotionMagicType.Standard.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.active.firstOrNull()?.getMotionMagicType()?.ordinal?:MotionMagicType.Standard.ordinal
                    else if(bus == "canivore") return talonFxsFDService.active.firstOrNull()?.getMotionMagicType()?.ordinal?:MotionMagicType.Standard.ordinal
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
                    talonFxService.active_MM_type = type
                    talonFxService.active.forEach { it.setMotionMagicType(type) }
                }
                else if(bus == "canivore"){
                    talonFxFDService.active_MM_type = type
                    talonFxFDService.active.forEach { it.setMotionMagicType(type) }
                }
                else throw  IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.active_MM_type = type
                    talonFxsService.active.forEach { it.setMotionMagicType(type) }
                }
                else if(bus == "canivore") {
                    talonFxsFDService.active_MM_type = type
                    talonFxsFDService.active.forEach { it.setMotionMagicType(type) }
                }
                else throw  IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}