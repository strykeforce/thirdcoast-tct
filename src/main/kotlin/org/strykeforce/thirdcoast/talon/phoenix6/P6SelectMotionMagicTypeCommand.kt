package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.MM_Type
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectMotionMagicTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<MM_Type>(parent, key, toml,
    listOf(MM_Type.STANDARD, MM_Type.VELOCITY, MM_Type.DYNAMIC, MM_Type.EXPONENTIAL),
    listOf("Standard (Position)", "Velocity", "(FD) Dynamic", "Exponential")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.active_MM_type.ordinal
            else if(bus == "canivore") return talonFxFDService.active_MM_type.ordinal
            else throw IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val type = values[index]
        if(bus == "rio") talonFxService.active_MM_type = type
        else if(bus == "canivore") talonFxFDService.active_MM_type = type
        else throw  IllegalArgumentException()
    }
}