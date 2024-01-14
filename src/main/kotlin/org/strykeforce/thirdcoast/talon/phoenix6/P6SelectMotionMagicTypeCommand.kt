package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.MM_Type
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectMotionMagicTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<MM_Type>(parent, key, toml,
    listOf(MM_Type.STANDARD, MM_Type.VELOCITY, MM_Type.DYNAMIC, MM_Type.EXPONENTIAL),
    listOf("Standard (Position)", "Velocity", "(FD) Dynamic", "Exponential")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.active_MM_type.ordinal

    override fun setActive(index: Int) {
        val type = values[index]
        talonFxService.active_MM_type = type
    }
}