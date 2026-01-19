package org.strykeforce.thirdcoast.device

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.controls.DynamicMotionMagicDutyCycle
import com.ctre.phoenix6.controls.StrictFollower
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.ControlModeValue
import com.ctre.phoenix6.signals.NeutralModeValue
import mu.KotlinLogging
import org.strykeforce.controller.CTRE_DifferentialType
import org.strykeforce.controller.CTRE_FollowerType
import org.strykeforce.controller.MotionMagicType
import org.strykeforce.controller.SF_TalonFX
import org.strykeforce.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

private val CONTROL_MODE_DEFAULT = ControlModeValue.DutyCycleOut
private const val ACTIVE_SLOT_DEFAULT = 0
private val NEUTRAL_MODE_DEFAULT = NeutralModeValue.Coast
private const val VOLTAGE_COMPENSATION_ENABLED_DEFAULT = true
private const val CURRENT_LIMIT_ENABLED_DEFAULT = false
private const val SENSOR_PHASE_INVERTED_DEFAULT = false
private const val OUTPUT_INVERTED_DEFAULT = false

enum class Units{
    PERCENT, VOLTAGE, TORQUE_CURRENT
}

enum class MM_Type {
    STANDARD, VELOCITY, DYNAMIC, EXPONENTIAL
}

enum class FollowerType {
    STRICT, STANDARD
}

enum class SetpointType {
    OPEN_LOOP, POSITION, VELOCITY, MOTION_MAGIC, DIFFERENTIAL, FOLLOWER, NEUTRAL, MUSIC
}
enum class DifferentialType {
    OPEN_LOOP, POSITION, VELOCITY, MOTION_MAGIC, FOLLOWER
}


