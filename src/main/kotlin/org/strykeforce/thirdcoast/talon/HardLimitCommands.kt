package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal.*
import com.ctre.phoenix.motorcontrol.LimitSwitchSource
import com.ctre.phoenix.motorcontrol.LimitSwitchSource.*
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import com.ctre.phoenix.motorcontrol.can.BaseTalonConfiguration
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

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

    private val talonService: TalonService by inject()

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    val isForward = toml.getBoolean("forward") ?: true

    val activeSource
        get() = values[activeIndex]


    override val activeIndex: Int
        get() {
            var config: BaseTalonConfiguration = talonService.activeConfiguration
            return values.indexOf(
                    if (isForward)
                        config.forwardLimitSwitchSource
                    else
                        config.reverseLimitSwitchSource
            )
        }

    override fun setActive(index: Int) {
        var active: Set<BaseTalon>
        var config: BaseTalonConfiguration

        active = talonService.active
        config = talonService.activeConfiguration

        val source = values[index]
        val normal = getNormal()
        if (isForward) {
            active.forEach { it.configForwardLimitSwitchSource(source, normal) }
            config.forwardLimitSwitchSource = source
            logger.info { "set forward hard limit to $source, $normal" }
        } else {
            active.forEach { it.configReverseLimitSwitchSource(source, normal) }
            config.reverseLimitSwitchSource = source
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

    private val talonService: TalonService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    val isForward = toml.getBoolean("forward") ?: true

    var activeConfig = BaseTalonConfiguration(FeedbackDevice.CTRE_MagEncoder_Relative)

    val activeNormal
        get() = values[activeIndex]

    override val activeIndex: Int
        get() {
            activeConfig = talonService.activeConfiguration
            return values.indexOf(
                    if (isForward)
                        activeConfig.forwardLimitSwitchNormal
                    else
                        activeConfig.reverseLimitSwitchNormal
            )
        }

    override fun setActive(index: Int) {
        val active: Set<BaseTalon>

        activeConfig = talonService.activeConfiguration
        active = talonService.active

        val source = getSource()
        val normal = values[index]
        if (isForward) {
            active.forEach { it.configForwardLimitSwitchSource(source, normal) }
            activeConfig.forwardLimitSwitchNormal = normal
            logger.info { "set forward hard limit to $source, $normal" }
        } else {
            active.forEach { it.configReverseLimitSwitchSource(source, normal) }
            activeConfig.reverseLimitSwitchNormal = normal
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
