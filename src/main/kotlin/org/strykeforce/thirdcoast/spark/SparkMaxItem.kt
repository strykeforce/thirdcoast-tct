package org.strykeforce.thirdcoast.spark

import com.revrobotics.CANAnalog
import com.revrobotics.CANDigitalInput
import com.revrobotics.CANSparkMax
import com.revrobotics.SensorType
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.item.Measurable
import java.util.function.DoubleSupplier

class SparkMaxItem(
    private val sparkMax: CANSparkMax,
    override val description: String = "SparkMax ${sparkMax.deviceId}"
): Measurable {
    override val deviceId = sparkMax.deviceId
    override val type = "sparkMax"
    override val measures: Set<Measure>
        get() = MEASURES

    override fun measurementFor(measure: Measure): DoubleSupplier {
        return when (measure){
            Measure.UNKNOWN -> DoubleSupplier { 2767.0 }
            Measure.OUTPUT_CURRENT -> DoubleSupplier { sparkMax.outputCurrent }
            Measure.OUTPUT_PERCENT -> DoubleSupplier { sparkMax.appliedOutput }
            Measure.CLOSED_LOOP_TARGET -> DoubleSupplier { sparkMax.get() }
            Measure.SELECTED_SENSOR_POSITION -> DoubleSupplier { sparkMax.encoder.position }
            Measure.SELECTED_SENSOR_VELOCITY -> DoubleSupplier { sparkMax.encoder.velocity }
            Measure.BUS_VOLTAGE -> DoubleSupplier { sparkMax.busVoltage }
            Measure.INTEGRAL_ACCUMULATOR -> DoubleSupplier { sparkMax.pidController.iAccum }
            Measure.ANALOG_IN -> DoubleSupplier { sparkMax.getAnalog(CANAnalog.AnalogMode.kRelative).position }
            Measure.ANALOG_IN_RAW -> DoubleSupplier { sparkMax.getAnalog(CANAnalog.AnalogMode.kRelative).voltage }
            Measure.ANALOG_IN_VELOCITY -> DoubleSupplier { sparkMax.getAnalog(CANAnalog.AnalogMode.kRelative).velocity }
            Measure.QUAD_POSITION -> DoubleSupplier { sparkMax.getEncoder(SensorType.kEncoder,sparkMax.encoder.cpr).position }
            Measure.QUAD_VELOCITY -> DoubleSupplier { sparkMax.getEncoder(SensorType.kEncoder,sparkMax.encoder.cpr).velocity }
            Measure.FORWARD_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sparkMax.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen).get().toDouble()}
            Measure.REVERSE_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sparkMax.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen).get().toDouble() }
            Measure.VALUE -> DoubleSupplier { sparkMax.motorTemperature }
            Measure.PULSE_WIDTH_POSITION -> DoubleSupplier { sparkMax.getAnalog(CANAnalog.AnalogMode.kAbsolute).position }
            Measure.PULSE_WIDTH_VELOCITY -> DoubleSupplier { sparkMax.getAnalog(CANAnalog.AnalogMode.kAbsolute).velocity }
            Measure.PULSE_WIDTH_RISE_TO_FALL -> DoubleSupplier { sparkMax.getAnalog(CANAnalog.AnalogMode.kAbsolute).voltage }
            else -> TODO("$measure not implemented")
        }
    }

    companion object {
        val MEASURES = setOf(
            Measure.OUTPUT_CURRENT,
            Measure.OUTPUT_PERCENT,
            Measure.CLOSED_LOOP_TARGET,
            Measure.SELECTED_SENSOR_POSITION,
            Measure.SELECTED_SENSOR_VELOCITY,
            Measure.BUS_VOLTAGE,
            Measure.INTEGRAL_ACCUMULATOR,
            Measure.ANALOG_IN,
            Measure.ANALOG_IN_RAW,
            Measure.ANALOG_IN_VELOCITY,
            Measure.QUAD_POSITION,
            Measure.QUAD_VELOCITY,
            Measure.FORWARD_LIMIT_SWITCH_CLOSED,
            Measure.REVERSE_LIMIT_SWITCH_CLOSED,
            Measure.VALUE,
            Measure.PULSE_WIDTH_POSITION,
            Measure.PULSE_WIDTH_VELOCITY,
            Measure.PULSE_WIDTH_RISE_TO_FALL
        )
    }


    fun Boolean.toDouble() = if(this) 1.0 else 0.0
}