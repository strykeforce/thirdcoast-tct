package org.strykeforce.thirdcoast.spark

import com.revrobotics.*
import org.strykeforce.thirdcoast.telemetry.item.Measurable
import org.strykeforce.thirdcoast.telemetry.item.Measure
import java.util.function.DoubleSupplier

internal const val OUTPUT_CURRENT = "OUTPUT_CURRENT"
internal const val OUTPUT_PERCENT = "OUTPUT_PERCENT"
internal const val HALL_SENSOR_POSITION = "HALL_SENSOR_POSITION"
internal const val HALL_SENSOR_VELOCITY = "HALL_SENSOR_VELOCITY"
internal const val ENCODER_POSITION = "ENCODER_POSITION"
internal const val ENCODER_VELOCITY = "ENCODER_VELOCITY"
internal const val BUS_VOLTAGE = "BUS_VOLTAGE"
internal const val INTEGRAL_ACCUMULATOR = "INTEGRAL_ACCUMULATOR"
internal const val ANALOG_REL_VOLTAGE = "ANALOG_REL_VOLTAGE"
internal const val ANALOG_REL_POSITION = "ANALOG_REL_POSITION"
internal const val ANALOG_REL_VELOCITY = "ANALOG_REL_VELOCITY"
internal const val ANALOG_ABS_VOLTAGE = "ANALOG_ABS_VOLTAGE"
internal const val ANALOG_ABS_POSITION = "ANALOG_ABS_POSITION"
internal const val ANALOG_ABS_VELOCITY = "ANALOG_ABS_VELOCITY"
internal const val FWD_LIMIT_SWITCH_CLOSED = "FWD_LIMIT_SWITCH_CLOSED"
internal const val REV_LIMIT_SWITCH_CLOSED = "REV_LIMIT_SWITCH_CLOSED"
internal const val MOTOR_TEMP = "MOTOR_TEMP"


class SparkMaxItem(
    private val sparkMax: CANSparkMax,
    override val description: String = "SparkMax ${sparkMax.deviceId}"
): Measurable {
    override val deviceId = sparkMax.deviceId
    override val type = "sparkMax"
    override val measures = setOf(
        Measure(OUTPUT_CURRENT, "Output Current"),
        Measure(OUTPUT_PERCENT, "Output Percent"),
        Measure(HALL_SENSOR_POSITION, "Hall Sensor Position"),
        Measure(HALL_SENSOR_VELOCITY, "Hall Sensor Velocity"),
        Measure(BUS_VOLTAGE, "Bus Voltage"),
        Measure(INTEGRAL_ACCUMULATOR, "Integral Accumulator"),
        Measure(FWD_LIMIT_SWITCH_CLOSED, "Forward Limit Switch Closed"),
        Measure(REV_LIMIT_SWITCH_CLOSED, "Reverse Limit Switch Closed"),
        Measure(MOTOR_TEMP, "Motor Temperature")
    )

    private val hallSensor : CANEncoder by lazy { sparkMax.getEncoder(SensorType.kHallSensor, sparkMax.encoder.cpr) }

    override fun measurementFor(measure: Measure): DoubleSupplier {
        return when (measure.name){
            OUTPUT_CURRENT -> DoubleSupplier { sparkMax.outputCurrent }
            OUTPUT_PERCENT -> DoubleSupplier { sparkMax.appliedOutput }
            HALL_SENSOR_POSITION -> DoubleSupplier { hallSensor.position }
            HALL_SENSOR_VELOCITY -> DoubleSupplier { hallSensor.velocity }
            BUS_VOLTAGE -> DoubleSupplier { sparkMax.busVoltage }
            INTEGRAL_ACCUMULATOR -> DoubleSupplier { sparkMax.pidController.iAccum }
            FWD_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sparkMax.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen).get().toDouble()}
            REV_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sparkMax.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen).get().toDouble() }
            MOTOR_TEMP -> DoubleSupplier { sparkMax.motorTemperature }
            else -> DoubleSupplier { 2767.0 }
        }
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as SparkMaxItem

        if(deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId


    fun Boolean.toDouble() = if(this) 1.0 else 0.0
}