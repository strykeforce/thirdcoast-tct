package org.strykeforce.thirdcoast.device

import com.revrobotics.*
import mu.KotlinLogging
import org.strykeforce.thirdcoast.spark.SparkMaxItem
import org.strykeforce.thirdcoast.telemetry.TelemetryService

private val CONTROL_TYPE_DEFAULT = ControlType.kDutyCycle
private val VOLTAGE_COMPENSATION_ENABLED_DEFAULT = true
private val FEEDBACK_SENSOR_DEFAULT = SensorType.kHallSensor
private val OUTPUT_INVERTED_DEFAULT = false
private val DEFAULT_IDLE_MODE = CANSparkMax.IdleMode.kCoast
private val FWD_LIMIT_SWITCH_NORMAL = CANDigitalInput.LimitSwitchPolarity.kNormallyOpen
private val REV_LIMIT_SWITCH_NORMAL = CANDigitalInput.LimitSwitchPolarity.kNormallyOpen
private val FWD_LIMIT_SWITCH_EN = true
private val REV_LIMIT_SWITCH_EN = true
private val FWD_SOFT_LIMIT_EN = false
private val REV_SOFT_LIMIT_EN = false
private val FWD_SOFT_LIMIT = 0.0F
private val REV_SOFT_LIMIT = 0.0F

private val OPEN_LOOP_RAMP_DEFAULT = 0.0
private val CLOSED_LOOP_RAMP_DEFAULT = 0.0
private val DEFAULT_STALL_LIMIT = 80
private val DEFAULT_FREE_LIMIT = 20
private val DEFAULT_LIMIT_RPM = 10_000
private val DEFAULT_CHOP_CURRENT = 115.0
private val DEFAULT_CHOP_CYCLES = 0
private val DEFAULT_VOLTAGE_COMP = 12.0
private val DEFAULT_CPR = 64

private val logger = KotlinLogging.logger {}

/* Default Values

    * Motor Type: Starts as Brushless
    * Active Slot: 0
    * Control Mode: DutyCycle
 */

