package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import mu.KotlinLogging
import org.strykeforce.thirdcoast.talon.SelectFeedbackSensorCommand
import org.strykeforce.thirdcoast.talon.TalonParameterCommand
import org.strykeforce.thirdcoast.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

private val CONTROL_MODE_DEFAULT = ControlMode.PercentOutput
private const val ACTIVE_SLOT_DEFAULT = 0
private val NEUTRAL_MODE_DEFAULT = NeutralMode.EEPROMSetting
private const val VOLTAGE_COMPENSATION_ENABLED_DEFAULT = true
private const val CURRENT_LIMIT_ENABLED_DEFAULT = false
private const val SENSOR_PHASE_INVERTED_DEFAULT = false
private const val OUTPUT_INVERTED_DEFAULT = false

/**
 * Holds the active state for all Talons that have been activated by the user. Talons that are currently active
 * can be configured and ran. All Talons that have been made active will be available to the telemetry client (Grapher),
 * for the remainder of the `tct` session, even if not currently active.
 *
 * Persistent configuration settings will be refreshed from the Talon when the `TalonService` is marked as *dirty*. This
 * prevents repeated, time-consuming Phoenix API calls to read Talon state.
 *
 * For non-persistent settings, Talons will be set to a known state when made active. These include:
 * - control mode = PercentOutput
 * - active slot index = 0
 * - brake in neutral mode = EEPROM setting
 * - voltage compensation = enabled
 * - current limit = enabled
 * - sensor phase  = not inverted
 * - output = not inverted
 *
 *
 * @param telemetryService the telemetry service.
 * @param factory a factory lambda expression to instantiate Talons.
 */
class TalonService(private val telemetryService: TelemetryService, factory: (id: Int) -> TalonSRX) :
    AbstractDeviceService<TalonSRX>(factory) {

    val timeout = 10
    var dirty = true
    var controlMode = CONTROL_MODE_DEFAULT
    var activeSlotIndex: Int = ACTIVE_SLOT_DEFAULT
    var neutralMode = NEUTRAL_MODE_DEFAULT
    var voltageCompensation = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
    var sensorPhase = SENSOR_PHASE_INVERTED_DEFAULT
    var currentLimit = CURRENT_LIMIT_ENABLED_DEFAULT

    var activeConfiguration = TalonSRXConfiguration()
        get() {
            if (!dirty) return field
            active.firstOrNull()?.getAllConfigs(field) ?: logger.debug("no active talons, returning default config")
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

    override fun activate(ids: Collection<Int>) {
        dirty = true
        activeSlotIndex = ACTIVE_SLOT_DEFAULT
        controlMode = CONTROL_MODE_DEFAULT
        neutralMode = NEUTRAL_MODE_DEFAULT
        voltageCompensation = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
        sensorPhase = SENSOR_PHASE_INVERTED_DEFAULT
        currentLimit = CURRENT_LIMIT_ENABLED_DEFAULT

        super.activate(ids)
        telemetryService.stop()
        active.forEach {
            it.setNeutralMode(neutralMode)
            it.selectProfileSlot(activeSlotIndex, 0)
            it.enableVoltageCompensation(voltageCompensation)
            it.setSensorPhase(sensorPhase)
            it.inverted = OUTPUT_INVERTED_DEFAULT
            it.enableCurrentLimit(currentLimit)
            telemetryService.register(it)
        }
        telemetryService.start()
    }
}
