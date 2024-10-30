package org.strykeforce.thirdcoast.swerve

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.swerve.V6TalonSwerveModule
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readBoolean
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class SaveZeroCommand(
    parent: Command?, key: String, toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val swerve: SwerveDrive by inject()

    override fun execute(): Command {
        while (true) {
            try {
                if (reader.readBoolean(prompt(), false)) {
                    swerve.swerveModules.forEach {
                        val module = it as V6TalonSwerveModule

                        logger.debug { "azimuth ${module.azimuthTalon.deviceID}: store zero, before=${module.azimuthTalon.selectedSensorPosition}" }
                        module.storeAzimuthZeroReference()
                        logger.debug { "azimuth ${module.azimuthTalon.deviceID}: store zero, after=${module.azimuthTalon.selectedSensorPosition}" }
                    }
                    terminal.info("azimuth zero positions were saved")
                }
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a 'y' or 'n'")
            }
        }
    }
}