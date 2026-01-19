package org.strykeforce.thirdcoast.device

import com.ctre.phoenix6.configs.TalonFXSConfiguration
import com.ctre.phoenix6.hardware.TalonFXS
import com.ctre.phoenix6.signals.NeutralModeValue
import mu.KotlinLogging
import org.strykeforce.controller.*
import org.strykeforce.telemetry.TelemetryService

private const val ACTIVE_SLOT_DEFAULT = 0
private val logger = KotlinLogging.logger {}
class TalonFxsService(
    private val telemetryService: TelemetryService, factory: (id:Int) -> SF_TalonFXS
):AbstractDeviceService<SF_TalonFXS>(factory) {

    val timeout = 10.0
    var dirty = true
    var activeSlotIndex: Int = ACTIVE_SLOT_DEFAULT
    var activeUnits: CTRE_Units = CTRE_Units.Percent
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
    var limFwdMotion: Boolean = false;
    var limRevMotion: Boolean  = false;
    var grapherStatusFrameHz: Double = 0.0;
    var controlRequestUpdateFreq: Double = 100.0;

    var controlMode: String = "Duty Cycle Out"
        get() {
            when(setpointType){
                SetpointType.OPEN_LOOP -> {
                    when(activeUnits){
                        CTRE_Units.Percent -> return "Duty Cycle Out"
                        CTRE_Units.Voltage -> return "Voltage Out"
                        CTRE_Units.Torque_Current -> return "(pro) Torque Current FOC"
                    }
                }
                SetpointType.POSITION -> {
                    when(activeUnits){
                        CTRE_Units.Percent -> return "Position: Duty Cycle"
                        CTRE_Units.Voltage -> return "Position: Voltage"
                        CTRE_Units.Torque_Current -> return "(pro) Position: Torque Current FOC"
                    }
                }
                SetpointType.VELOCITY -> {
                    when(activeUnits) {
                        CTRE_Units.Percent -> return "Velocity: Duty Cycle"
                        CTRE_Units.Voltage -> return "Velocity: Voltage"
                        CTRE_Units.Torque_Current -> return "(pro) Velocity: Torque Current FOC"
                    }
                }
                SetpointType.MOTION_MAGIC -> {
                    when(active_MM_type) {
                        MotionMagicType.Standard -> {
                            when(activeUnits) {
                                CTRE_Units.Percent -> return "Motion Magic: Duty Cycle"
                                CTRE_Units.Voltage -> return "Motion Magic: Voltage"
                                CTRE_Units.Torque_Current -> return "(pro) Motion Magic: Torque Current FOC"
                            }
                        } MotionMagicType.Velocity -> {
                        when(activeUnits) {
                            CTRE_Units.Percent -> return "Motion Magic Velocity: Duty Cycle"
                            CTRE_Units.Voltage -> return "Motion Magic Velocity: Voltage"
                            CTRE_Units.Torque_Current -> return "(pro) Motion Magic Velocity: Torque Current FOC"
                        }
                    } MotionMagicType.Dynamic -> {
                        when(activeUnits) {
                            CTRE_Units.Percent -> return "Dynamic Motion Magic: Duty Cycle"
                            CTRE_Units.Voltage -> return "Dynamic Motion Magic: Voltage"
                            CTRE_Units.Torque_Current -> return "(pro) Dynamic Motion Magic: Torque Current FOC"
                        }
                    } MotionMagicType.Exponential -> {
                        when(activeUnits) {
                            CTRE_Units.Percent -> return "Motion Magic Exponential: Duty Cycle"
                            CTRE_Units.Voltage -> return "Motion Magic Exponential: Voltage"
                            CTRE_Units.Torque_Current -> return "(pro) Motion Magic Exponential: Torque Current FOC"
                        }
                    } MotionMagicType.DynamicExponential -> {
                        when(activeUnits) {
                            CTRE_Units.Percent -> return "Dynamic Motion Magic Exponential: Duty Cycle"
                            CTRE_Units.Voltage -> return "Dynamic Motion Magic Exponential: Voltage"
                            CTRE_Units.Torque_Current -> return "Dynamic Motion Magic Exponential: Torque Current FOC"
                        }
                    }
                    }
                }
                SetpointType.DIFFERENTIAL -> {
                    when(differentialType) {
                        CTRE_DifferentialType.Open_Loop -> {
                            when(activeUnits) {
                                CTRE_Units.Percent -> return "Differential: Duty Cycle"
                                CTRE_Units.Voltage -> return "Differential: Voltage"
                                else -> return "INVALID COMBO: No Differenital Torque Current"
                            }
                        } CTRE_DifferentialType.Position -> {
                        when(activeUnits) {
                            CTRE_Units.Percent -> return "Differential Position: Duty Cycle"
                            CTRE_Units.Voltage -> return "Differential Position: Voltage"
                            else -> return "INVALID COMBO: No Differenital Torque Current"
                        }
                    } CTRE_DifferentialType.Velocity -> {
                        when(activeUnits){
                            CTRE_Units.Percent -> return "Differential Velocity: Duty Cycle"
                            CTRE_Units.Voltage -> return "Differential Velocity: Voltage"
                            else -> return "INVALID COMBO: No Differenital Torque Current"
                        }
                    } CTRE_DifferentialType.Motion_Magic -> {
                        when(activeUnits) {
                            CTRE_Units.Percent -> return "Differential Motion Magic: Duty Cycle"
                            CTRE_Units.Voltage -> return "Differential Motion Magic: Voltage"
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

    var activeConfiguration = TalonFXSConfiguration()
        get() {
            if(!dirty) return field
            active.firstOrNull()?.talonFXS?.configurator?.refresh(field)
                ?:logger.debug("no active talon fxs's, returning default config")
            dirty = false
            return field
        }

    override fun activate(ids: Collection<Int>): Set<Int> {
        dirty = true
        logger.info { "Number Active: ${active.size}" }
        active.forEach {
            logger.info { "Active TalonFXS: ${it.deviceID}" }
        }

        val new = super.activate(ids)
        logger.info { "Number New: ${new.size}" }
        telemetryService.stop()
        active.filter { new.contains(it.deviceID) }.forEach{
            logger.info { "New TalonFXS: ${it.deviceID}" }
            activeSlotIndex = it.talonFXS.closedLoopSlot.value
            telemetryService.register(it, true)
        }
        telemetryService.start()
        active.firstOrNull()
        return new
    }

}