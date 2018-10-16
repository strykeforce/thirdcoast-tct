package org.strykeforce.thirdcoast

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Servo
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.koin.dsl.module.module
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.ServoService
import org.strykeforce.thirdcoast.device.TalonService
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

    single { (command: Command) -> Shell(command, get()) }

    single<Terminal> { TerminalBuilder.terminal() }

    single<LineReader>(createOnStart = true) { LineReaderBuilder.builder().terminal(get()).build() }

}
