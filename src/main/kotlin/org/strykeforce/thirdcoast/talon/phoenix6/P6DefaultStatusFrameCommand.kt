package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService
import java.lang.IllegalArgumentException

class P6DefaultStatusFrameCommand(
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
        val timeout = talonFxService.timeout
        val value = 10.0
        when(device) {
            "fx" -> {
                if (bus == "rio") {
                    talonFxService.active.forEach {
                        talonFxService.grapherStatusFrameHz = 0.0;
                        it.acceleration.setUpdateFrequency(50.0, timeout)
                        it.bridgeOutput.setUpdateFrequency(100.0, timeout)
                        it.deviceTemp.setUpdateFrequency(4.0, timeout)
                        it.differentialAveragePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialAverageVelocity.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferencePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferenceVelocity.setUpdateFrequency(4.0, timeout)
                        it.dutyCycle.setUpdateFrequency(100.0, timeout)
                        it.forwardLimit.setUpdateFrequency(100.0, timeout)
                        it.reverseLimit.setUpdateFrequency(100.0, timeout)
                        it.isProLicensed.setUpdateFrequency(4.0, timeout)
                        it.motionMagicIsRunning.setUpdateFrequency(4.0, timeout)
                        it.motorVoltage.setUpdateFrequency(100.0, timeout)
                        it.position.setUpdateFrequency(50.0, timeout)
                        it.processorTemp.setUpdateFrequency(4.0, timeout)
                        it.rotorPosition.setUpdateFrequency(4.0, timeout)
                        it.rotorVelocity.setUpdateFrequency(4.0, timeout)
                        it.statorCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyVoltage.setUpdateFrequency(4.0, timeout)
                        it.torqueCurrent.setUpdateFrequency(100.0, timeout)
                        it.velocity.setUpdateFrequency(50.0, timeout)

                        it.closedLoopDerivativeOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopError.setUpdateFrequency(4.0, timeout)
                        it.closedLoopFeedForward.setUpdateFrequency(4.0, timeout)
                        it.closedLoopIntegratedOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopProportionalOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReference.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReferenceSlope.setUpdateFrequency(4.0, timeout)
                        it.closedLoopSlot.setUpdateFrequency(4.0, timeout)
                        it.differentialClosedLoopDerivativeOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopError.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopFeedForward.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopIntegratedOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopProportionalOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReference.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReferenceSlope.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopSlot.setUpdateFrequency(4.0, timeout)
                    }
                } else if (bus == "canivore") {
                    talonFxFDService.active.forEach {
                        talonFxFDService.grapherStatusFrameHz = 0.0;
                        it.acceleration.setUpdateFrequency(50.0, timeout)
                        it.bridgeOutput.setUpdateFrequency(100.0, timeout)
                        it.deviceTemp.setUpdateFrequency(4.0, timeout)
                        it.differentialAveragePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialAverageVelocity.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferencePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferenceVelocity.setUpdateFrequency(4.0, timeout)
                        it.dutyCycle.setUpdateFrequency(100.0, timeout)
                        it.forwardLimit.setUpdateFrequency(100.0, timeout)
                        it.reverseLimit.setUpdateFrequency(100.0, timeout)
                        it.isProLicensed.setUpdateFrequency(4.0, timeout)
                        it.motionMagicIsRunning.setUpdateFrequency(4.0, timeout)
                        it.motorVoltage.setUpdateFrequency(100.0, timeout)
                        it.position.setUpdateFrequency(50.0, timeout)
                        it.processorTemp.setUpdateFrequency(4.0, timeout)
                        it.rotorPosition.setUpdateFrequency(4.0, timeout)
                        it.rotorVelocity.setUpdateFrequency(4.0, timeout)
                        it.statorCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyVoltage.setUpdateFrequency(4.0, timeout)
                        it.torqueCurrent.setUpdateFrequency(100.0, timeout)
                        it.velocity.setUpdateFrequency(50.0, timeout)

                        it.closedLoopDerivativeOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopError.setUpdateFrequency(4.0, timeout)
                        it.closedLoopFeedForward.setUpdateFrequency(4.0, timeout)
                        it.closedLoopIntegratedOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopProportionalOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReference.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReferenceSlope.setUpdateFrequency(4.0, timeout)
                        it.closedLoopSlot.setUpdateFrequency(4.0, timeout)
                        it.differentialClosedLoopDerivativeOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopError.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopFeedForward.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopIntegratedOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopProportionalOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReference.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReferenceSlope.setUpdateFrequency(100.0, timeout)
                    }
                }

            }

            "fxs" -> {
                if (bus == "rio") {
                    talonFxsService.active.forEach {
                        talonFxsService.grapherStatusFrameHz = 0.0;
                        it.acceleration.setUpdateFrequency(50.0, timeout)
                        it.bridgeOutput.setUpdateFrequency(100.0, timeout)
                        it.deviceTemp.setUpdateFrequency(4.0, timeout)
                        it.differentialAveragePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialAverageVelocity.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferencePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferenceVelocity.setUpdateFrequency(4.0, timeout)
                        it.dutyCycle.setUpdateFrequency(100.0, timeout)
                        it.forwardLimit.setUpdateFrequency(100.0, timeout)
                        it.reverseLimit.setUpdateFrequency(100.0, timeout)
                        it.isProLicensed.setUpdateFrequency(4.0, timeout)
                        it.motionMagicIsRunning.setUpdateFrequency(4.0, timeout)
                        it.motorVoltage.setUpdateFrequency(100.0, timeout)
                        it.position.setUpdateFrequency(50.0, timeout)
                        it.processorTemp.setUpdateFrequency(4.0, timeout)
                        it.rotorPosition.setUpdateFrequency(4.0, timeout)
                        it.rotorVelocity.setUpdateFrequency(4.0, timeout)
                        it.statorCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyVoltage.setUpdateFrequency(4.0, timeout)
                        it.torqueCurrent.setUpdateFrequency(100.0, timeout)
                        it.velocity.setUpdateFrequency(50.0, timeout)

                        it.closedLoopDerivativeOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopError.setUpdateFrequency(4.0, timeout)
                        it.closedLoopFeedForward.setUpdateFrequency(4.0, timeout)
                        it.closedLoopIntegratedOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopProportionalOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReference.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReferenceSlope.setUpdateFrequency(4.0, timeout)
                        it.closedLoopSlot.setUpdateFrequency(4.0, timeout)
                        it.differentialClosedLoopDerivativeOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopError.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopFeedForward.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopIntegratedOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopProportionalOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReference.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReferenceSlope.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopSlot.setUpdateFrequency(4.0, timeout)

                        it.ancillaryDeviceTemp.setUpdateFrequency(4.0, timeout)
                        it.externalMotorTemp.setUpdateFrequency(4.0, timeout)
                        it.rawPulseWidthPosition.setUpdateFrequency(0.0,timeout)
                        it.rawQuadraturePosition.setUpdateFrequency(0.0, timeout)
                        it.rawPulseWidthVelocity.setUpdateFrequency(0.0, timeout)
                        it.rawQuadratureVelocity.setUpdateFrequency(0.0, timeout)
                    }
                } else if (bus == "canivore") {
                    talonFxsFDService.active.forEach {
                        talonFxFDService.grapherStatusFrameHz = 0.0;
                        it.acceleration.setUpdateFrequency(50.0, timeout)
                        it.bridgeOutput.setUpdateFrequency(100.0, timeout)
                        it.deviceTemp.setUpdateFrequency(4.0, timeout)
                        it.differentialAveragePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialAverageVelocity.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferencePosition.setUpdateFrequency(4.0, timeout)
                        it.differentialDifferenceVelocity.setUpdateFrequency(4.0, timeout)
                        it.dutyCycle.setUpdateFrequency(100.0, timeout)
                        it.forwardLimit.setUpdateFrequency(100.0, timeout)
                        it.reverseLimit.setUpdateFrequency(100.0, timeout)
                        it.isProLicensed.setUpdateFrequency(4.0, timeout)
                        it.motionMagicIsRunning.setUpdateFrequency(4.0, timeout)
                        it.motorVoltage.setUpdateFrequency(100.0, timeout)
                        it.position.setUpdateFrequency(50.0, timeout)
                        it.processorTemp.setUpdateFrequency(4.0, timeout)
                        it.rotorPosition.setUpdateFrequency(4.0, timeout)
                        it.rotorVelocity.setUpdateFrequency(4.0, timeout)
                        it.statorCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyCurrent.setUpdateFrequency(4.0, timeout)
                        it.supplyVoltage.setUpdateFrequency(4.0, timeout)
                        it.torqueCurrent.setUpdateFrequency(100.0, timeout)
                        it.velocity.setUpdateFrequency(50.0, timeout)

                        it.closedLoopDerivativeOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopError.setUpdateFrequency(4.0, timeout)
                        it.closedLoopFeedForward.setUpdateFrequency(4.0, timeout)
                        it.closedLoopIntegratedOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopProportionalOutput.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReference.setUpdateFrequency(4.0, timeout)
                        it.closedLoopReferenceSlope.setUpdateFrequency(4.0, timeout)
                        it.closedLoopSlot.setUpdateFrequency(4.0, timeout)
                        it.differentialClosedLoopDerivativeOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopError.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopFeedForward.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopIntegratedOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopProportionalOutput.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReference.setUpdateFrequency(100.0, timeout)
                        it.differentialClosedLoopReferenceSlope.setUpdateFrequency(100.0, timeout)

                        it.ancillaryDeviceTemp.setUpdateFrequency(4.0, timeout)
                        it.externalMotorTemp.setUpdateFrequency(4.0, timeout)
                        it.rawPulseWidthPosition.setUpdateFrequency(0.0,timeout)
                        it.rawQuadraturePosition.setUpdateFrequency(0.0, timeout)
                        it.rawPulseWidthVelocity.setUpdateFrequency(0.0, timeout)
                        it.rawQuadratureVelocity.setUpdateFrequency(0.0, timeout)
                    }
                }

            }
            else -> throw IllegalArgumentException()
        }
        return super.execute()
    }
}