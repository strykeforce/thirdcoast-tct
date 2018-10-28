package org.strykeforce.thirdcoast.swerve

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readBoolean
import org.strykeforce.thirdcoast.warn

class SaveZeroCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val swerve: SwerveDrive by inject()

    override fun execute(): Command {
        while (true) {
            try {
                if (reader.readBoolean(prompt(), false)) {
                    swerve.saveAzimuthPositions()
                    swerve.zeroAzimuthEncoders()
                    terminal.info("azimuth zero positions were saved")
                }
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a 'y' or 'n'")
            }
        }
    }
}