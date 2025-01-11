package org.strykeforce.thirdcoast.canifier

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifierConfiguration
import com.ctre.phoenix.CANifierStatusFrame
import com.ctre.phoenix.CANifierStatusFrame.*
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.canifier.CanifierParameter.Enum.*
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CanifierService

private val logger = KotlinLogging.logger {}

class CanifierParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    companion object {
        var reset = true
        private var slot: SlotConfiguration = SlotConfiguration()
        private var config = CANifierConfiguration()
    }

    private val canifierService: CanifierService by inject()
    private val timeout = canifierService.timeout
    private val param = CanifierParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            return when (param.enum) {
                QUAD_POSITION -> formatMenu(canifierService.active.firstOrNull()?.quadraturePosition ?: 0)
                STATUS_GENERAL1 -> formatMenu(defaultFor(Status_1_General))
                STATUS_GENERAL2 -> formatMenu(defaultFor(Status_2_General))
                STATUS_PWM_INPUTS0 -> formatMenu(defaultFor(Status_3_PwmInputs0))
                STATUS_PWM_INPUTS1 -> formatMenu(defaultFor(Status_4_PwmInputs1))
                STATUS_PWM_INPUTS2 -> formatMenu(defaultFor(Status_5_PwmInputs2))
                STATUS_PWM_INPUTS3 -> formatMenu(defaultFor(Status_6_PwmInputs3))
                FACTORY_DEFAULTS -> tomlMenu
            }
        }

    override fun execute(): Command {
        when (param.enum) {
            QUAD_POSITION -> configIntParam(0) { talon, value ->
                talon.setQuadraturePosition(value, timeout)
            }
            STATUS_GENERAL1 -> configIntParam(defaultFor(Status_1_General)) { talon, value ->
                talon.setStatusFramePeriod(Status_1_General, value, timeout)
            }
            STATUS_GENERAL2 -> configIntParam(defaultFor(Status_2_General)) { talon, value ->
                talon.setStatusFramePeriod(Status_2_General, value, timeout)
            }
            STATUS_PWM_INPUTS0 -> configIntParam(defaultFor(Status_3_PwmInputs0)) { talon, value ->
                talon.setStatusFramePeriod(Status_3_PwmInputs0, value, timeout)
            }
            STATUS_PWM_INPUTS1 -> configIntParam(defaultFor(Status_4_PwmInputs1)) { talon, value ->
                talon.setStatusFramePeriod(Status_4_PwmInputs1, value, timeout)
            }
            STATUS_PWM_INPUTS2 -> configIntParam(defaultFor(Status_5_PwmInputs2)) { talon, value ->
                talon.setStatusFramePeriod(Status_5_PwmInputs2, value, timeout)
            }
            STATUS_PWM_INPUTS3 -> configIntParam(defaultFor(Status_6_PwmInputs3)) { talon, value ->
                talon.setStatusFramePeriod(Status_6_PwmInputs3, value, timeout)
            }
            FACTORY_DEFAULTS -> configBooleanParam(false) { talon, value ->
                if (value) talon.configFactoryDefault(timeout)
            }

        }
        return super.execute()
    }

    private fun configBooleanParam(default: Boolean, config: (CANifier, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        canifierService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${canifierService.active.size} talon ${param.name}: $paramValue" }
    }


    private fun configIntParam(default: Int, config: (CANifier, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        canifierService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${canifierService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleParam(default: Double, config: (CANifier, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        canifierService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${canifierService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun defaultFor(frame: CANifierStatusFrame): Int =
        canifierService.active.first().getStatusFramePeriod(frame)

}