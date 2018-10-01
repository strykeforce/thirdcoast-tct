package org.strykeforce.thirdcoast.command

import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.readIntList

class TestCommand(
    parent: Command?,
    key: String, toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        val reader = LineReaderBuilder.builder().terminal(terminal).build()
        val writer = terminal.writer()
        writer.println(reader.readIntList("ints> ", listOf(10, 11, 12)))

        val talon = talonService.get(1)
        val config = TalonSRXConfiguration()
        talon.getAllConfigs(config)
        writer.println(config.toString("talon1"))
        return super.execute()
    }
}


//for (f in TalonSRX::class.functions) {
//    writer.print("${f.visibility} ${f.name} - ")
//    for (p in f.parameters)
//        writer.print("${p.type} ")
//    writer.println()
//}
