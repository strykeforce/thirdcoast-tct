package org.strykeforce.thirdcoast.command

import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import net.consensys.cava.toml.TomlTable

class TestCommand(
    parent: Command?,
    key: String, toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        val writer = terminal.writer()

        talonService.active.forEach {
            val config = TalonSRXConfiguration()
            it.getAllConfigs(config)
            writer.println(config.toString(it.deviceID.toString()))
        }
        return super.execute()
    }
}


//for (f in TalonSRX::class.functions) {
//    writer.print("${f.visibility} ${f.name} - ")
//    for (p in f.parameters)
//        writer.print("${p.type} ")
//    writer.println()
//}
