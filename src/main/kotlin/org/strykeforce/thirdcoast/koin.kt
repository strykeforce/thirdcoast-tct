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


val tctModule = module {

    single { TalonService { id -> TalonSRX(id) } }

    single { ServoService { id -> Servo(id) } }

    single { (command: Command) -> Shell(command, get(), get()) }

    single<Terminal> { TerminalBuilder.terminal() }

    single<LineReader> { LineReaderBuilder.builder().terminal(get()).build() }

}
