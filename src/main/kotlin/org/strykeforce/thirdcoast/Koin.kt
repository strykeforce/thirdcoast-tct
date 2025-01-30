package org.strykeforce.thirdcoast

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.sensors.PigeonIMU
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.hardware.TalonFXS
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj.DigitalOutput
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Servo
import edu.wpi.first.wpilibj.Solenoid
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module
//import org.koin.dsl.module.module
import org.strykeforce.swerve.*
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*
import org.strykeforce.telemetry.TelemetryController
import org.strykeforce.telemetry.TelemetryService
import org.strykeforce.telemetry.grapher.ClientHandler
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.util.function.Function

private const val CLIENT_PORT = 5801
private const val SERVER_PORT = 5800

val tctModule = module {

    factory { ClientHandler(CLIENT_PORT, DatagramSocket()) }

    single {
        TelemetryService(Function { inventory ->
            TelemetryController(
                inventory,
                get(),
                InetSocketAddress(SERVER_PORT)
            )
        })
    }

    single { TalonService(get()) { id -> TalonSRX(id) } }

    single { TalonFxService(get()) { id -> com.ctre.phoenix6.hardware.TalonFX(id)} }

    single {TalonFxFDService(get()) {id -> com.ctre.phoenix6.hardware.TalonFX(id, "*")} }

    single {TalonFxsService(get()) {id -> com.ctre.phoenix6.hardware.TalonFXS(id)} }

    single {TalonFXsFDService(get()) {id -> TalonFXS(id, "*")} }

    single { ServoService { id -> Servo(id) } }

    single { SolenoidService { id -> Solenoid(PneumaticsModuleType.CTREPCM, id) } }

    single { DigitalOutputService { id -> DigitalOutput(id) } }

    single { CanifierService(get()) { id -> CANifier(id) } }

    single { CancoderService(get()) {id -> CANcoder(id, "rio")} }

    single { CancoderFDService(get()) {id -> CANcoder(id, "*")} }

    single { PigeonService(get()) { id -> PigeonIMU(id) } }

    single { (command: Command) -> Shell(command, get()) }

    single<Terminal> { TerminalBuilder.terminal() }

    single<LineReader>(createdAtStart = true) { LineReaderBuilder.builder().terminal(get()).build() }

}

val swerveModule = module {

    single (named("V6")){ SwerveDrive(*getSwerveModules()) }

}

val fxSwerveModule = module {
    single (named("FX")) {SwerveDrive(*getFXSwerveModules())}
}

val fxCANivoreSwerveModule = module {
    single (named("FX-CANifier")) {SwerveDrive(*getCANivoreSwerveModules())}
}

private fun getSwerveModules() = Array<SwerveModule>(4) { i -> getSwerveModule(i) }

private val moduleBuilder = V6TalonSwerveModule.V6Builder().driveGearRatio(1.0).wheelDiameterInches(1.0)
    .driveMaximumMetersPerSecond(1.0)

private fun getSwerveModule(i: Int) : V6TalonSwerveModule {
    val location = when (i) {
        0 -> Translation2d(1.0, 1.0)
        1 -> Translation2d(1.0, -1.0)
        2 -> Translation2d(-1.0, 1.0)
        else -> Translation2d(-1.0, -1.0)
    }
    return moduleBuilder.azimuthTalon(TalonSRX(i)).driveTalon(com.ctre.phoenix6.hardware.TalonFX(i + 10))
        .wheelLocationMeters(location).build()
}

private fun getFXSwerveModules() = Array<SwerveModule>(4) {i -> getFXSwerveModule(i)}
private fun getCANivoreSwerveModules() = Array<SwerveModule>(4) {i -> getCANivoreSwerveModule(i) }
private val fxModuleBuilder = FXSwerveModule.FXBuilder().driveGearRatio(1.0).wheelDiameterInches(1.0).driveMaximumMetersPerSecond(1.0)

private fun getFXSwerveModule(i:Int): FXSwerveModule {
    val location = when(i) {
        0 -> Translation2d(1.0, 1.0)
        1 -> Translation2d(1.0, -1.0)
        2 -> Translation2d(-1.0, 1.0)
        else -> Translation2d(-1.0, -1.0)
    }
    return fxModuleBuilder.azimuthTalon(TalonFXS(i)).driveTalon(TalonFX(i+10)).wheelLocationMeters(location).build()
}

private fun getCANivoreSwerveModule(i: Int): FXSwerveModule {
    val location = when(i) {
        0 -> Translation2d(1.0, 1.0)
        1 -> Translation2d(1.0, -1.0)
        2 -> Translation2d(-1.0, 1.0)
        else -> Translation2d(-1.0, -1.0)
    }
    return fxModuleBuilder.azimuthTalon(TalonFXS(i,"*")).driveTalon(TalonFX(i+10,"*")).wheelLocationMeters(location).build()
}


/*
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
            integralZone = 0.0
            allowableClosedloopError = 0.0
        }
        motionAcceleration = 10_000.0
        motionCruiseVelocity = 800.0
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
 */
