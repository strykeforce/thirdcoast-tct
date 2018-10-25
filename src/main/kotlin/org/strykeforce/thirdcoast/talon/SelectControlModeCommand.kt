package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.ControlMode.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

class SelectControlModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<ControlMode>(
    parent,
    key,
    toml,
    listOf(PercentOutput, MotionMagic, Velocity, Position, Follower),
    listOf("Percent Output", "Motion Magic", "Velocity", "Position", "Follower")
) {
    private val talonService: TalonService by inject()

    override val activeIndex
        get() = values.indexOf(talonService.controlMode)

    override fun setActive(index: Int) {
        talonService.controlMode = values[index]
    }
}
