package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode.*
import net.consensys.cava.toml.TomlTable
import org.jline.reader.EndOfFileException
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.*
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.command.toMenu
import org.strykeforce.thirdcoast.device.TalonService

class RunTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()

    override fun execute(): Command {
        var done = false

        while (!done) {
            try {
                val line = reader.readLine(prompt()).trim()
                if (line.isEmpty()) throw EndOfFileException()
                val setpoint = line.toDouble()
                talonService.active.forEach { it.set(talonService.controlMode, setpoint) }
            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }

}