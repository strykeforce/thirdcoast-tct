package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SetpointType
import org.strykeforce.thirdcoast.device.SetpointType.*
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<SetpointType>(parent, key, toml,
    listOf(OPEN_LOOP, POSITION, VELOCITY, MOTION_MAGIC, DIFFERENTIAL, FOLLOWER, NEUTRAL, MUSIC),
    listOf("Open Loop", "Position", "Velocity", "Motion Magic", "Differential", "Follower", "Neutral Output", "Music Tone")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.setpointType.ordinal

    override fun setActive(index: Int) {
        val mode = values[index]
        talonFxService.setpointType = mode
    }
}