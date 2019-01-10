package org.strykeforce.thirdcoast

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import edu.wpi.first.wpilibj.DigitalOutput
import edu.wpi.first.wpilibj.Servo
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.koin.dsl.module.module
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CanifierService
import org.strykeforce.thirdcoast.device.DigitalOutputService
import org.strykeforce.thirdcoast.device.ServoService
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.swerve.SwerveDriveConfig
import org.strykeforce.thirdcoast.swerve.Wheel
import org.strykeforce.thirdcoast.telemetry.TelemetryController
import org.strykeforce.thirdcoast.telemetry.TelemetryService
import org.strykeforce.thirdcoast.telemetry.grapher.ClientHandler
import java.net.DatagramSocket
import java.util.function.Function

private const val CLIENT_PORT = 5801
private const val SERVER_PORT = 5800

val tctModule = module {

    factory { ClientHandler(CLIENT_PORT, DatagramSocket()) }

    single { TelemetryService(Function { inventory -> TelemetryController(inventory, get(), SERVER_PORT) }) }

    single { TalonService(get()) { id -> TalonSRX(id) } }

    single { ServoService { id -> Servo(id) } }

    single { DigitalOutputService { id -> DigitalOutput(id) } }

    single { CanifierService(get()) { id -> CANifier(id) } }

    single { (command: Command) -> Shell(command, get()) }

    single<Terminal> { TerminalBuilder.terminal() }

    single<LineReader>(createOnStart = true) { LineReaderBuilder.builder().terminal(get()).build() }

}

val swerveModule = module {

    single {
        SwerveDriveConfig().apply {
            wheels = getWheels()
        }
    }

    single { SwerveDrive(get()) }

}

private fun getWheels(): Array<Wheel> {
    val azimuthConfig = TalonSRXConfiguration().apply {
        primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative
        continuousCurrentLimit = 10
        peakCurrentLimit = 0
        peakCurrentDuration = 0
        slot0.apply {
            kP = 10.0
            kI = 0.0
            kD = 100.0
            kF = 1.0
            integralZone = 0
            allowableClosedloopError = 0
        }
        motionAcceleration = 10_000
        motionCruiseVelocity = 800
    }


    val driveConfig = TalonSRXConfiguration().apply {
        primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative
        continuousCurrentLimit = 40
        peakCurrentLimit = 0
        peakCurrentDuration = 0
    }

    val timeout = 10

    return Array(4) {
        Wheel(
            TalonSRX(it).apply { configAllSettings(azimuthConfig, timeout) },
            TalonSRX(it + 10).apply {
                configAllSettings(driveConfig, timeout)
                setNeutralMode(NeutralMode.Brake)
            }, 0.0
        )
    }
}
