package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.controls.CoastOut
import com.ctre.phoenix6.controls.ControlRequest
import com.ctre.phoenix6.controls.DifferentialDutyCycle
import com.ctre.phoenix6.controls.DifferentialFollower
import com.ctre.phoenix6.controls.DifferentialMotionMagicDutyCycle
import com.ctre.phoenix6.controls.DifferentialMotionMagicVoltage
import com.ctre.phoenix6.controls.DifferentialPositionDutyCycle
import com.ctre.phoenix6.controls.DifferentialPositionVoltage
import com.ctre.phoenix6.controls.DifferentialStrictFollower
import com.ctre.phoenix6.controls.DifferentialVelocityDutyCycle
import com.ctre.phoenix6.controls.DifferentialVelocityVoltage
import com.ctre.phoenix6.controls.DifferentialVoltage
import com.ctre.phoenix6.controls.DutyCycleOut
import com.ctre.phoenix6.controls.DynamicMotionMagicDutyCycle
import com.ctre.phoenix6.controls.DynamicMotionMagicTorqueCurrentFOC
import com.ctre.phoenix6.controls.DynamicMotionMagicVoltage
import com.ctre.phoenix6.controls.Follower
import com.ctre.phoenix6.controls.MotionMagicDutyCycle
import com.ctre.phoenix6.controls.MotionMagicExpoDutyCycle
import com.ctre.phoenix6.controls.MotionMagicExpoTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage
import com.ctre.phoenix6.controls.MotionMagicTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicVelocityDutyCycle
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage
import com.ctre.phoenix6.controls.MotionMagicVoltage
import com.ctre.phoenix6.controls.MusicTone
import com.ctre.phoenix6.controls.PositionDutyCycle
import com.ctre.phoenix6.controls.PositionTorqueCurrentFOC
import com.ctre.phoenix6.controls.PositionVoltage
import com.ctre.phoenix6.controls.StaticBrake
import com.ctre.phoenix6.controls.StrictFollower
import com.ctre.phoenix6.controls.TorqueCurrentFOC
import com.ctre.phoenix6.controls.VelocityDutyCycle
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC
import com.ctre.phoenix6.controls.VelocityVoltage
import com.ctre.phoenix6.controls.VoltageOut
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.*
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger{}

