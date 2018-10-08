package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.ControlMode.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command


private val MODES = listOf(PercentOutput, MotionMagic, Velocity, Position, Follower)
private val LABELS = listOf("Percent Output", "Motion Magic", "Velocity", "Position", "Follower")

class SelectControlModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<ControlMode>(parent, key, toml, MODES, LABELS) {

    override fun activeIndex() = MODES.indexOf(talonService.controlMode)

    override fun setActive(index: Int) {
        talonService.controlMode = MODES[index]
    }
}
