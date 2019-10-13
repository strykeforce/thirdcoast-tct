package org.strykeforce.thirdcoast.spark

import com.revrobotics.CANDigitalInput
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

class SelectLimitNormalCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<CANDigitalInput.LimitSwitchPolarity>(
    parent,
    key,
    toml,
    listOf(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen, CANDigitalInput.LimitSwitchPolarity.kNormallyClosed),
    listOf("Normally Open", "Normally Closed")
) {
    private val sparkMaxService: SparkMaxService by inject()
    val isForward = toml.getBoolean("forward") ?: true

    override val activeIndex: Int
        get() = values.indexOf(
            if (isForward) sparkMaxService.fwdLimitNormal
            else sparkMaxService.revLimitNormal
        )

    override fun setActive(index: Int) {
        if (isForward){
            sparkMaxService.fwdLimitNormal = values[index]
            sparkMaxService.active.forEach{
                it.getForwardLimitSwitch(sparkMaxService.fwdLimitNormal)
            }
        }
        else {
            sparkMaxService.revLimitNormal = values[index]
            sparkMaxService.active.forEach{
                it.getReverseLimitSwitch(sparkMaxService.revLimitNormal)
            }
        }
    }
}