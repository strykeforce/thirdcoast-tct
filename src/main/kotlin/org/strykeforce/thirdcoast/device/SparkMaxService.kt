package org.strykeforce.thirdcoast.device

import com.revrobotics.*
import mu.KotlinLogging
import org.strykeforce.thirdcoast.spark.SparkMaxItem
import org.strykeforce.thirdcoast.telemetry.TelemetryService
import kotlin.math.log

private val CONTROL_TYPE_DEFAULT = ControlType.kDutyCycle
private val ACTIVE_SLOT_DEFAULT = 0
private val VOLTAGE_COMPENSATION_ENABLED_DEFAULT = true
private val FEEDBACK_SENSOR_DEFAULT = SensorType.kHallSensor

private val DEFAULT_STALL_LIMIT = 80
private val DEFAULT_FREE_LIMIT = 20
private val DEFAULT_LIMIT_RPM = 10_000
private val DEFAULT_CHOP_CURRENT = 115.0
private val DEFAULT_CHOP_CYCLES = 0

private val logger = KotlinLogging.logger {}

/* Default Values

    * Motor Type: Starts as Brushless
    * Active Slot: 0
    * Control Mode: DutyCycle
 */

class SparkMaxService(private val telemetryService: TelemetryService, factory: (id: Int) -> CANSparkMax) :
    AbstractDeviceService<CANSparkMax>(factory) {

    var controlType = CONTROL_TYPE_DEFAULT
    var activeSlot = ACTIVE_SLOT_DEFAULT
    var voltageCompensation = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
    var smartCurrentLimit : SmartCurrentLimit = SmartCurrentLimit(DEFAULT_STALL_LIMIT, DEFAULT_FREE_LIMIT,
        DEFAULT_LIMIT_RPM)
    var secondaryCurrentLimit: SecondaryCurrentLimit = SecondaryCurrentLimit(DEFAULT_CHOP_CURRENT, DEFAULT_CHOP_CYCLES)
    var feedbackSensor = FEEDBACK_SENSOR_DEFAULT




    data class SmartCurrentLimit(var stallLimit: Int, var freeLimit: Int, var limitRPM: Int)
    data class SecondaryCurrentLimit(var chopCurrent: Double, var chopCycles: Int)


    override fun activate(ids: Collection<Int>): Set<Int> {
        val new = super.activate(ids)
        telemetryService.stop()
        active.filter { new.contains(it.deviceId) }.forEach {

            it.getEncoder(SensorType.kHallSensor,it.encoder.cpr)
            it.setSecondaryCurrentLimit(secondaryCurrentLimit.chopCurrent,secondaryCurrentLimit.chopCycles)
            it.setSmartCurrentLimit(smartCurrentLimit.stallLimit,smartCurrentLimit.freeLimit,smartCurrentLimit.limitRPM)
            if (voltageCompensation) it.enableVoltageCompensation(12.0)
            else it.disableVoltageCompensation()
            
            telemetryService.register(SparkMaxItem(it))
        }
        telemetryService.start()
        return new
    }
}