class P6RunCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override fun execute(): Command {
        var done = false
        while(!done){
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val setpoint = setpoints[0].toDouble()
                val duration = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                var setpointType: SetpointType
                var units: Units
                var motionType: MM_Type
                var differentialType: DifferentialType
                var followerType: FollowerType
                var enableFOC: Boolean
                var overrideNeutral: Boolean
                var limFwdMotion: Boolean
                var limRevMotion: Boolean
                var velocity: Double
                var acceleration: Double
                var jerk: Double
                var feedFwd: Double
                var slot: Int
                var diffSlot: Int
                var diffPos: Double
                var torqueCurrentMaxOut: Double
                var torqueCurrentDeadband: Double
                var controlReqUpdateFreq: Double
                when(device) {
                    "fx" -> {
                        setpointType = if(bus == "rio") talonFxService.setpointType else talonFxFDService.setpointType
                        units = if(bus == "rio") talonFxService.activeUnits else talonFxFDService.activeUnits
                        motionType = if(bus == "rio") talonFxService.active_MM_type else talonFxFDService.active_MM_type
                        differentialType = if(bus == "rio") talonFxService.differentialType else talonFxFDService.differentialType
                        followerType = if(bus == "rio") talonFxService.activeFollowerType else talonFxFDService.activeFollowerType
                        enableFOC = if(bus == "rio") talonFxService.activeFOC else talonFxFDService.activeFOC
                        overrideNeutral = if(bus == "rio") talonFxService.activeOverrideNeutral else talonFxFDService.activeOverrideNeutral
                        limFwdMotion = if(bus == "rio") talonFxService.limFwdMotion else talonFxFDService.limFwdMotion
                        limRevMotion = if(bus == "rio") talonFxService.limRevMotion else talonFxFDService.limRevMotion
                        velocity = if(bus == "rio") talonFxService.activeVelocity else talonFxFDService.activeVelocity
                        acceleration = if(bus == "rio") talonFxService.activeAcceleration else talonFxFDService.activeAcceleration
                        jerk = if(bus == "rio") talonFxService.activeJerk else talonFxFDService.activeJerk
                        feedFwd = if(bus == "rio") talonFxService.activeFeedForward else talonFxFDService.activeFeedForward
                        slot = if(bus == "rio") talonFxService.activeSlotIndex else talonFxFDService.activeSlotIndex
                        diffSlot = if(bus == "rio") talonFxService.activeDifferentialSlot else talonFxFDService.activeDifferentialSlot
                        diffPos = if(bus == "rio") talonFxService.activeDifferentialTarget else talonFxFDService.activeDifferentialTarget
                        torqueCurrentMaxOut = if(bus == "rio") talonFxService.activeTorqueCurrentMaxOut else talonFxFDService.activeTorqueCurrentMaxOut
                        torqueCurrentDeadband = if(bus == "rio") talonFxService.activeTorqueCurrentDeadband else talonFxFDService.activeTorqueCurrentDeadband
                        controlReqUpdateFreq = if(bus == "rio") talonFxService.controlRequestUpdateFreq else talonFxFDService.controlRequestUpdateFreq
                    }
                    "fxs" -> {
                        setpointType = if(bus == "rio") talonFxsService.setpointType else talonFxsFDService.setpointType
                        units = if(bus == "rio") talonFxsService.activeUnits else talonFxsFDService.activeUnits
                        motionType = if(bus == "rio") talonFxsService.active_MM_type else talonFxsFDService.active_MM_type
                        differentialType = if(bus == "rio") talonFxsService.differentialType else talonFxsFDService.differentialType
                        followerType = if(bus == "rio") talonFxsService.activeFollowerType else talonFxsFDService.activeFollowerType
                        enableFOC = if(bus == "rio") talonFxsService.activeFOC else talonFxsFDService.activeFOC
                        overrideNeutral = if(bus == "rio") talonFxsService.activeOverrideNeutral else talonFxsFDService.activeOverrideNeutral
                        limFwdMotion = if(bus == "rio") talonFxsService.limFwdMotion else talonFxsFDService.limFwdMotion
                        limRevMotion = if(bus == "rio") talonFxsService.limRevMotion else talonFxsFDService.limRevMotion
                        velocity = if(bus == "rio") talonFxsService.activeVelocity else talonFxsFDService.activeVelocity
                        acceleration = if(bus == "rio") talonFxsService.activeAcceleration else talonFxsFDService.activeAcceleration
                        jerk = if(bus == "rio") talonFxsService.activeJerk else talonFxsFDService.activeJerk
                        feedFwd = if(bus == "rio") talonFxsService.activeFeedForward else talonFxsFDService.activeFeedForward
                        slot = if(bus == "rio") talonFxsService.activeSlotIndex else talonFxsFDService.activeSlotIndex
                        diffSlot = if(bus == "rio") talonFxsService.activeDifferentialSlot else talonFxsFDService.activeDifferentialSlot
                        diffPos = if(bus == "rio") talonFxsService.activeDifferentialTarget else talonFxsFDService.activeDifferentialTarget
                        torqueCurrentMaxOut = if(bus == "rio") talonFxsService.activeTorqueCurrentMaxOut else talonFxsFDService.activeTorqueCurrentMaxOut
                        torqueCurrentDeadband = if(bus == "rio") talonFxsService.activeTorqueCurrentDeadband else talonFxsFDService.activeTorqueCurrentDeadband
                        controlReqUpdateFreq = if(bus == "rio") talonFxsService.controlRequestUpdateFreq else talonFxsFDService.controlRequestUpdateFreq
                    }
                    else -> throw IllegalArgumentException()
                }
                var controlRequest: ControlRequest =
                    DutyCycleOut(0.0)
                        .withEnableFOC(false)
                        .withOverrideBrakeDurNeutral(false)
                        .withLimitForwardMotion(limFwdMotion).withLimitReverseMotion(limRevMotion)
                        .withUpdateFreqHz(controlReqUpdateFreq);


                //sanity checks
                if (units == Units.PERCENT && setpointType == SetpointType.OPEN_LOOP && !(-1.0..1.0).contains(setpoint)) {
                    terminal.warn("setpoint must be in range -1.0 to 1.0 for percent modes")
                    continue
                }

                if (((setpointType == SetpointType.MOTION_MAGIC && motionType != MM_Type.VELOCITY) || setpointType == SetpointType.POSITION) && duration > 0.0) {
                    terminal.warn("specifying a duration in position modes not allowed")
                    continue
                }

                logger.info { "Setpoint: $setpoint" }

                //make control request
                when (setpointType) {
                    SetpointType.OPEN_LOOP -> {
                        when (units) {
                            Units.PERCENT -> controlRequest = DutyCycleOut(setpoint)
                                .withEnableFOC(enableFOC)
                                .withOverrideBrakeDurNeutral(overrideNeutral)
                                .withLimitForwardMotion(limFwdMotion)
                                .withLimitReverseMotion(limRevMotion)
                                .withUpdateFreqHz(controlReqUpdateFreq)
                            Units.VOLTAGE -> controlRequest = VoltageOut(setpoint)
                                .withEnableFOC(enableFOC)
                                .withOverrideBrakeDurNeutral(overrideNeutral)
                                .withLimitForwardMotion(limFwdMotion)
                                .withLimitReverseMotion(limRevMotion)
                                .withUpdateFreqHz(controlReqUpdateFreq)
                            Units.TORQUE_CURRENT -> controlRequest = TorqueCurrentFOC(setpoint)
                                .withMaxAbsDutyCycle(torqueCurrentMaxOut)
                                .withDeadband(torqueCurrentDeadband)
                                .withLimitForwardMotion(limFwdMotion)
                                .withLimitReverseMotion(limRevMotion)
                                .withOverrideCoastDurNeutral(overrideNeutral)
                                .withUpdateFreqHz(controlReqUpdateFreq)
                        }
                    }

                    SetpointType.POSITION -> {
                        when (units) {
                            Units.PERCENT -> controlRequest =
                                PositionDutyCycle(setpoint)
                                    .withVelocity(velocity)
                                    .withEnableFOC(enableFOC)
                                    .withFeedForward(feedFwd)
                                    .withSlot(slot)
                                    .withOverrideBrakeDurNeutral(overrideNeutral)
                                    .withLimitForwardMotion(limFwdMotion)
                                    .withLimitReverseMotion(limRevMotion)
                                    .withUpdateFreqHz(controlReqUpdateFreq)

                            Units.VOLTAGE -> controlRequest =
                                PositionVoltage(setpoint)
                                    .withVelocity(velocity)
                                    .withEnableFOC(enableFOC)
                                    .withFeedForward(feedFwd)
                                    .withSlot(slot)
                                    .withOverrideBrakeDurNeutral(overrideNeutral)
                                    .withLimitForwardMotion(limFwdMotion)
                                    .withLimitReverseMotion(limRevMotion)
                                    .withUpdateFreqHz(controlReqUpdateFreq)

                            Units.TORQUE_CURRENT -> controlRequest =
                                PositionTorqueCurrentFOC(setpoint)
                                    .withVelocity(velocity)
                                    .withFeedForward(feedFwd)
                                    .withSlot(slot)
                                    .withOverrideCoastDurNeutral(overrideNeutral)
                                    .withLimitForwardMotion(limFwdMotion)
                                    .withLimitReverseMotion(limRevMotion)
                                    .withUpdateFreqHz(controlReqUpdateFreq)
                        }
                    }

                    SetpointType.VELOCITY -> {
                        when (units) {
                            Units.PERCENT -> controlRequest =
                                VelocityDutyCycle(setpoint)
                                    .withAcceleration(acceleration)
                                    .withEnableFOC(enableFOC)
                                    .withFeedForward(feedFwd)
                                    .withSlot(slot)
                                    .withOverrideBrakeDurNeutral(overrideNeutral)
                                    .withLimitForwardMotion(limFwdMotion)
                                    .withLimitReverseMotion(limRevMotion)
                                    .withUpdateFreqHz(controlReqUpdateFreq)

                            Units.VOLTAGE -> controlRequest =
                                VelocityVoltage(setpoint)
                                    .withAcceleration(acceleration)
                                    .withEnableFOC(enableFOC)
                                    .withFeedForward(feedFwd)
                                    .withSlot(slot)
                                    .withOverrideBrakeDurNeutral(overrideNeutral)
                                    .withLimitForwardMotion(limFwdMotion)
                                    .withLimitReverseMotion(limRevMotion)
                                    .withUpdateFreqHz(controlReqUpdateFreq)

                            Units.TORQUE_CURRENT -> controlRequest =
                                VelocityTorqueCurrentFOC(setpoint)
                                    .withAcceleration(acceleration)
                                    .withFeedForward(feedFwd)
                                    .withSlot(slot)
                                    .withOverrideCoastDurNeutral(overrideNeutral)
                                    .withLimitForwardMotion(limFwdMotion)
                                    .withLimitReverseMotion(limRevMotion)
                                    .withUpdateFreqHz(controlReqUpdateFreq)
                        }
                    }

                    SetpointType.MOTION_MAGIC -> {
                        when (motionType) {
                            MM_Type.STANDARD -> {
                                logger.info { "Motion Magic: $motionType, $units" }
                                when (units) {
                                    Units.PERCENT -> controlRequest =
                                        MotionMagicDutyCycle(setpoint)
                                            .withEnableFOC(enableFOC)
                                            .withSlot(slot)
                                            .withFeedForward(feedFwd)
                                            .withOverrideBrakeDurNeutral(overrideNeutral)
                                            .withLimitForwardMotion(limFwdMotion)
                                            .withLimitReverseMotion(limRevMotion)
                                            .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest =
                                        MotionMagicVoltage(setpoint)
                                            .withEnableFOC(enableFOC)
                                            .withFeedForward(feedFwd)
                                            .withSlot(slot)
                                            .withOverrideBrakeDurNeutral(overrideNeutral)
                                            .withLimitForwardMotion(limFwdMotion)
                                            .withLimitReverseMotion(limRevMotion)
                                            .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.TORQUE_CURRENT -> controlRequest =
                                        MotionMagicTorqueCurrentFOC(setpoint)
                                            .withFeedForward(feedFwd)
                                            .withSlot(slot)
                                            .withOverrideCoastDurNeutral(overrideNeutral)
                                            .withLimitForwardMotion(limFwdMotion)
                                            .withLimitReverseMotion(limRevMotion)
                                            .withUpdateFreqHz(controlReqUpdateFreq)
                                }
                            }

                            MM_Type.VELOCITY -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = MotionMagicVelocityDutyCycle(setpoint)
                                        .withAcceleration(acceleration)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest = MotionMagicVelocityVoltage(setpoint)
                                        .withAcceleration(acceleration)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.TORQUE_CURRENT -> controlRequest = MotionMagicVelocityTorqueCurrentFOC(setpoint)
                                        .withAcceleration(acceleration)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideCoastDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)
                                }
                            }

                            MM_Type.DYNAMIC -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DynamicMotionMagicDutyCycle(setpoint, velocity, acceleration, jerk)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest = DynamicMotionMagicVoltage(setpoint, velocity, acceleration, jerk)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.TORQUE_CURRENT -> controlRequest = DynamicMotionMagicTorqueCurrentFOC(setpoint, velocity, acceleration, jerk)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideCoastDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)
                                }
                            }
                            MM_Type.EXPONENTIAL -> {
                                when(units) {
                                    Units.PERCENT -> controlRequest = MotionMagicExpoDutyCycle(setpoint)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)
                                    Units.VOLTAGE -> controlRequest = MotionMagicExpoVoltage(setpoint)
                                        .withEnableFOC(enableFOC)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)
                                    Units.TORQUE_CURRENT -> controlRequest = MotionMagicExpoTorqueCurrentFOC(setpoint)
                                        .withFeedForward(feedFwd)
                                        .withSlot(slot)
                                        .withOverrideCoastDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)
                                }
                            }
                        }
                    }

                    SetpointType.DIFFERENTIAL -> {
                        when (differentialType) {
                            DifferentialType.OPEN_LOOP -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest =
                                        DifferentialDutyCycle(setpoint, diffPos)
                                            .withEnableFOC(enableFOC)
                                            .withDifferentialSlot(diffSlot)
                                            .withOverrideBrakeDurNeutral(overrideNeutral)
                                            .withLimitForwardMotion(limFwdMotion)
                                            .withLimitReverseMotion(limRevMotion)
                                            .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest =
                                        DifferentialVoltage(setpoint, diffPos)
                                            .withEnableFOC(enableFOC)
                                            .withDifferentialSlot(diffSlot)
                                            .withOverrideBrakeDurNeutral(overrideNeutral)
                                            .withLimitForwardMotion(limFwdMotion)
                                            .withLimitReverseMotion(limRevMotion)
                                            .withUpdateFreqHz(controlReqUpdateFreq)

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.POSITION -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DifferentialPositionDutyCycle(setpoint, diffPos)
                                        .withEnableFOC(enableFOC)
                                        .withTargetSlot(slot)
                                        .withDifferentialSlot(diffSlot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest = DifferentialPositionVoltage(setpoint, diffPos)
                                        .withEnableFOC(enableFOC)
                                        .withTargetSlot(slot)
                                        .withDifferentialSlot(diffSlot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.VELOCITY -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DifferentialVelocityDutyCycle(setpoint, diffPos)
                                        .withEnableFOC(enableFOC)
                                        .withTargetSlot(slot)
                                        .withDifferentialSlot(diffSlot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest = DifferentialVelocityVoltage(setpoint, diffPos)
                                        .withEnableFOC(enableFOC)
                                        .withTargetSlot(slot)
                                        .withDifferentialSlot(diffSlot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.MOTION_MAGIC -> {
                                when (units) {
                                    Units.PERCENT -> controlRequest = DifferentialMotionMagicDutyCycle(setpoint, diffPos)
                                        .withEnableFOC(enableFOC)
                                        .withTargetSlot(slot)
                                        .withDifferentialSlot(diffSlot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    Units.VOLTAGE -> controlRequest = DifferentialMotionMagicVoltage(setpoint, diffPos)
                                        .withEnableFOC(enableFOC)
                                        .withTargetSlot(slot)
                                        .withDifferentialSlot(diffSlot)
                                        .withOverrideBrakeDurNeutral(overrideNeutral)
                                        .withLimitForwardMotion(limFwdMotion)
                                        .withLimitReverseMotion(limRevMotion)
                                        .withUpdateFreqHz(controlReqUpdateFreq)

                                    else -> {
                                        terminal.warn("Units chosen not valid for this control mode")
                                        continue
                                    }
                                }
                            }

                            DifferentialType.FOLLOWER -> {
                                when (followerType) {
                                    FollowerType.STANDARD -> controlRequest =
                                        DifferentialFollower(setpoint.toInt(), talonFxService.activeOpposeMain)

                                    FollowerType.STRICT -> controlRequest =
                                        DifferentialStrictFollower(setpoint.toInt()).withUpdateFreqHz(controlReqUpdateFreq)
                                }
                            }

                        }

                    }

                    SetpointType.FOLLOWER -> {
                        when (followerType) {
                            FollowerType.STANDARD -> controlRequest =
                                Follower(setpoint.toInt(), talonFxService.activeOpposeMain)

                            FollowerType.STRICT -> controlRequest = StrictFollower(setpoint.toInt()).withUpdateFreqHz(controlReqUpdateFreq)
                        }
                    }

                    SetpointType.NEUTRAL -> {
                        when (talonFxService.activeNeutralOut) {
                            NeutralModeValue.Coast -> controlRequest = CoastOut().withUpdateFreqHz(controlReqUpdateFreq)
                            NeutralModeValue.Brake -> controlRequest = StaticBrake().withUpdateFreqHz(controlReqUpdateFreq)
                        }
                    }

                    SetpointType.MUSIC -> {
                        controlRequest = MusicTone(setpoint).withUpdateFreqHz(controlReqUpdateFreq)
                    }

                }

                //run Talon
                when(device) {
                    "fx" -> {
                        if(bus == "rio") {
                            talonFxService.active.forEach {
                                logger.info { "Control Request: ${controlRequest.name}: ${controlRequest.controlInfo}" }
                                it.setControl(controlRequest)
                            }
                        } else if(bus == "canivore") {
                            talonFxFDService.active.forEach {
                                logger.info { "Control Request: ${controlRequest.name}: ${controlRequest.controlInfo}" }
                                it.setControl(controlRequest)
                            }
                        } else throw  IllegalArgumentException()
                    }
                    "fxs" -> {
                        if(bus == "rio") {
                            talonFxsService.active.forEach {
                                logger.info { "Control Request: ${controlRequest.name}: ${controlRequest.controlInfo}" }
                                it.setControl(controlRequest)
                            }
                        } else if(bus == "canivore") {
                            talonFxsFDService.active.forEach {
                                logger.info { "Control Request: ${controlRequest.name}: ${controlRequest.controlInfo}" }
                                it.setControl(controlRequest)
                            }
                        } else throw  IllegalArgumentException()
                    }
                    else -> throw IllegalArgumentException()
                }


                //Check Timeout
                if (duration > 0.0) {
                    logger.debug { "run duration = $duration seconds" }
                    Timer.delay(duration)
                    logger.debug { "run duration expired, setting output = 0.0" }
                    when(device) {
                        "fx" -> {
                            if(bus == "rio") {
                                talonFxService.active.forEach { it.set(0.0) }
                            } else if(bus == "canivore") {
                                talonFxFDService.active.forEach { it.set(0.0) }
                            } else throw  IllegalArgumentException()
                        }
                        "fxs" -> {
                            if(bus == "rio") {
                                talonFxsService.active.forEach { it.set(0.0) }
                            } else if(bus == "canivore") {
                                talonFxsFDService.active.forEach { it.set(0.0) }
                            } else throw  IllegalArgumentException()
                        }
                        else -> throw IllegalArgumentException()
                    }

                }
            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }
}