class SparkMaxService(private val telemetryService: TelemetryService, factory: (id: Int) -> CANSparkMax) :
    AbstractDeviceService<CANSparkMax>(factory) {

    val timeout = 10
    var dirty = true

    //Output Settings
    var outputInverted = OUTPUT_INVERTED_DEFAULT
        get() {
            if(!dirty) return field
            return active.firstOrNull()?.inverted ?: false
        }
    var openLoopRamp = OPEN_LOOP_RAMP_DEFAULT
        get() {
            if(!dirty) return field
            return active.firstOrNull()?.openLoopRampRate ?: 0.0
        }
    var closedLoopRamp = CLOSED_LOOP_RAMP_DEFAULT
        get() {
            if(!dirty) return field
            return active.firstOrNull()?.closedLoopRampRate ?: 0.0
        }
    var idleMode = DEFAULT_IDLE_MODE
        get(){
            logger.debug("Idle Mode Dirty: {}", dirty)
            if(!dirty) return field
            return active.firstOrNull()?.idleMode ?: DEFAULT_IDLE_MODE
        }


    //Voltage Comp
    var voltageCompEnabled = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
    var voltageComp = DEFAULT_VOLTAGE_COMP

    //Current Limit
    var smartCurrentLimit : SmartCurrentLimit = SmartCurrentLimit(DEFAULT_STALL_LIMIT, DEFAULT_FREE_LIMIT,
        DEFAULT_LIMIT_RPM)
    var secondaryCurrentLimit: SecondaryCurrentLimit = SecondaryCurrentLimit(DEFAULT_CHOP_CURRENT, DEFAULT_CHOP_CYCLES)

    //Output Feedback
    var controlType = CONTROL_TYPE_DEFAULT
    var feedbackSensor = FeedbackSensor()
        get(){
            if(!dirty) return field
            var encoder = active.firstOrNull()?.getEncoder(field.sensorType, field.cpr)
            field.positionConversionFactor = encoder?.positionConversionFactor ?: field.positionConversionFactor
            field.velocityConversionFactor = encoder?.velocityConversionFactor ?: field.velocityConversionFactor
            field.measurementPeriod = encoder?.measurementPeriod ?: field.measurementPeriod
            field.sensorInverted = encoder?.inverted ?: field.sensorInverted
            field.avgDepth = encoder?.averageDepth ?: field.avgDepth

            return field
        }

    var activeSlot: ActiveSlot = ActiveSlot()
        get() {
            if (!dirty) return field
            var spark = active.firstOrNull()
            field.p = spark?.pidController?.getP(field.id) ?: 0.0
            field.i = spark?.pidController?.getI(field.id) ?: 0.0
            field.d = spark?.pidController?.getD(field.id) ?: 0.0
            field.ff = spark?.pidController?.getFF(field.id) ?: 0.0
            field.iZone = spark?.pidController?.getIZone(field.id) ?: 0.0
            field.maxIAccum = spark?.pidController?.getIMaxAccum(field.id) ?: 0.0
            field.dFilter = spark?.pidController?.getDFilter(field.id) ?: 0.0
            field.motionMaxAccel = spark?.pidController?.getSmartMotionMaxAccel(field.id) ?: 0.0
            field.motionMaxVel = spark?.pidController?.getSmartMotionMaxVelocity(field.id) ?: 0.0
            field.motionMinVel = spark?.pidController?.getSmartMotionMinOutputVelocity(field.id) ?: 0.0
            field.motionAllowError = spark?.pidController?.getSmartMotionAllowedClosedLoopError(field.id) ?: 0.0
            field.maxOut = spark?.pidController?.getOutputMax(field.id) ?: 1.0
            field.minOut = spark?.pidController?.getOutputMin(field.id) ?: -1.0

            return field
        }


    //Limits
    var fwdLimitNormal = FWD_LIMIT_SWITCH_NORMAL
    var fwdLimitSwitchEnable = FWD_LIMIT_SWITCH_EN
    var revLimitNormal = REV_LIMIT_SWITCH_NORMAL
    var revLimitSwitchEnable = REV_LIMIT_SWITCH_EN
    var fwdSoftLimitEnable = FWD_SOFT_LIMIT_EN
    var fwdSoftLimit = FWD_SOFT_LIMIT
    var revSoftLimitEnable = REV_SOFT_LIMIT_EN
    var revSoftLimit = REV_SOFT_LIMIT







    override fun activate(ids: Collection<Int>): Set<Int> {
        dirty = true

        val new = super.activate(ids)
        telemetryService.stop()

        active.filter { new.contains(it.deviceId) }.forEach {
            it.getEncoder(feedbackSensor.sensorType,it.encoder.cpr)
            it.setSecondaryCurrentLimit(secondaryCurrentLimit.chopCurrent,secondaryCurrentLimit.chopCycles)
            it.setSmartCurrentLimit(smartCurrentLimit.stallLimit,smartCurrentLimit.freeLimit,smartCurrentLimit.limitRPM)
            if (voltageCompEnabled) it.enableVoltageCompensation(voltageComp)
            else it.disableVoltageCompensation()

            telemetryService.register(SparkMaxItem(it))
        }
        telemetryService.start()
        return new
    }

    fun updateParams(){
        dirty = true
        activeSlot
        openLoopRamp
        closedLoopRamp
        outputInverted
        idleMode
        feedbackSensor
        dirty = false
    }

    data class SmartCurrentLimit(var stallLimit: Int, var freeLimit: Int, var limitRPM: Int)
    data class SecondaryCurrentLimit(var chopCurrent: Double, var chopCycles: Int)
    data class ActiveSlot(
        var id: Int = 0,
        var p: Double = 0.0,
        var i: Double = 0.0,
        var d: Double = 0.0,
        var dFilter : Double = 0.0,
        var ff: Double = 0.0,
        var maxIAccum: Double = 0.0,
        var iZone: Double = 0.0,
        var minOut: Double = -1.0,
        var maxOut: Double = 1.0,
        var motionMaxAccel: Double = 0.0,
        var motionMaxVel: Double = 0.0,
        var motionMinVel: Double = 0.0,
        var motionAllowError: Double = 0.0)


    data class FeedbackSensor(
        var sensorType: SensorType = FEEDBACK_SENSOR_DEFAULT,
        var positionConversionFactor: Double = 1.0,
        var velocityConversionFactor: Double = 1.0,
        var measurementPeriod: Int = 100,
        var sensorInverted: Boolean = false,
        var sensorMin: Float = 0.0F,
        var sensorMax: Float = 0.0F,
        var avgDepth: Int = 64,
        var cpr: Int = DEFAULT_CPR
    )

}