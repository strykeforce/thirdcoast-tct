package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
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

    private val timeout = talonService.timeout
    private val param = CtreParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val menu: String
        get() = formatMenu(
            when (pidIndex) {
                0 -> talonService.activeConfiguration.primaryPID.selectedFeedbackCoefficient
                else -> talonService.activeConfiguration.auxilaryPID.selectedFeedbackCoefficient
            }
        )

    override fun execute(): Command {
        val config = talonService.activeConfiguration
        val default = when (pidIndex) {
            0 -> config.primaryPID.selectedFeedbackCoefficient
            else -> config.auxilaryPID.selectedFeedbackCoefficient
        }

        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { it.configSelectedFeedbackCoefficient(paramValue, pidIndex, timeout) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }

        return super.execute()
    }

}