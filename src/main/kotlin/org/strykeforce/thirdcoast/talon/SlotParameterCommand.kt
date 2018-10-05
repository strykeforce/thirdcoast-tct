package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.ParamEnum.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command

private val logger = KotlinLogging.logger {}

class SlotParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val timeout = talonService.timeout
    val param = CtreParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    override val menu = param.name

    override fun execute(): Command {
        val config = talonService.activeConfiguration
        val slotIndex = talonService.activeSlotIndex
        val slot = when (slotIndex) {
            0 -> config.slot_0
            1 -> config.slot_1
            2 -> config.slot_2
            3 -> config.slot_3
            else -> throw IllegalStateException("no such slot: $slotIndex")
        }

        when (param.enum) {
            eProfileParamSlot_P -> configDoubleParam(slot.kP) { talon, value ->
                talon.config_kP(slotIndex, value, timeout)
            }
            eProfileParamSlot_I -> configDoubleParam(slot.kI) { talon, value ->
                talon.config_kI(slotIndex, value, timeout)
            }
            eProfileParamSlot_D -> configDoubleParam(slot.kD) { talon, value ->
                talon.config_kD(slotIndex, value, timeout)
            }
            eProfileParamSlot_F -> configDoubleParam(slot.kF) { talon, value ->
                talon.config_kF(slotIndex, value, timeout)
            }
            eProfileParamSlot_IZone -> configIntParam(slot.integralZone) { talon, value ->
                talon.config_IntegralZone(slotIndex, value, timeout)
            }
            eProfileParamSlot_AllowableErr -> configIntParam(slot.allowableClosedloopError) { talon, value ->
                talon.configAllowableClosedloopError(slotIndex, value, timeout)
            }
            eProfileParamSlot_MaxIAccum -> configDoubleParam(slot.maxIntegralAccumulator) { talon, value ->
                talon.configMaxIntegralAccumulator(slotIndex, value, timeout)
            }
            eProfileParamSlot_PeakOutput -> configDoubleParam(slot.closedLoopPeakOutput) { talon, value ->
                talon.configClosedLoopPeakOutput(slotIndex, value, timeout)
            }
            else -> throw java.lang.IllegalStateException(param.enum.name)
        }
        return super.execute()
    }

    private fun configIntParam(default: Int, config: (TalonSRX, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

    private fun configDoubleParam(default: Double, config: (TalonSRX, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        talonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
    }

}

