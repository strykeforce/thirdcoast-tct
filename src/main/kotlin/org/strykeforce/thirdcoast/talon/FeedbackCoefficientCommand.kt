package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.BaseTalonConfiguration
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.LegacyTalonFxService
import org.strykeforce.thirdcoast.device.TalonService

private val logger = KotlinLogging.logger {}

class FeedbackCoefficientCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()
    private val legacyTalonFxService: LegacyTalonFxService by inject()

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonService.timeout
    private val param = TalonParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val menu: String
        get() = formatMenu(
            when (pidIndex) {
                0 -> {
                    if(type == "srx") talonService.activeConfiguration.primaryPID.selectedFeedbackCoefficient
                    else if(type == "fx") legacyTalonFxService.activeConfiguration.primaryPID.selectedFeedbackCoefficient
                    else throw IllegalArgumentException()
                }
                else -> {
                    if(type == "srx") talonService.activeConfiguration.auxiliaryPID.selectedFeedbackCoefficient
                    else if(type == "fx") legacyTalonFxService.activeConfiguration.auxiliaryPID.selectedFeedbackCoefficient
                    else throw IllegalArgumentException()
                }
            }
        )

    override fun execute(): Command {
        var config: BaseTalonConfiguration
        if(type == "srx") config = talonService.activeConfiguration
        else if(type == "fx") config = legacyTalonFxService.activeConfiguration
        else throw IllegalArgumentException()

        val default = when (pidIndex) {
            0 -> config.primaryPID.selectedFeedbackCoefficient
            else -> config.auxiliaryPID.selectedFeedbackCoefficient
        }

        val paramValue = param.readDouble(reader, default)
        if(type == "srx") talonService.active.forEach { it.configSelectedFeedbackCoefficient(paramValue, pidIndex, talonService.timeout) }
        else if(type == "fx") legacyTalonFxService.active.forEach { it.configSelectedFeedbackCoefficient(paramValue, pidIndex, legacyTalonFxService.timeout) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }

        return super.execute()
    }

}