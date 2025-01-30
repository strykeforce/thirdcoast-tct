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
import org.koin.core.qualifier.named

private const val DELAY = 20.0/1000.0
private const val WHEEL_PREF_KEY = "SwerveDrive/wheel.0"

private val logger = KotlinLogging.logger {  }
class P6SetAzimuthCommand(
    parent: Command?, key: String, toml: TomlTable
): AbstractCommand(parent, key, toml) {
    val swerve: SwerveDrive by inject(named("FX"))
    val canifierSwerve: SwerveDrive by inject(named("FX-CANifier"))
    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override val menu: String
        get() {
            if (bus == "rio") return formatMenu(swerve.swerveModules.map {
                var offset = (it as FXSwerveModule).azimuthPositionOffset
                (it as FXSwerveModule).azimuthTalon.getPosition().valueAsDouble - offset
            }.joinToString())
            else return formatMenu(canifierSwerve.swerveModules.map {
                var offset = (it as FXSwerveModule).azimuthPositionOffset
                (it as FXSwerveModule).azimuthTalon.getPosition().valueAsDouble - offset
            }.joinToString())
        }

    override fun execute(): Command {
        if(!wheelZeroSaved()) {
            terminal.warn("swerve drive wheels are not zeroed")
            return super.execute()
        }

        if(bus == "rio") swerve.swerveModules.forEach { it.loadAndSetAzimuthZeroReference() }
        else canifierSwerve.swerveModules.forEach { it.loadAndSetAzimuthZeroReference() }

        while (true) {
            try {
                val setpoint = reader.readDouble(prompt())
                if(bus=="rio") {
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
                } else {
                    canifierSwerve.swerveModules.forEach {
                        var offset = (it as FXSwerveModule).azimuthPositionOffset
                        (it as FXSwerveModule).azimuthTalon.setControl(MotionMagicVoltage(setpoint + offset))
                    }
                    val swerveModule = canifierSwerve.swerveModules[0] as FXSwerveModule
                    while (!swerveModule.onTarget(setpoint, swerveModule.azimuthPositionOffset)) Timer.delay(DELAY)
                    Timer.delay(5* DELAY)
                    logger.info {
                        canifierSwerve.swerveModules.map {
                            (it as FXSwerveModule).azimuthTalon.getPosition().valueAsDouble
                        }.joinToString()
                    }
                }

                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please enter a double")
            }
        }
    }

    private fun wheelZeroSaved() = Preferences.containsKey(WHEEL_PREF_KEY)
}