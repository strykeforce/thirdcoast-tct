package org.strykeforce.thirdcoast.swerve

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

class SetAzimuthCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val swerve: SwerveDrive by inject()

    override val menu: String
        get() = formatMenu(swerve.wheels.map { it.azimuthTalon.getSelectedSensorPosition(0) }.joinToString())

    override fun execute(): Command {
        swerve.zeroAzimuthEncoders()

        while (true) {
            try {
                val setpoint = reader.readInt(prompt())
                swerve.wheels.forEach { it.setAzimuthPosition(setpoint) }
                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please enter an integer")
            }
        }
    }
}