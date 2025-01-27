package org.strykeforce.thirdcoast.swerve

import com.ctre.phoenix6.controls.MotionMagicVoltage
import edu.wpi.first.wpilibj.Preferences
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.swerve.FXSwerveModule
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readDouble
import org.strykeforce.thirdcoast.warn
import org.koin.core.component.inject

private const val DELAY = 20.0/1000.0
private const val WHEEL_PREF_KEY = "SwerveDrive/wheel.0"

private val logger = KotlinLogging.logger {  }
class P6SetAzimuthCommand(
    parent: Command?, key: String, toml: TomlTable
): AbstractCommand(parent, key, toml) {
    val swerve: SwerveDrive by inject()

    override val menu: String
        get() = formatMenu(swerve.swerveModules.map {
            var offset = (it as FXSwerveModule).azimuthPositionOffset
            (it as FXSwerveModule).azimuthTalon.getPosition().valueAsDouble - offset
        }.joinToString())

    override fun execute(): Command {
        if(!wheelZeroSaved()) {
            terminal.warn("swerve drive wheels are not zeroed")
            return super.execute()
        }

        swerve.swerveModules.forEach { it.loadAndSetAzimuthZeroReference() }

        while (true) {
            try {
                val setpoint = reader.readDouble(prompt())
                swerve.swerveModules.forEach {
                    var offset = (it as FXSwerveModule).azimuthPositionOffset
                    (it as FXSwerveModule).azimuthTalon.setControl(MotionMagicVoltage(setpoint + offset))
                }
                val swerveModule = swerve.swerveModules[0] as FXSwerveModule
                while (!swerveModule.onTarget(setpoint, swerveModule.azimuthPositionOffset)) Timer.delay(DELAY)
                Timer.delay(5* DELAY)
                logger.info {
                    swerve.swerveModules.map {
                        (it as FXSwerveModule).azimuthTalon.getPosition().valueAsDouble
                    }.joinToString()
                }
                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please enter a double")
            }
        }
    }

    private fun wheelZeroSaved() = Preferences.containsKey(WHEEL_PREF_KEY)
}