package org.strykeforce.thirdcoast.spark

import com.ctre.phoenix.motorcontrol.ControlMode
import com.revrobotics.ControlType
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

class SelectControlTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<ControlType>(
    parent,
    key,
    toml,
    listOf(
        ControlType.kDutyCycle,
        ControlType.kSmartMotion,
        ControlType.kVelocity,
        ControlType.kSmartVelocity,
        ControlType.kPosition,
        ControlType.kVoltage,
        ControlType.kCurrent
    ),
    listOf("Duty Cycle", "Smart Motion", "Velocity", "Smart Velocity", "Position", "Voltage", "Current")
) {
    private val sparkMaxService: SparkMaxService by inject()

    override val activeIndex: Int
        get() = values.indexOf(sparkMaxService.controlType)

    override fun setActive(index: Int) {
        sparkMaxService.controlType = values[index]
    }
}