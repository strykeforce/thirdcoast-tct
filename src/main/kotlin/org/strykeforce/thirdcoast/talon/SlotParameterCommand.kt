package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.ParamEnum.*
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration
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

    companion object {
        var reset = true
    }

    private val timeout = talonService.timeout
    val param = CtreParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    private var slot: SlotConfiguration = updateSlot(talonService.activeSlotIndex)

    override val menu: String
        get() {
            if (reset) {
                slot = updateSlot(talonService.activeSlotIndex)
                logger.info { "first time: ${slot.toString("active")}" }
                reset = false
            }
            val value = when (param.enum) {
                eProfileParamSlot_P -> slot.kP.toString()
                eProfileParamSlot_I -> slot.kI.toString()
                eProfileParamSlot_D -> slot.kD.toString()
                eProfileParamSlot_F -> slot.kF.toString()
                eProfileParamSlot_IZone -> slot.integralZone.toString()
                eProfileParamSlot_AllowableErr -> slot.allowableClosedloopError.toString()
                eProfileParamSlot_MaxIAccum -> slot.maxIntegralAccumulator.toString()
                eProfileParamSlot_PeakOutput -> slot.closedLoopPeakOutput.toString()
                else -> throw java.lang.IllegalStateException(param.enum.name)
            }

            return formatMenu(value)
        }

    override fun execute(): Command {
        val slotIndex = talonService.activeSlotIndex
        slot = updateSlot(slotIndex)

        when (param.enum) {
            eProfileParamSlot_P -> configDoubleParam(slot.kP) { talon, value ->
                talon.config_kP(slotIndex, value, timeout)
                slot.kP = value
            }
            eProfileParamSlot_I -> configDoubleParam(slot.kI) { talon, value ->
                talon.config_kI(slotIndex, value, timeout)
                slot.kI = value
            }
            eProfileParamSlot_D -> configDoubleParam(slot.kD) { talon, value ->
                talon.config_kD(slotIndex, value, timeout)
                slot.kD = value
            }
            eProfileParamSlot_F -> configDoubleParam(slot.kF) { talon, value ->
                talon.config_kF(slotIndex, value, timeout)
                slot.kF = value
            }
            eProfileParamSlot_IZone -> configIntParam(slot.integralZone) { talon, value ->
                talon.config_IntegralZone(slotIndex, value, timeout)
                slot.integralZone = value
            }
            eProfileParamSlot_AllowableErr -> configIntParam(slot.allowableClosedloopError) { talon, value ->
                talon.configAllowableClosedloopError(slotIndex, value, timeout)
                slot.allowableClosedloopError = value
            }
            eProfileParamSlot_MaxIAccum -> configDoubleParam(slot.maxIntegralAccumulator) { talon, value ->
                talon.configMaxIntegralAccumulator(slotIndex, value, timeout)
                slot.maxIntegralAccumulator = value
            }
            eProfileParamSlot_PeakOutput -> configDoubleParam(slot.closedLoopPeakOutput) { talon, value ->
                talon.configClosedLoopPeakOutput(slotIndex, value, timeout)
                slot.closedLoopPeakOutput = value
            }
            else -> throw java.lang.IllegalStateException(param.enum.name)
        }
        return super.execute()
    }

    private fun updateSlot(index: Int): SlotConfiguration {
        val config = talonService.activeConfiguration
        val slotIndex = talonService.activeSlotIndex
        return when (slotIndex) {
            0 -> config.slot_0
            1 -> config.slot_1
            2 -> config.slot_2
            3 -> config.slot_3
            else -> throw IllegalStateException("no such slot: $slotIndex")
        }
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

