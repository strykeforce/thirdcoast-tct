package org.strykeforce.thirdcoast.swerve

import edu.wpi.first.wpilibj.Preferences
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
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

    val swerve: SwerveDrive by inject()

    override val menu: String
        get() = formatMenu(swerve.wheels.map { it.azimuthTalon.getSelectedSensorPosition(0) }.joinToString())

    override fun execute(): Command {
        if (!wheelZeroSaved()) {
            terminal.warn("swerve drive wheels are not zeroed")
            return super.execute()
        }

        swerve.zeroAzimuthEncoders()

        while (true) {
            try {
                val setpoint = reader.readInt(prompt())
                swerve.wheels.forEach { it.setAzimuthPosition(setpoint) }
                while (!swerve.wheels[0].onTarget(setpoint)) Timer.delay(DELAY)
                Timer.delay(5 * DELAY)
                logger.info { swerve.wheels.map { it.azimuthTalon.getSelectedSensorPosition(0) }.joinToString() }
                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please enter an integer")
            }
        }
    }
}

private fun wheelZeroSaved() = Preferences.getInstance().containsKey(WHEEL_PREF_KEY)