open class TalonFxService(
    private val telemetryService: TelemetryService, factory: (id:Int) -> SF_TalonFX
) : AbstractDeviceService<SF_TalonFX>(factory) {

    val timeout = 10.0
    var dirty = true
    //var neutralMode = NEUTRAL_MODE_DEFAULT
    //var controlMode = CONTROL_MODE_DEFAULT
    // var voltageCompensation = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
    //var sensorPhase  = SENSOR_PHASE_INVERTED_DEFAULT
    var activeSlotIndex: Int = ACTIVE_SLOT_DEFAULT
    var activeUnits: Units = Units.PERCENT
    var active_MM_type: MotionMagicType = MotionMagicType.Standard
    var activeFeedForward: Double = 0.0
    var activeTorqueCurrentDeadband: Double = 0.0
    var activeTorqueCurrentMaxOut: Double = 1.0
    var activeVelocity: Double = 0.0
    var activeAcceleration: Double = 0.0
    var activeJerk: Double = 0.0
    var activeDifferentialTarget: Double = 0.0
    var activeFollowerType: CTRE_FollowerType = CTRE_FollowerType.Strict
    var activeOpposeMain: Boolean = false
    var activeDifferentialSlot: Int = 0
    var activeFOC: Boolean = false
    var setpointType: SetpointType = SetpointType.OPEN_LOOP
    var differentialType: CTRE_DifferentialType = CTRE_DifferentialType.Open_Loop
    var activeNeutralOut: NeutralModeValue = NeutralModeValue.Coast
    var activeOverrideNeutral: Boolean = false
    var limFwdMotion : Boolean = false;
    var limRevMotion : Boolean = false;
    var grapherStatusFrameHz : Double = 0.0;
    var isRioBus : Boolean = true;
    var controlRequestUpdateFreq: Double = 100.0;

    var controlMode: String = "Duty Cycle Out"
        get() {
            when(setpointType){
                SetpointType.OPEN_LOOP -> {
                    when(activeUnits){
                        Units.PERCENT -> return "Duty Cycle Out"
                        Units.VOLTAGE -> return "Voltage Out"
                        Units.TORQUE_CURRENT -> return "(pro) Torque Current FOC"
                    }
                }
                SetpointType.POSITION -> {
                    when(activeUnits){
                        Units.PERCENT -> return "Position: Duty Cycle"
                        Units.VOLTAGE -> return "Position: Voltage"
                        Units.TORQUE_CURRENT -> return "(pro) Position: Torque Current FOC"
                    }
                }
                SetpointType.VELOCITY -> {
                    when(activeUnits) {
                        Units.PERCENT -> return "Velocity: Duty Cycle"
                        Units.VOLTAGE -> return "Velocity: Voltage"
                        Units.TORQUE_CURRENT -> return "(pro) Velocity: Torque Current FOC"
                    }
                }
                SetpointType.MOTION_MAGIC -> {
                    when(active_MM_type) {
                        MotionMagicType.Standard -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Motion Magic: Duty Cycle"
                                Units.VOLTAGE -> return "Motion Magic: Voltage"
                                Units.TORQUE_CURRENT -> return "(pro) Motion Magic: Torque Current FOC"
                            }
                        } MotionMagicType.Velocity -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Motion Magic Velocity: Duty Cycle"
                                Units.VOLTAGE -> return "Motion Magic Velocity: Voltage"
                                Units.TORQUE_CURRENT -> return "(pro) Motion Magic Velocity: Torque Current FOC"
                            }
                        } MotionMagicType.Dynamic -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Dynamic Motion Magic: Duty Cycle"
                                Units.VOLTAGE -> return "Dynamic Motion Magic: Voltage"
                                Units.TORQUE_CURRENT -> return "(pro) Dynamic Motion Magic: Torque Current FOC"
                            }
                        } MotionMagicType.Exponential -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Motion Magic Exponential: Duty Cycle"
                                Units.VOLTAGE -> return "Motion Magic Exponential: Voltage"
                                Units.TORQUE_CURRENT -> return "(pro) Motion Magic Exponential: Torque Current FOC"
                            }
                        } MotionMagicType.DynamicExponential -> {
                        when(activeUnits) {
                            Units.PERCENT -> return "Dynamic Motion Magic Exponential: Duty Cycle"
                            Units.VOLTAGE -> return "Dynamic Motion Magic Exponential: Voltage"
                            Units.TORQUE_CURRENT -> return "Dynamic Motion Magic Exponential: Torque Current FOC"
                        }
                    }
                    }
                }
                SetpointType.DIFFERENTIAL -> {
                    when(differentialType) {
                        CTRE_DifferentialType.Open_Loop -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Differential: Duty Cycle"
                                Units.VOLTAGE -> return "Differential: Voltage"
                                else -> return "INVALID COMBO: No Differenital Torque Current"
                            }
                        } CTRE_DifferentialType.Position -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Differential Position: Duty Cycle"
                                Units.VOLTAGE -> return "Differential Position: Voltage"
                                else -> return "INVALID COMBO: No Differenital Torque Current"
                            }
                        } CTRE_DifferentialType.Velocity -> {
                            when(activeUnits){
                                Units.PERCENT -> return "Differential Velocity: Duty Cycle"
                                Units.VOLTAGE -> return "Differential Velocity: Voltage"
                                else -> return "INVALID COMBO: No Differenital Torque Current"
                            }
                        } CTRE_DifferentialType.Motion_Magic -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Differential Motion Magic: Duty Cycle"
                                Units.VOLTAGE -> return "Differential Motion Magic: Voltage"
                                else -> return "INVALID COMBO: No Differenital Torque Current"
                            }
                        } CTRE_DifferentialType.Follower -> {
                            when(activeFollowerType){
                                CTRE_FollowerType.Standard -> return "Differential Follower: Non-Strict"
                                CTRE_FollowerType.Strict -> return "Differential Follower: Strict"
                            }
                        }
                    }
                }
                SetpointType.FOLLOWER -> {
                    when(activeFollowerType){
                        CTRE_FollowerType.Standard -> return "Follower: Non-Strict"
                        CTRE_FollowerType.Strict -> return "Follower: Strict"
                    }
                }
                SetpointType.NEUTRAL -> {
                    when(activeNeutralOut){
                        NeutralModeValue.Brake -> return "Brake Mode"
                        NeutralModeValue.Coast -> return "Coast Mode"
                    }
                }
                SetpointType.MUSIC -> {
                    return "Music Tone"

                }
            }
        }



    var activeConfiguration = TalonFXConfiguration()
        get() {
            if(!dirty) return field
            active.firstOrNull()?.talonFX?.configurator?.refresh(field)
                ?:logger.debug("no active talon fx's, returning default config")
            dirty = false
            return field
        }

    override fun activate(ids: Collection<Int>): Set<Int> {
        dirty = true
        logger.info { "Number Active: ${active.size}" }
        active.forEach{
            logger.info { "Active SF_TalonFX: ${it.deviceId}" }
        }

        val new = super.activate(ids)

        logger.info { "Number New: ${new.size}" }
        telemetryService.stop()
        active.filter { new.contains(it.deviceId) }.forEach{
            logger.info { "New SF_TalonFX: ${it.deviceId}" }
            activeSlotIndex = it.talonFX.closedLoopSlot.value
            telemetryService.register(it,true)

            //Update Freq Log
            /*
            logger.info("Accel update: {}", it.acceleration.appliedUpdateFrequency)
            logger.info("Bridge Out update: {}", it.bridgeOutput.appliedUpdateFrequency)
            logger.info("Device Temp update: {}", it.deviceTemp.appliedUpdateFrequency)
            logger.info("Diff Avg Pos update: {}", it.differentialAveragePosition.appliedUpdateFrequency)
            logger.info("Diff Avg Vel update: {}", it.differentialAverageVelocity.appliedUpdateFrequency)
            logger.info("Diff Diff Pos update: {}", it.differentialDifferencePosition.appliedUpdateFrequency)
            logger.info("Diff Diff Vel update: {}", it.differentialDifferenceVelocity.appliedUpdateFrequency)
            logger.info("Duty Cycle update: {}", it.dutyCycle.appliedUpdateFrequency)
            logger.info("Fwd Lim update: {}", it.forwardLimit.appliedUpdateFrequency)
            logger.info("Rev Lim update: {}", it.reverseLimit.appliedUpdateFrequency)
            logger.info("Is Pro Lic update: {}", it.isProLicensed.appliedUpdateFrequency)
            logger.info("Is MM Running update: {}", it.motionMagicIsRunning.appliedUpdateFrequency)
            logger.info("Motor Voltage update: {}", it.motorVoltage.appliedUpdateFrequency)
            logger.info("Position update: {}", it.position.appliedUpdateFrequency)
            logger.info("Processor Temp update: {}", it.processorTemp.appliedUpdateFrequency)
            logger.info("Rotor Pos update: {}", it.rotorPosition.appliedUpdateFrequency)
            logger.info("Rotor Vel update: {}", it.rotorVelocity.appliedUpdateFrequency)
            logger.info("Stator I update: {}", it.statorCurrent.appliedUpdateFrequency)
            logger.info("Supply I update: {}", it.supplyCurrent.appliedUpdateFrequency)
            logger.info("Supply V update: {}", it.supplyVoltage.appliedUpdateFrequency)
            logger.info("Torque I update: {}", it.torqueCurrent.appliedUpdateFrequency)
            logger.info("Velocity update: {}", it.velocity.appliedUpdateFrequency)

            logger.info("Closed D Out update: {}", it.closedLoopDerivativeOutput.appliedUpdateFrequency)
            logger.info("Closed Loop Err update: {}", it.closedLoopError.appliedUpdateFrequency)
            logger.info("Closed FF update: {}", it.closedLoopFeedForward.appliedUpdateFrequency)
            logger.info("Closed I update: {}", it.closedLoopIntegratedOutput.appliedUpdateFrequency)
            logger.info("Closed Loop Out: {}", it.closedLoopOutput.appliedUpdateFrequency)
            logger.info("Closed P update: {}", it.closedLoopProportionalOutput.appliedUpdateFrequency)
            logger.info("Closed Loop Ref update: {}", it.closedLoopReference.appliedUpdateFrequency)
            logger.info("Closed Loop Ref Slope: {}", it.closedLoopReferenceSlope.appliedUpdateFrequency)
            logger.info("Closed Loop Slot update: {}", it.closedLoopSlot.appliedUpdateFrequency)
            logger.info("Diff Closed D update: {}", it.differentialClosedLoopDerivativeOutput.appliedUpdateFrequency)
            logger.info("Diff Closed Err update: {}", it.differentialClosedLoopError.appliedUpdateFrequency)
            logger.info("Diff Closed FF update: {}", it.differentialClosedLoopFeedForward.appliedUpdateFrequency)
            logger.info("Diff Closed I update: {}", it.differentialClosedLoopIntegratedOutput.appliedUpdateFrequency)
            logger.info("Diff Closed Out update: {}", it.differentialClosedLoopOutput.appliedUpdateFrequency)
            logger.info("Diff Closed P update: {}", it.differentialClosedLoopProportionalOutput.appliedUpdateFrequency)
            logger.info("Diff Closed Ref update: {}", it.differentialClosedLoopReference.appliedUpdateFrequency)
            logger.info("Diff Closed Ref Slope update: {}", it.differentialClosedLoopReferenceSlope.appliedUpdateFrequency)
            logger.info("Diff Closed Slot update: {}", it.differentialClosedLoopSlot.appliedUpdateFrequency)
    */



        }


        telemetryService.start()
        active.firstOrNull()
        return new
    }

}