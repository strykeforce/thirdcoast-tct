package org.strykeforce.thirdcoast.device

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.ControlModeValue
import com.ctre.phoenix6.signals.NeutralModeValue
import mu.KotlinLogging
import org.strykeforce.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

private val CONTROL_MODE_DEFAULT = ControlModeValue.DutyCycleOut
private const val ACTIVE_SLOT_DEFAULT = 0
private val NEUTRAL_MODE_DEFAULT = NeutralModeValue.Coast
private const val VOLTAGE_COMPENSATION_ENABLED_DEFAULT = true
private const val CURRENT_LIMIT_ENABLED_DEFAULT = false
private const val SENSOR_PHASE_INVERTED_DEFAULT = false
private const val OUTPUT_INVERTED_DEFAULT = false
class TalonFxFDService(
    private  val telemetryService: TelemetryService,
    factory: (id: Int) -> TalonFX
) : AbstractDeviceService<TalonFX>(factory) {

    var dirty = true
    //var neutralMode = NEUTRAL_MODE_DEFAULT
    //var controlMode = CONTROL_MODE_DEFAULT
    // var voltageCompensation = VOLTAGE_COMPENSATION_ENABLED_DEFAULT
    //var sensorPhase  = SENSOR_PHASE_INVERTED_DEFAULT
    var activeSlotIndex: Int = ACTIVE_SLOT_DEFAULT
    var activeUnits: Units = Units.PERCENT
    var active_MM_type: MM_Type = MM_Type.STANDARD
    var activeFeedForward: Double = 0.0
    var activeTorqueCurrentDeadband: Double = 0.0
    var activeTorqueCurrentMaxOut: Double = 1.0
    var activeVelocity: Double = 0.0
    var activeAcceleration: Double = 0.0
    var activeJerk: Double = 0.0
    var activeDifferentialTarget: Double = 0.0
    var activeFollowerType: FollowerType = FollowerType.STRICT
    var activeOpposeMain: Boolean = false
    var activeDifferentialSlot: Int = 0
    var activeFOC: Boolean = false
    var setpointType: SetpointType = SetpointType.OPEN_LOOP
    var differentialType: DifferentialType = DifferentialType.OPEN_LOOP
    var activeNeutralOut: NeutralModeValue = NeutralModeValue.Coast
    var activeOverrideNeutral: Boolean = false
    var limFwdMotion : Boolean = false;
    var limRevMotion : Boolean = false;
    var grapherStatusFrameHz : Double = 20.0;

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
                        MM_Type.STANDARD -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Motion Magic: Duty Cycle"
                                Units.VOLTAGE -> return "Motion Magic: Voltage"
                                Units.TORQUE_CURRENT -> return "(pro) Motion Magic: Torque Current FOC"
                            }
                        } MM_Type.VELOCITY -> {
                        when(activeUnits) {
                            Units.PERCENT -> return "Motion Magic Velocity: Duty Cycle"
                            Units.VOLTAGE -> return "Motion Magic Velocity: Voltage"
                            Units.TORQUE_CURRENT -> return "(pro) Motion Magic Velocity: Torque Current FOC"
                        }
                    } MM_Type.DYNAMIC -> {
                        when(activeUnits) {
                            Units.PERCENT -> return "Dynamic Motion Magic: Duty Cycle"
                            Units.VOLTAGE -> return "Dynamic Motion Magic: Voltage"
                            Units.TORQUE_CURRENT -> return "(pro) Dynamic Motion Magic: Torque Current FOC"
                        }
                    } MM_Type.EXPONENTIAL -> {
                        when(activeUnits) {
                            Units.PERCENT -> return "Motion Magic Exponential: Duty Cycle"
                            Units.VOLTAGE -> return "Motion Magic Exponential: Voltage"
                            Units.TORQUE_CURRENT -> return "(pro) Motion Magic Exponential: Torque Current FOC"
                        }
                    }
                    }
                }
                SetpointType.DIFFERENTIAL -> {
                    when(differentialType) {
                        DifferentialType.OPEN_LOOP -> {
                            when(activeUnits) {
                                Units.PERCENT -> return "Differential: Duty Cycle"
                                Units.VOLTAGE -> return "Differential: Voltage"
                                else -> return "INVALID COMBO: No Differenital Torque Current"
                            }
                        } DifferentialType.POSITION -> {
                        when(activeUnits) {
                            Units.PERCENT -> return "Differential Position: Duty Cycle"
                            Units.VOLTAGE -> return "Differential Position: Voltage"
                            else -> return "INVALID COMBO: No Differenital Torque Current"
                        }
                    } DifferentialType.VELOCITY -> {
                        when(activeUnits){
                            Units.PERCENT -> return "Differential Velocity: Duty Cycle"
                            Units.VOLTAGE -> return "Differential Velocity: Voltage"
                            else -> return "INVALID COMBO: No Differenital Torque Current"
                        }
                    } DifferentialType.MOTION_MAGIC -> {
                        when(activeUnits) {
                            Units.PERCENT -> return "Differential Motion Magic: Duty Cycle"
                            Units.VOLTAGE -> return "Differential Motion Magic: Voltage"
                            else -> return "INVALID COMBO: No Differenital Torque Current"
                        }
                    } DifferentialType.FOLLOWER -> {
                        when(activeFollowerType){
                            FollowerType.STANDARD -> return "Differential Follower: Non-Strict"
                            FollowerType.STRICT -> return "Differential Follower: Strict"
                        }
                    }
                    }
                }
                SetpointType.FOLLOWER -> {
                    when(activeFollowerType){
                        FollowerType.STANDARD -> return "Follower: Non-Strict"
                        FollowerType.STRICT -> return "Follower: Strict"
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
            active.firstOrNull()?.configurator?.refresh(field)
                ?:logger.debug("no active talon fx's, returning default config")
            dirty = false
            return field
        }

    override fun activate(ids: Collection<Int>): Set<Int> {
        dirty = true
        logger.info { "Number Active: ${active.size}" }
        active.forEach{
            logger.info { "Active TalonFX: ${it.deviceID}" }
        }

        val new = super.activate(ids)
        logger.info { "Number New: ${new.size}" }
        telemetryService.stop()
        active.filter { new.contains(it.deviceID) }.forEach{
            logger.info { "New TalonFX: ${it.deviceID}" }
            activeSlotIndex = it.closedLoopSlot.value
            telemetryService.register(it,true)
        }


        telemetryService.start()
        active.firstOrNull()
        return new
    }
}