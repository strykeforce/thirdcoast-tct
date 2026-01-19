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
                        it.registerAcceleration()
                        it.registerBridgeOutput()
                        it.registerDeviceTemp()
                        it.registerDifferentialAveragePosition()
                        it.registerDifferentialAverageVelocity()
                        it.registerDifferentialDifferencePosition()
                        it.registerDifferentialDifferenceVelocity()
                        it.registerDutyCycle()
                        it.registerForwardLimit()
                        it.registerReverseLimit()
                        it.registerIsProLicensed()
                        it.registerMotionMagicIsRunning()
                        it.registerMotorVoltage()
                        it.registerPosition()
                        it.registerProcessorTemp()
                        it.registerRotorPosition()
                        it.registerRotorVelocity()
                        it.registerStatorCurrent()
                        it.registerSupplyCurrent()
                        it.registerSupplyVoltage()
                        it.registerTorqueCurrent()
                        it.registerVelocity()

                        it.registerClosedLoopDerivativeOutput()
                        it.registerClosedLoopError()
                        it.registerClosedLoopFeedForward()
                        it.registerClosedLoopIntegratedOutput()
                        it.registerClosedLoopOutput()
                        it.registerClosedLoopProportionalOutput()
                        it.registerClosedLoopReference()
                        it.registerClosedLoopReferenceSlope()
                        it.registerClosedLoopSlot()
                        it.registerDifferentialClosedLoopDerivativeOutput()
                        it.registerDifferentialClosedLoopError()
                        it.registerDifferentialClosedLoopFeedForward()
                        it.registerDifferentialClosedLoopIntegratedOutput()
                        it.registerDifferentialClosedLoopOutput()
                        it.registerDifferentialClosedLoopProportionalOutput()
                        it.registerDifferentialClosedLoopReference()
                        it.registerDifferentialClosedLoopReferenceSlope()
                        it.registerDifferentialClosedLoopSlot()
                    }
                } else if (bus == "canivore") {
                    talonFxFDService.active.forEach {
                        talonFxFDService.grapherStatusFrameHz = 0.0;
                        it.registerAcceleration()
                        it.registerBridgeOutput()
                        it.registerDeviceTemp()
                        it.registerDifferentialAveragePosition()
                        it.registerDifferentialAverageVelocity()
                        it.registerDifferentialDifferencePosition()
                        it.registerDifferentialDifferenceVelocity()
                        it.registerDutyCycle()
                        it.registerForwardLimit()
                        it.registerReverseLimit()
                        it.registerIsProLicensed()
                        it.registerMotionMagicIsRunning()
                        it.registerMotorVoltage()
                        it.registerPosition()
                        it.registerProcessorTemp()
                        it.registerRotorPosition()
                        it.registerRotorVelocity()
                        it.registerStatorCurrent()
                        it.registerSupplyCurrent()
                        it.registerSupplyVoltage()
                        it.registerTorqueCurrent()
                        it.registerVelocity()

                        it.registerClosedLoopDerivativeOutput()
                        it.registerClosedLoopError()
                        it.registerClosedLoopFeedForward()
                        it.registerClosedLoopIntegratedOutput()
                        it.registerClosedLoopOutput()
                        it.registerClosedLoopProportionalOutput()
                        it.registerClosedLoopReference()
                        it.registerClosedLoopReferenceSlope()
                        it.registerClosedLoopSlot()
                        it.registerDifferentialClosedLoopDerivativeOutput()
                        it.registerDifferentialClosedLoopError()
                        it.registerDifferentialClosedLoopFeedForward()
                        it.registerDifferentialClosedLoopIntegratedOutput()
                        it.registerDifferentialClosedLoopOutput()
                        it.registerDifferentialClosedLoopProportionalOutput()
                        it.registerDifferentialClosedLoopReference()
                        it.registerDifferentialClosedLoopReferenceSlope()
                        it.registerDifferentialClosedLoopSlot()
                    }
                }

            }

            "fxs" -> {
                if (bus == "rio") {
                    talonFxsService.active.forEach {
                        talonFxsService.grapherStatusFrameHz = 0.0;
                        it.registerAcceleration()
                        it.registerBridgeOutput()
                        it.registerDeviceTemp()
                        it.registerDifferentialAveragePosition()
                        it.registerDifferentialAverageVelocity()
                        it.registerDifferentialDifferencePosition()
                        it.registerDifferentialDifferenceVelocity()
                        it.registerDutyCycle()
                        it.registerForwardLimit()
                        it.registerReverseLimit()
                        it.registerIsProLicensed()
                        it.registerMotionMagicIsRunning()
                        it.registerMotorVoltage()
                        it.registerPosition()
                        it.registerProcessorTemp()
                        it.registerRotorPosition()
                        it.registerRotorVelocity()
                        it.registerStatorCurrent()
                        it.registerSupplyCurrent()
                        it.registerSupplyVoltage()
                        it.registerTorqueCurrent()
                        it.registerVelocity()

                        it.registerClosedLoopDerivativeOutput()
                        it.registerClosedLoopError()
                        it.registerClosedLoopFeedForward()
                        it.registerClosedLoopIntegratedOutput()
                        it.registerClosedLoopOutput()
                        it.registerClosedLoopProportionalOutput()
                        it.registerClosedLoopReference()
                        it.registerClosedLoopReferenceSlope()
                        it.registerClosedLoopSlot()
                        it.registerDifferentialClosedLoopDerivativeOutput()
                        it.registerDifferentialClosedLoopError()
                        it.registerDifferentialClosedLoopFeedForward()
                        it.registerDifferentialClosedLoopIntegratedOutput()
                        it.registerDifferentialClosedLoopOutput()
                        it.registerDifferentialClosedLoopProportionalOutput()
                        it.registerDifferentialClosedLoopReference()
                        it.registerDifferentialClosedLoopReferenceSlope()
                        it.registerDifferentialClosedLoopSlot()

                        it.registerAncillaryDeviceTemp()
                        it.registerExternalMotorTemp()
                        it.registerRawPulseWidthPosition(0.0)
                        it.registerRawQuadraturePosition(0.0)
                        it.registerRawPulseWidthVelocity(0.0)
                        it.registerRawQuadratureVelocity(0.0)
                    }
                } else if (bus == "canivore") {
                    talonFxsFDService.active.forEach {
                        talonFxFDService.grapherStatusFrameHz = 0.0;
                        it.registerAcceleration()
                        it.registerBridgeOutput()
                        it.registerDeviceTemp()
                        it.registerDifferentialAveragePosition()
                        it.registerDifferentialAverageVelocity()
                        it.registerDifferentialDifferencePosition()
                        it.registerDifferentialDifferenceVelocity()
                        it.registerDutyCycle()
                        it.registerForwardLimit()
                        it.registerReverseLimit()
                        it.registerIsProLicensed()
                        it.registerMotionMagicIsRunning()
                        it.registerMotorVoltage()
                        it.registerPosition()
                        it.registerProcessorTemp()
                        it.registerRotorPosition()
                        it.registerRotorVelocity()
                        it.registerStatorCurrent()
                        it.registerSupplyCurrent()
                        it.registerSupplyVoltage()
                        it.registerTorqueCurrent()
                        it.registerVelocity()

                        it.registerClosedLoopDerivativeOutput()
                        it.registerClosedLoopError()
                        it.registerClosedLoopFeedForward()
                        it.registerClosedLoopIntegratedOutput()
                        it.registerClosedLoopOutput()
                        it.registerClosedLoopProportionalOutput()
                        it.registerClosedLoopReference()
                        it.registerClosedLoopReferenceSlope()
                        it.registerClosedLoopSlot()
                        it.registerDifferentialClosedLoopDerivativeOutput()
                        it.registerDifferentialClosedLoopError()
                        it.registerDifferentialClosedLoopFeedForward()
                        it.registerDifferentialClosedLoopIntegratedOutput()
                        it.registerDifferentialClosedLoopOutput()
                        it.registerDifferentialClosedLoopProportionalOutput()
                        it.registerDifferentialClosedLoopReference()
                        it.registerDifferentialClosedLoopReferenceSlope()
                        it.registerDifferentialClosedLoopSlot()

                        it.registerAncillaryDeviceTemp()
                        it.registerExternalMotorTemp()
                        it.registerRawPulseWidthPosition(0.0)
                        it.registerRawQuadraturePosition(0.0)
                        it.registerRawPulseWidthVelocity(0.0)
                        it.registerRawQuadratureVelocity(0.0)
                    }
                }

            }
            else -> throw IllegalArgumentException()
        }
        return super.execute()
    }
}