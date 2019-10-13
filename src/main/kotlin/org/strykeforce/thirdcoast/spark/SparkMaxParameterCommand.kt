package org.strykeforce.thirdcoast.spark

import com.revrobotics.CANSparkMax
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.device.SparkMaxService

private val logger = KotlinLogging.logger {}

class SparkMaxParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val sparkMaxService: SparkMaxService by inject()
    private val timeout = sparkMaxService.timeout
    private val param = SparkMaxParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            var slot = sparkMaxService.activeSlot
            var sensor = sparkMaxService.feedbackSensor
            return when (param.enum){
                SparkMaxParameter.Enum.SLOT_P -> formatMenu(slot.p, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_I -> formatMenu(slot.i, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_D -> formatMenu(slot.d, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_D_FILTER -> formatMenu(slot.dFilter, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_FF -> formatMenu(slot.ff, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_I_MAX_ACCUM -> formatMenu(slot.maxIAccum, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_I_ZONE -> formatMenu(slot.iZone, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_OUTPUT_MAX -> formatMenu(slot.maxOut, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_OUTPUT_MIN -> formatMenu(slot.minOut, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_MOTION_MAX_ACCEL -> formatMenu(slot.motionMaxAccel, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_MOTION_MAX_VEL -> formatMenu(slot.motionMaxVel, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_MOTION_MIN_VEL -> formatMenu(slot.motionMinVel, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SLOT_MOTION_ALLOWED_ERROR -> formatMenu(slot.motionAllowError, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.VOLTAGE_COMP_ENABLE -> formatMenu(sparkMaxService.voltageCompEnabled)
                SparkMaxParameter.Enum.VOLTAGE_COMP_VOLTAGE -> formatMenu(sparkMaxService.voltageComp, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.OPEN_LOOP_RAMP -> formatMenu(sparkMaxService.openLoopRamp, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.CLOSED_LOOP_RAMP -> formatMenu(sparkMaxService.closedLoopRamp, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.INVERTED -> formatMenu(sparkMaxService.outputInverted)
                SparkMaxParameter.Enum.SECONDARY_CURRENT_LIMIT -> formatMenu(sparkMaxService.secondaryCurrentLimit.chopCurrent, DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SECONDARY_CURRENT_LIMIT_CHOP_CYCLES -> formatMenu(sparkMaxService.secondaryCurrentLimit.chopCycles)
                SparkMaxParameter.Enum.SMART_CURRENT_FREE_LIMIT -> formatMenu(sparkMaxService.smartCurrentLimit.freeLimit)
                SparkMaxParameter.Enum.SMART_CURRENT_STALL_LIMIT -> formatMenu(sparkMaxService.smartCurrentLimit.stallLimit)
                SparkMaxParameter.Enum.SMART_CURRENT_RPM_LIMIT -> formatMenu(sparkMaxService.smartCurrentLimit.limitRPM)
                SparkMaxParameter.Enum.POSITION_CONV_FACTOR -> formatMenu(sensor.positionConversionFactor,
                    DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.VELOCITY_CONV_FACTOR -> formatMenu(sensor.velocityConversionFactor,
                    DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.MEASUREMENT_PERIOD -> formatMenu(sensor.measurementPeriod)
                SparkMaxParameter.Enum.SENSOR_INVERTED -> formatMenu(sensor.sensorInverted)
                SparkMaxParameter.Enum.SENSOR_MIN -> formatMenu(sensor.sensorMin.toDouble(), DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SENSOR_MAX -> formatMenu(sensor.sensorMax.toDouble(), DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.SENSOR_AVG_DEPTH -> formatMenu(sensor.avgDepth)
                SparkMaxParameter.Enum.SENSOR_CPR -> formatMenu(sensor.cpr)
                SparkMaxParameter.Enum.FWD_HARD_LIMIT_EN -> formatMenu(sparkMaxService.fwdLimitSwitchEnable)
                SparkMaxParameter.Enum.REV_HARD_LIMIT_EN -> formatMenu(sparkMaxService.revLimitSwitchEnable)
                SparkMaxParameter.Enum.FWD_SOFT_LIMIT_EN -> formatMenu(sparkMaxService.fwdSoftLimitEnable)
                SparkMaxParameter.Enum.FWD_SOFT_LIMIT -> formatMenu(sparkMaxService.fwdSoftLimit.toDouble(), DOUBLE_FORMAT_4)
                SparkMaxParameter.Enum.REV_SOFT_LIMIT_EN -> formatMenu(sparkMaxService.revSoftLimitEnable)
                SparkMaxParameter.Enum.REV_SOFT_LIMIT -> formatMenu(sparkMaxService.revSoftLimit.toDouble(), DOUBLE_FORMAT_4)
                else -> TODO( "${param.enum} not implemented")

            }



        }

    override fun execute(): Command {
        val slot = sparkMaxService.activeSlot
        val smartCurrentLimit = sparkMaxService.smartCurrentLimit
        val secondaryCurrentLimit = sparkMaxService.secondaryCurrentLimit
        val sensor = sparkMaxService.feedbackSensor
        when(param.enum){
            SparkMaxParameter.Enum.SLOT_P -> configDoubleParam(slot.p) {
                    sparkMax, value -> sparkMax.pidController.setP(value,slot.id)
                    slot.p = value
            }
            SparkMaxParameter.Enum.SLOT_I -> configDoubleParam(slot.i){
                sparkMax, value -> sparkMax.pidController.setI(value, slot.id)
                slot.i = value
            }
            SparkMaxParameter.Enum.SLOT_D -> configDoubleParam(slot.d){
                sparkMax, value -> sparkMax.pidController.setD(value, slot.id)
                slot.d = value
            }
            SparkMaxParameter.Enum.SLOT_FF -> configDoubleParam(slot.ff){
                sparkMax, value -> sparkMax.pidController.setFF(value, slot.id)
                slot.ff = value
            }
            SparkMaxParameter.Enum.SLOT_I_ZONE -> configDoubleParam(slot.iZone){
                sparkMax, value -> sparkMax.pidController.setIZone(value, slot.id)
                slot.iZone = value
            }
            SparkMaxParameter.Enum.SLOT_I_MAX_ACCUM -> configDoubleParam(slot.maxIAccum){
                sparkMax, value -> sparkMax.pidController.setIMaxAccum(value, slot.id)
                slot.maxIAccum = value
            }
            SparkMaxParameter.Enum.SLOT_D_FILTER -> configDoubleParam(slot.dFilter){
                sparkMax, value -> sparkMax.pidController.setDFilter(value, slot.id)
                slot.dFilter = value
            }
            SparkMaxParameter.Enum.SLOT_MOTION_MAX_ACCEL -> configDoubleParam(slot.motionMaxAccel){
                sparkMax, value -> sparkMax.pidController.setSmartMotionMaxAccel(value, slot.id)
                slot.motionMaxAccel = value
            }
            SparkMaxParameter.Enum.SLOT_MOTION_MAX_VEL -> configDoubleParam(slot.motionMaxVel){
                sparkMax, value -> sparkMax.pidController.setSmartMotionMaxVelocity(value, slot.id)
                slot.motionMaxVel = value
            }
            SparkMaxParameter.Enum.SLOT_MOTION_MIN_VEL -> configDoubleParam(slot.motionMinVel){
                sparkMax, value -> sparkMax.pidController.setSmartMotionMinOutputVelocity(value, slot.id)
                slot.motionMinVel = value
            }
            SparkMaxParameter.Enum.SLOT_MOTION_ALLOWED_ERROR -> configDoubleParam(slot.motionAllowError){
                sparkMax, value -> sparkMax.pidController.setSmartMotionAllowedClosedLoopError(value, slot.id)
                slot.motionAllowError = value
            }
            SparkMaxParameter.Enum.SLOT_OUTPUT_MIN -> configDoubleParam(slot.minOut){
                sparkMax, value -> sparkMax.pidController.setOutputRange(value, slot.maxOut, slot.id)
                slot.minOut = value
            }
            SparkMaxParameter.Enum.SLOT_OUTPUT_MAX -> configDoubleParam(slot.maxOut){
                sparkMax, value -> sparkMax.pidController.setOutputRange(slot.minOut, value, slot.id)
                slot.maxOut = value
            }
            SparkMaxParameter.Enum.VOLTAGE_COMP_ENABLE -> configBooleanParam(sparkMaxService.voltageCompEnabled){
                sparkMax, value ->
                if(value) sparkMax.enableVoltageCompensation(sparkMaxService.voltageComp)
                else sparkMax.disableVoltageCompensation()
                sparkMaxService.voltageCompEnabled = value
            }
            SparkMaxParameter.Enum.VOLTAGE_COMP_VOLTAGE -> configDoubleParam(sparkMaxService.voltageComp){
                sparkMax, value -> sparkMax.enableVoltageCompensation(value)
                sparkMaxService.voltageComp = value
            }
            SparkMaxParameter.Enum.OPEN_LOOP_RAMP -> configDoubleParam(sparkMaxService.openLoopRamp){
                sparkMax, value -> sparkMax.setOpenLoopRampRate(value)
                sparkMaxService.openLoopRamp = value
            }
            SparkMaxParameter.Enum.CLOSED_LOOP_RAMP -> configDoubleParam(sparkMaxService.closedLoopRamp){
                sparkMax, value -> sparkMax.setClosedLoopRampRate(value)
                sparkMaxService.closedLoopRamp = value
            }
            SparkMaxParameter.Enum.INVERTED -> configBooleanParam(sparkMaxService.outputInverted){
                sparkMax, value -> sparkMax.setInverted(value)
                sparkMaxService.outputInverted = value
            }
            SparkMaxParameter.Enum.SECONDARY_CURRENT_LIMIT -> configDoubleParam(secondaryCurrentLimit.chopCurrent){
                sparkMax, value -> sparkMax.setSecondaryCurrentLimit(value,secondaryCurrentLimit.chopCycles)
                sparkMaxService.secondaryCurrentLimit.chopCurrent = value
            }
            SparkMaxParameter.Enum.SECONDARY_CURRENT_LIMIT_CHOP_CYCLES -> configIntParam(secondaryCurrentLimit.chopCycles){
                sparkMax, value -> sparkMax.setSecondaryCurrentLimit(secondaryCurrentLimit.chopCurrent,value)
                sparkMaxService.secondaryCurrentLimit.chopCycles = value
            }
            SparkMaxParameter.Enum.SMART_CURRENT_STALL_LIMIT -> configIntParam(smartCurrentLimit.stallLimit){
                sparkMax, value -> sparkMax.setSmartCurrentLimit(value, smartCurrentLimit.freeLimit, smartCurrentLimit.limitRPM)
                sparkMaxService.smartCurrentLimit.stallLimit = value
            }
            SparkMaxParameter.Enum.SMART_CURRENT_FREE_LIMIT -> configIntParam(smartCurrentLimit.freeLimit){
                sparkMax, value -> sparkMax.setSmartCurrentLimit(smartCurrentLimit.stallLimit, value, smartCurrentLimit.limitRPM)
                sparkMaxService.smartCurrentLimit.freeLimit = value
            }
            SparkMaxParameter.Enum.SMART_CURRENT_RPM_LIMIT -> configIntParam(smartCurrentLimit.limitRPM){
                sparkMax, value -> sparkMax.setSmartCurrentLimit(smartCurrentLimit.stallLimit, smartCurrentLimit.freeLimit, value)
                sparkMaxService.smartCurrentLimit.limitRPM = value
            }
            SparkMaxParameter.Enum.POSITION_CONV_FACTOR -> configDoubleParam(sensor.positionConversionFactor){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType,sensor.cpr).setPositionConversionFactor(value)
                sparkMaxService.feedbackSensor.positionConversionFactor = value
            }
            SparkMaxParameter.Enum.VELOCITY_CONV_FACTOR -> configDoubleParam(sensor.velocityConversionFactor){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, sensor.cpr).setVelocityConversionFactor(value)
                sparkMaxService.feedbackSensor.velocityConversionFactor = value
            }
            SparkMaxParameter.Enum.MEASUREMENT_PERIOD -> configIntParam(sensor.measurementPeriod){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, sensor.cpr).setMeasurementPeriod(value)
                sparkMaxService.feedbackSensor.measurementPeriod = value
            }
            SparkMaxParameter.Enum.SENSOR_INVERTED -> configBooleanParam(sensor.sensorInverted){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, sensor.cpr).setInverted(value)
                sparkMaxService.feedbackSensor.sensorInverted = value
            }
            SparkMaxParameter.Enum.SENSOR_MIN -> configDoubleParam(sensor.sensorMin.toDouble()){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, sensor.cpr).setFeedbackSensorRange(value.toFloat(),sensor.sensorMax)
                sparkMaxService.feedbackSensor.sensorMin = value.toFloat()
            }
            SparkMaxParameter.Enum.SENSOR_MAX -> configDoubleParam(sensor.sensorMax.toDouble()){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, sensor.cpr).setFeedbackSensorRange(sensor.sensorMin, value.toFloat())
                sparkMaxService.feedbackSensor.sensorMax = value.toFloat()
            }
            SparkMaxParameter.Enum.SENSOR_AVG_DEPTH -> configIntParam(sensor.avgDepth){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, sensor.cpr).setAverageDepth(value)
                sparkMaxService.feedbackSensor.avgDepth = value
            }
            SparkMaxParameter.Enum.SENSOR_CPR -> configIntParam(sensor.cpr){
                sparkMax, value -> sparkMax.getEncoder(sensor.sensorType, value)
                sparkMaxService.feedbackSensor.cpr = value
            }
            SparkMaxParameter.Enum.FWD_HARD_LIMIT_EN -> configBooleanParam(sparkMaxService.fwdLimitSwitchEnable){
                sparkMax, value -> sparkMax.getForwardLimitSwitch(sparkMaxService.fwdLimitNormal).enableLimitSwitch(value)
                sparkMaxService.fwdLimitSwitchEnable = value
            }
            SparkMaxParameter.Enum.REV_HARD_LIMIT_EN -> configBooleanParam(sparkMaxService.revLimitSwitchEnable){
                sparkMax, value -> sparkMax.getReverseLimitSwitch(sparkMaxService.revLimitNormal).enableLimitSwitch(value)
                sparkMaxService.revLimitSwitchEnable = value
            }
            SparkMaxParameter.Enum.FWD_SOFT_LIMIT_EN -> configBooleanParam(sparkMaxService.fwdSoftLimitEnable){
                sparkMax, value -> sparkMax.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward,value)
                sparkMaxService.fwdSoftLimitEnable = value
            }
            SparkMaxParameter.Enum.FWD_SOFT_LIMIT -> configDoubleParam(sparkMaxService.fwdSoftLimit.toDouble()){
                sparkMax, value -> sparkMax.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward,value.toFloat())
                sparkMaxService.fwdSoftLimit = value.toFloat()
            }
            SparkMaxParameter.Enum.REV_SOFT_LIMIT_EN -> configBooleanParam(sparkMaxService.revSoftLimitEnable){
                sparkMax, value -> sparkMax.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, value)
                sparkMaxService.revSoftLimitEnable = value
            }
            SparkMaxParameter.Enum.REV_SOFT_LIMIT -> configDoubleParam(sparkMaxService.revSoftLimit.toDouble()){
                sparkMax, value -> sparkMax.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse,value.toFloat())
                sparkMaxService.revSoftLimit = value.toFloat()
            }
            else -> TODO("${param.enum} not implemented")
        }
        return super.execute()
    }

    private fun configDoubleParam(default: Double, config: (CANSparkMax, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        sparkMaxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${sparkMaxService.active.size} Spark Max ${param.name}: $paramValue" }
    }

    private fun configBooleanParam(default: Boolean, config: (CANSparkMax, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        sparkMaxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${sparkMaxService.active.size} Spark Max ${param.name}: $paramValue" }
    }

    private fun configIntParam(default: Int, config: (CANSparkMax, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        sparkMaxService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${sparkMaxService.active.size} Spark max ${param.name}: $paramValue" }
    }




}