package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.DifferentialType
import org.strykeforce.thirdcoast.device.DifferentialType.*
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectDifferentialTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<DifferentialType>(parent, key, toml,
    listOf(OPEN_LOOP, POSITION, VELOCITY, MOTION_MAGIC, FOLLOWER),
    listOf("Open Loop", "Position", "Velocity", "Motion Magic", "Follower")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.differentialType.ordinal

    override fun setActive(index: Int) {
        val type = values[index]
        talonFxService.differentialType = type
    }
}