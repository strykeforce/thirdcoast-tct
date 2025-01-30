package org.strykeforce.thirdcoast.cancoder

import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import edu.wpi.first.units.Units
import edu.wpi.first.units.Units.Rotations
import edu.wpi.first.units.measure.Angle
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
//import org.koin.standalone.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CancoderService
import org.strykeforce.thirdcoast.cancoder.CancoderParameter.Enum.*
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.CancoderFDService

private val logger = KotlinLogging.logger {  }
class CancoderParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val cancoderService: CancoderService by inject()
    private val cancoderFDService: CancoderFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    private val timeout = cancoderService.timeout
    private val param = CancoderParameter.create(this, toml.getString("param")?: "UNKNOWN")

    override val menu: String
        get() {
            return when (param.enum) {
                POSITION -> formatMenu(
                    if(bus == "rio") cancoderService.active.firstOrNull()?.position?.valueAsDouble ?: 0.0
                    else cancoderFDService.active.firstOrNull()?.position?.valueAsDouble ?: 0.0, DOUBLE_FORMAT_4)
                MAG_OFFSET -> formatMenu(
                    if(bus == "rio") cancoderService.activeConfiguration.MagnetSensor.MagnetOffset
                    else cancoderFDService.activeConfiguration.MagnetSensor.MagnetOffset)
                DISCONTINUITY_POINT -> formatMenu(
                    if(bus == "rio") cancoderService.activeConfiguration.MagnetSensor.AbsoluteSensorDiscontinuityPoint
                    else cancoderFDService.activeConfiguration.MagnetSensor.AbsoluteSensorDiscontinuityPoint
                )
            }
        }

    override fun execute(): Command {
        var activeConfig = if(bus == "rio") cancoderService.activeConfiguration else cancoderFDService.activeConfiguration
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
            DISCONTINUITY_POINT -> configDoubleParam(activeConfig.MagnetSensor.absoluteSensorDiscontinuityPointMeasure.`in`(
                Rotations)) {
                caNcoder, value ->
                activeConfig.MagnetSensor.AbsoluteSensorDiscontinuityPoint = value
                caNcoder.configurator.apply(activeConfig)
            }
            else -> TODO("${param.name} not implemented")
        }
        return super.execute()
    }


    private fun configDoubleParam(default: Double, config: (CANcoder, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        if(bus == "rio") {
            cancoderService.active.forEach {
                config(it, paramValue)
                logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
            }
        } else if(bus == "canivore") {
            cancoderFDService.active.forEach {
                config(it, paramValue)
            logger.info { "set ${cancoderFDService.active.size} CANcoder's ${param.name}: $paramValue" }}
        } else throw IllegalArgumentException()
    }

    private fun configAngleParameter(default: Angle, config: (CANcoder, Angle) -> Unit) {
        val paramValue = param.readDouble(reader, default.`in`(Units.Rotations))
        if(bus == "rio") {
            cancoderService.active.forEach {
                config(it, Rotations.of(paramValue))
                logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
            }
        } else if(bus == "canivore") {
            cancoderFDService.active.forEach {
                config(it, Rotations.of(paramValue))
                logger.info { "set ${cancoderFDService.active.size} CANcoder's ${param.name}: $paramValue" }
            }
        } else throw IllegalArgumentException()
    }


    private fun configIntParameter(default: Int, config: (CANcoder, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        if(bus == "rio") {
            cancoderService.active.forEach{
                config(it, paramValue)
                logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
            }
        } else if(bus == "canivore") {
            cancoderFDService.active.forEach {
                config(it, paramValue)
                logger.info { "set ${cancoderFDService.active.size} CANcoder's ${param.name}: $paramValue" }}
        } else throw IllegalArgumentException()
    }

    private fun configBooleanParam(default: Boolean, config: (CANcoder, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        if(bus == "rio") {
            cancoderService.active.forEach {
                config(it, paramValue)
                logger.info { "set ${cancoderService.active.size} CANcoder's ${param.name}: $paramValue" }
            }
        } else if(bus == "canivore") {
            cancoderFDService.active.forEach {
                config(it, paramValue)
                logger.info { "set ${cancoderFDService.active.size} CANcoder's ${param.name}: $paramValue" }}
        } else throw IllegalArgumentException()
    }
}