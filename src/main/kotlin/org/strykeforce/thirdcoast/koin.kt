package org.strykeforce.thirdcoast

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Servo
import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.koin.dsl.module.module
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.Parameter
import org.strykeforce.thirdcoast.command.ParameterImpl


val tctModule = module {
    single<DeviceFactory<TalonSRX>>(name = "Talon") { TalonFactoryImpl() }
    single<DeviceService<TalonSRX>>(name = "Talon") {
        TalonServiceImpl(
            get(name = "Talon")
        )
    }

    single<DeviceFactory<Servo>>(name = "Servo") { ServoFactoryImpl() }
    single<DeviceService<Servo>>(name = "Servo") {
        ServoServiceImpl(
            get(name = "Servo")
        )
    }

    factory<Parameter> { (command: Command, toml: TomlTable) -> ParameterImpl(command, toml) }

    single<Terminal> { TerminalBuilder.terminal() }

    single<LineReader> { LineReaderBuilder.builder().terminal(get()).build() }

}
