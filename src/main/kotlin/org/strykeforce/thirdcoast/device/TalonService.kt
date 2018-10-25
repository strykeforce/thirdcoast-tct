package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import mu.KotlinLogging
import org.strykeforce.thirdcoast.talon.ParameterCommand
import org.strykeforce.thirdcoast.talon.SelectFeedbackSensorCommand
import org.strykeforce.thirdcoast.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

private const val ACTIVE_SLOT_DEFAULT = 0
private const val VOLTAGE_COMPENSATION_DEFAULT = true
private const val SENSOR_PHASE_DEFAULT = false
private const val CURRENT_LIMIT_DEFAULT = false
private const val INVERTED_DEFAULT = false

class TalonService(private val telemetryService: TelemetryService, factory: (id: Int) -> TalonSRX) :
    AbstractDeviceService<TalonSRX>(factory) {

    val timeout = 10
    var activeSlotIndex: Int = ACTIVE_SLOT_DEFAULT
    var controlMode = ControlMode.PercentOutput
    var neutralMode = NeutralMode.EEPROMSetting
    var voltageCompensation = VOLTAGE_COMPENSATION_DEFAULT
    var sensorPhase = SENSOR_PHASE_DEFAULT
    var currentLimit = CURRENT_LIMIT_DEFAULT

    var activeConfiguration = TalonSRXConfiguration()
        get() {
            active.firstOrNull()?.getAllConfigs(field) ?: logger.debug("no active talons, returning default config")
            return field
        }

    val outputReverse: Boolean
        get() = active.firstOrNull()?.inverted ?: INVERTED_DEFAULT

    override fun activate(ids: Collection<Int>) {
        ParameterCommand.reset = true
        SelectFeedbackSensorCommand.reset = true
        activeSlotIndex = ACTIVE_SLOT_DEFAULT
        controlMode = ControlMode.PercentOutput
        neutralMode = NeutralMode.EEPROMSetting
        voltageCompensation = VOLTAGE_COMPENSATION_DEFAULT
        sensorPhase = SENSOR_PHASE_DEFAULT
        currentLimit = CURRENT_LIMIT_DEFAULT

        super.activate(ids)
        telemetryService.stop()
        active.forEach {
            it.setNeutralMode(neutralMode)
            it.selectProfileSlot(activeSlotIndex, 0)
            it.enableVoltageCompensation(voltageCompensation)
            it.setSensorPhase(sensorPhase)
            it.inverted = INVERTED_DEFAULT
            it.enableCurrentLimit(currentLimit)
            telemetryService.register(it)
        }
        telemetryService.start()
    }
}
