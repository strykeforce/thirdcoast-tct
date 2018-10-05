package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.ControlMode.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.BACK
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readMenu
import org.strykeforce.thirdcoast.toMenu
import org.strykeforce.thirdcoast.warn

class RunTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val controlModes = listOf(PercentOutput, MotionMagic, Velocity, Position, Follower)
    private val controlModeLabels = listOf("Percent Output", "Motion Magic", "Velocity", "Position", "Follower")

    override fun execute(): Command {
        val writer = terminal.writer()
        var done = false
        while (!done) {
            controlModeLabels.forEachIndexed { index, mode -> writer.println(mode.toMenu(index)) }
            val choice = readControlModeMenu()
            when (choice) {
                in 1..controlModes.size -> {
                    talonService.controlMode = controlModes[choice - 1]
                    done = true
                }
                BACK -> return super.execute()
                else -> terminal.warn("Please enter a valid control mode")
            }
        }

        done = false
        while (!done) {

        }

        return super.execute()
    }

    private fun readControlModeMenu(): Int {
        val default = (controlModes.indexOf(talonService.controlMode) + 1).toString()
        return reader.readMenu(controlModes.size, this.prompt("control mode"), default, quit = false)
    }
}