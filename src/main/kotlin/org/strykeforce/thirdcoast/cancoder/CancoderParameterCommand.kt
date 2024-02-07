package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderService
import org.strykeforce.thirdcoast.cancoder.CancoderParameter.Enum.*
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4

private val logger = KotlinLogging.logger {  }
class CancoderParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val cancoderService: CancoderService by inject()
    private val timeout = cancoderService.timeout
    private val param = CancoderParameter.create(this, toml.getString("param")?: "UNKNOWN")

    override val menu: String
        get() {
            return when (param.enum) {
                POSITION -> formatMenu(cancoderService.active.firstOrNull()?.position?.valueAsDouble ?: 0.0, DOUBLE_FORMAT_4)
                MAG_OFFSET -> formatMenu(cancoderService.activeConfiguration.MagnetSensor.MagnetOffset)
            }
        }

    override fun execute(): Command {
        var activeConfig = cancoderService.activeConfiguration
        when(param.enum) {
            POSITION -> configDoubleParam(cancoderService.active.firstOrNull()?.position?.valueAsDouble ?: 0.0) {
                cancoder, value ->
                cancoder.setPosition(value)
            }
            MAG_OFFSET -> configDoubleParam(activeConfig.MagnetSensor.MagnetOffset) {
                cancoder, value ->
                activeConfig.MagnetSensor.MagnetOffset = value
                cancoder.configurator.apply(activeConfig)
            }
            else -> TODO("${param.name} not implemented")
        }
        return super.execute()
    }


    private fun configDoubleParam(default: Double, config: (CANcoder, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        cancoderService.active.forEach {
            config(it, paramValue)
            logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
        }
    }

    private fun configIntParameter(default: Int, config: (CANcoder, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        cancoderService.active.forEach{
            config(it, paramValue)
            logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
        }
    }

    private fun configBooleanParam(default: Boolean, config: (CANcoder, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        cancoderService.active.forEach {
            config(it, paramValue)
            logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
        }
    }
}