package org.strykeforce.thirdcoast.swerve

import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

class SelectAzimuthCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override val menu: String
        get() = formatMenu(AdjustAzimuthCommand.active)

    override fun execute(): Command {
        while (true) {
            try {
                val azimuth = reader.readInt(prompt(), default = AdjustAzimuthCommand.active)
                if (!(0..3).contains(azimuth)) throw IllegalArgumentException()
                AdjustAzimuthCommand.active = azimuth
                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please enter an integer")
            }
        }
    }
}


private const val GOOD_ENOUGH = 10
private const val JOG = 250
private const val DELAY = 5.0 / 1000.0

class AdjustAzimuthCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    val swerve: SwerveDrive by inject()

    override val menu: String
        get() = formatMenu(swerve.wheels[active].azimuthTalon.getSelectedSensorPosition(0))

    override fun execute(): Command {
        val wheel = swerve.wheels[active]
        var position = wheel.azimuthTalon.getSelectedSensorPosition(0)
        while (true) {
            try {
                position = reader.readInt(prompt(), position)
                wheel.jogAround(position)
                logger.info { "positioned wheel $active to $position" }
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter an integer")
            }
        }
    }


    companion object {
        var active: Int = 0
    }
}

private fun Wheel.jogAround(position: Int) {
    val positions = listOf(position - JOG, position + JOG, position)
    positions.forEach {
        this.setAzimuthPosition(it)
        while (!this.onTarget(it)) Timer.delay(DELAY)
    }
}

internal fun Wheel.onTarget(target: Int, goodEnough: Int = GOOD_ENOUGH) =
    Math.abs(target - this.azimuthTalon.getSelectedSensorPosition(0)) < goodEnough