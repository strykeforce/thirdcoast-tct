package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal.*
import com.ctre.phoenix.motorcontrol.LimitSwitchSource
import com.ctre.phoenix.motorcontrol.LimitSwitchSource.*
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command

private val logger = KotlinLogging.logger {}

class SelectHardLimitSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<LimitSwitchSource>(
    parent,
    key,
    toml,
    listOf(Deactivated, FeedbackConnector, RemoteCANifier, RemoteTalonSRX),
    listOf("Deactivated", "Feedback Connector", "Remote CANifier", "Remote TalonSRX")
) {

    val isForward = toml.getBoolean("forward") ?: true

    val activeSource
        get() = values[activeIndex]


    override val activeIndex
        get() = values.indexOf(
            if (isForward)
                talonService.activeConfiguration.forwardLimitSwitchSource
            else
                talonService.activeConfiguration.reverseLimitSwitchSource
        )

    override fun setActive(index: Int) {
        val active = talonService.active
        val source = values[index]
        val normal = getNormal()
        if (isForward) {
            active.forEach { it.configForwardLimitSwitchSource(source, normal) }
            talonService.activeConfiguration.forwardLimitSwitchSource = source
            logger.info { "set forward hard limit to $source, $normal" }
        } else {
            active.forEach { it.configReverseLimitSwitchSource(source, normal) }
            talonService.activeConfiguration.reverseLimitSwitchSource = source
            logger.info { "set reverse hard limit to $source, $normal" }
        }
    }

    private fun getNormal(): LimitSwitchNormal {
        require(parent != null)
        val command =
            parent.children.find {
                it is SelectHardLimitNormalCommand && it.isForward == isForward
            } as SelectHardLimitNormalCommand
        return command.activeNormal
    }
}

class SelectHardLimitNormalCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<LimitSwitchNormal>(
    parent,
    key,
    toml,
    listOf(Disabled, NormallyClosed, NormallyOpen),
    listOf("Disabled", "Normally Closed", "Normally Open")
) {

    val isForward = toml.getBoolean("forward") ?: true

    val activeNormal
        get() = values[activeIndex]

    override val activeIndex
        get() = values.indexOf(
            if (isForward)
                talonService.activeConfiguration.forwardLimitSwitchNormal
            else
                talonService.activeConfiguration.reverseLimitSwitchNormal
        )

    override fun setActive(index: Int) {
        val active = talonService.active
        val source = getSource()
        val normal = values[index]
        if (isForward) {
            active.forEach { it.configForwardLimitSwitchSource(source, normal) }
            talonService.activeConfiguration.forwardLimitSwitchNormal = normal
            logger.info { "set forward hard limit to $source, $normal" }
        } else {
            active.forEach { it.configReverseLimitSwitchSource(source, normal) }
            talonService.activeConfiguration.reverseLimitSwitchNormal = normal
            logger.info { "set reverse hard limit to $source, $normal" }
        }
    }

    private fun getSource(): LimitSwitchSource {
        require(parent != null)
        val command =
            parent.children.find {
                it is SelectHardLimitSourceCommand && it.isForward == isForward
            } as SelectHardLimitSourceCommand
        return command.activeSource

    }

}
