package org.strykeforce.thirdcoast.swerve

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.swerve.FXSwerveModule
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readBoolean
import org.strykeforce.thirdcoast.warn
import java.lang.IllegalArgumentException
import org.koin.core.component.inject

private val logger = KotlinLogging.logger {  }
class P6SaveZeroCommand(
    parent: Command?, key: String, toml: TomlTable
): AbstractCommand(parent, key, toml) {
    val swerve: SwerveDrive by inject()

    override fun execute(): Command {
        while (true) {
            try {
                if(reader.readBoolean(prompt(), false)) {
                    swerve.swerveModules.forEach {
                        val module = it as FXSwerveModule
                        logger.debug { "azimuth ${module.azimuthTalon.deviceID}: store zero, before=${module.azimuthTalon.position.valueAsDouble}, offset: ${module.azimuthPositionOffset}" }
                        module.loadAndSetAzimuthZeroReference()
                        logger.debug { "azimuth ${module.azimuthTalon.deviceID} store zero, after=${module.azimuthTalon.position.valueAsDouble}, offset: ${module.azimuthPositionOffset}" }
                    }
                    terminal.info("azimuth zero positions were saved")
                }
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("please enter a 'y' or 'n")
            }
        }
    }
}