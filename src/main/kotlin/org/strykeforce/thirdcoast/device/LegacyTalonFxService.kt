package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration
import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration
import mu.KotlinLogging
import org.strykeforce.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

private val CONTROL_MODE_DEFAULT = ControlMode.PercentOutput
private const val ACTIVE_SLOT_DEFAULT = 0
private val NEUTRAL_MODE_DEFAULT = NeutralMode.EEPROMSetting
private const val VOLTAGE_COMPENSATION_ENABLED_DEFAULT = true
private const val CURRENT_LIMIT_ENABLED_DEFAULT = false
private const val SENSOR_PHASE_INVERTED_DEFAULT = false
private const val OUTPUT_INVERTED_DEFAULT = false


class LegacyTalonFxService(
    private val telemetryService: TelemetryService, factory: (id: Int) -> TalonFX
) : AbstractDeviceService<TalonFX>(factory) {

    val timeout = 10
    var dirty = true
    var neutralMode = NEUTRAL_MODE_DEFAULT
    var controlMode = CONTROL_MODE_DEFAULT
    var voltageCompensation = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
    var sensorPhase = SENSOR_PHASE_INVERTED_DEFAULT
    var activeSlotIndex: Int = ACTIVE_SLOT_DEFAULT

    var activeConfiguration = TalonFXConfiguration()
        get() {
            if (!dirty) return field
            active.firstOrNull()?.getAllConfigs(field, timeout)
                ?: logger.debug("no active talon fx's, returning default config")
            dirty = false
            return field
        }

    val activeSlot: SlotConfiguration
        get() = when (activeSlotIndex) {
            0 -> activeConfiguration.slot0
            1 -> activeConfiguration.slot1
            2 -> activeConfiguration.slot2
            3 -> activeConfiguration.slot3
            else -> throw IllegalStateException("invalid slot: $activeSlotIndex")
        }

    val outputInverted: Boolean
        get() = active.firstOrNull()?.inverted ?: OUTPUT_INVERTED_DEFAULT

    override fun activate(ids: Collection<Int>): Set<Int> {
        dirty = true

        val new = super.activate(ids)
        telemetryService.stop()
        active.filter { new.contains(it.deviceID) }.forEach {
            it.setNeutralMode(neutralMode)
            it.selectProfileSlot(activeSlotIndex, 0)
            it.enableVoltageCompensation(voltageCompensation)
            it.setSensorPhase(sensorPhase)
            it.inverted = OUTPUT_INVERTED_DEFAULT
            telemetryService.register(it)
        }
        telemetryService.start()
        return new
    }
}