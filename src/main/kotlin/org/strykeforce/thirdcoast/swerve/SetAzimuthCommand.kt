package org.strykeforce.thirdcoast.swerve

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode
import edu.wpi.first.wpilibj.Preferences
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.swerve.V6TalonSwerveModule
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

private const val DELAY = 20.0 / 1000.0
private const val WHEEL_PREF_KEY = "SwerveDrive/wheel.0"

private val logger = KotlinLogging.logger {}

class SetAzimuthCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val swerve: SwerveDrive by inject(named("V6"))


    override val menu: String
        get() = formatMenu(swerve.swerveModules.map {
            (it as V6TalonSwerveModule).azimuthTalon.getSelectedSensorPosition(0)
        }.joinToString())

    override fun execute(): Command {
        if (!wheelZeroSaved()) {
            terminal.warn("swerve drive wheels are not zeroed")
            return super.execute()
        }

        swerve.swerveModules.forEach { it.loadAndSetAzimuthZeroReference() }

        while (true) {
            try {
                val setpoint = reader.readInt(prompt()).toDouble()
                swerve.swerveModules.forEach {
                    (it as V6TalonSwerveModule).azimuthTalon.set(
                        TalonSRXControlMode.MotionMagic,
                        setpoint
                    )
                }
                val swerveModule = swerve.swerveModules[0] as V6TalonSwerveModule
                while (!swerveModule.onTarget(setpoint)) Timer.delay(DELAY)
                Timer.delay(5 * DELAY)
                logger.info {
                    swerve.swerveModules.map {
                        (it as V6TalonSwerveModule).azimuthTalon.getSelectedSensorPosition(
                            0
                        )
                    }
                        .joinToString()
                }
                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please enter an integer")
            }
        }
    }
}

private fun wheelZeroSaved() = Preferences.containsKey(WHEEL_PREF_KEY)

