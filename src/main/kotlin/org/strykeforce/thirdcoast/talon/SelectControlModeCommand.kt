package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.ControlMode.*
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
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

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val activeIndex: Int
        get() {
            return values.indexOf(talonService.controlMode)
        }

    override fun setActive(index: Int) {
        talonService.controlMode = values[index]
    }
}
