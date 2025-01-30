package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.BaseTalonConfiguration
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

private val logger = KotlinLogging.logger {}

class FeedbackCoefficientCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonService.timeout
    private val param = TalonParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val menu: String
        get() = formatMenu(
            when (pidIndex) {
                0 -> {
                    talonService.activeConfiguration.primaryPID.selectedFeedbackCoefficient
                }
                else -> {
                    talonService.activeConfiguration.auxiliaryPID.selectedFeedbackCoefficient
                }
            }
        )

    override fun execute(): Command {
        var config: BaseTalonConfiguration
        config = talonService.activeConfiguration

        val default = when (pidIndex) {
            0 -> config.primaryPID.selectedFeedbackCoefficient
            else -> config.auxiliaryPID.selectedFeedbackCoefficient
        }

        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { it.configSelectedFeedbackCoefficient(paramValue, pidIndex, talonService.timeout) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }

        return super.execute()
    }

}