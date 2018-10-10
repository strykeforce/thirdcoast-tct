package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.SlotConfiguration
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.talon.CtreParameter.Enum.*

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
    private val param = CtreParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    private var slot: SlotConfiguration = activeSlot()

    override val menu: String
        get() {
            if (reset) {
                slot = activeSlot()
                logger.info { "reset active slot to: ${slot.toString("active")}" }
                reset = false
            }

            return when (param.enum) {
                SLOT_P -> formatMenu(slot.kP)
                SLOT_I -> formatMenu(slot.kI)
                SLOT_D -> formatMenu(slot.kD)
                SLOT_F -> formatMenu(slot.kF)
                SLOT_I_ZONE -> formatMenu(slot.integralZone)
                SLOT_ALLOWABLE_ERR -> formatMenu(slot.allowableClosedloopError)
                SLOT_MAX_I_ACCUM -> formatMenu(slot.maxIntegralAccumulator)
                SLOT_PEAK_OUTPUT -> formatMenu(slot.closedLoopPeakOutput)
                else -> throw java.lang.IllegalStateException(param.enum.name)
            }
        }

    override fun execute(): Command {
        val slotIndex = talonService.activeSlotIndex
        slot = activeSlot()

        when (param.enum) {
            SLOT_P -> configDoubleParam(slot.kP) { talon, value ->
                talon.config_kP(slotIndex, value, timeout)
                slot.kP = value
            }
            SLOT_I -> configDoubleParam(slot.kI) { talon, value ->
                talon.config_kI(slotIndex, value, timeout)
                slot.kI = value
            }
            SLOT_D -> configDoubleParam(slot.kD) { talon, value ->
                talon.config_kD(slotIndex, value, timeout)
                slot.kD = value
            }
            SLOT_F -> configDoubleParam(slot.kF) { talon, value ->
                talon.config_kF(slotIndex, value, timeout)
                slot.kF = value
            }
            SLOT_I_ZONE -> configIntParam(slot.integralZone) { talon, value ->
                talon.config_IntegralZone(slotIndex, value, timeout)
                slot.integralZone = value
            }
            SLOT_ALLOWABLE_ERR -> configIntParam(slot.allowableClosedloopError) { talon, value ->
                talon.configAllowableClosedloopError(slotIndex, value, timeout)
                slot.allowableClosedloopError = value
            }
            SLOT_MAX_I_ACCUM -> configDoubleParam(slot.maxIntegralAccumulator) { talon, value ->
                talon.configMaxIntegralAccumulator(slotIndex, value, timeout)
                slot.maxIntegralAccumulator = value
            }
            SLOT_PEAK_OUTPUT -> configDoubleParam(slot.closedLoopPeakOutput) { talon, value ->
                talon.configClosedLoopPeakOutput(slotIndex, value, timeout)
                slot.closedLoopPeakOutput = value
            }
            else -> throw java.lang.IllegalStateException(param.enum.name)
        }
        return super.execute()
    }

    private fun activeSlot(): SlotConfiguration {
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

