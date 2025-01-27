package org.strykeforce.thirdcoast.swerve

import com.ctre.phoenix6.controls.MotionMagicVoltage
import edu.wpi.first.wpilibj.Timer
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.swerve.FXSwerveModule
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.readDouble
import org.strykeforce.thirdcoast.readInt
import org.strykeforce.thirdcoast.warn
import kotlin.math.abs
import org.koin.core.component.inject


private val logger = KotlinLogging.logger{}

class P6SelectAzimuthCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {
    override val menu: String
        get() = formatMenu(P6AdjustAzimuthCommand.active)

    override fun execute(): Command {
        while(true) {
            try {
                val azimuth = reader.readInt(prompt(), default = P6AdjustAzimuthCommand.active)
                if(!(0..3).contains(azimuth)) throw IllegalArgumentException()
                P6AdjustAzimuthCommand.active = azimuth
                return super.execute()
            } catch (e: Exception) {
                terminal.warn("Please eneter an integer")
            }
        }
    }
}

private const val GOOD_ENOUGH = 10.0/4096.0
private const val JOG = 250.0/4096.0
private const val DELAY = 5.0/1000.0
class P6AdjustAzimuthCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {
    private val swerve: SwerveDrive by inject()

    override val menu: String
        get() = formatMenu(
            (swerve.swerveModules[active] as FXSwerveModule).azimuthTalon.getPosition().valueAsDouble - (swerve.swerveModules[active] as FXSwerveModule).azimuthPositionOffset)

    override fun execute(): Command {
        val swerveModule = swerve.swerveModules[active] as FXSwerveModule
        var position = swerveModule.azimuthTalon.getPosition().valueAsDouble
        while(true) {
            try {
                position = reader.readDouble(prompt(), position)
                swerveModule.jogAround(position.toDouble(), swerveModule.azimuthPositionOffset)
                logger.info { "positioned wheel $active to $position" }
                return super.execute()
            } catch (e: java.lang.IllegalArgumentException) {
                terminal.warn("Please enter a Double")
            }
        }
    }
    companion object {
        var active: Int = 0
    }
}

private fun FXSwerveModule.jogAround(position: Double, offset: Double) {
    val positions = listOf(position - JOG, position + JOG, position)
    positions.forEach {
        this.azimuthTalon.setControl(MotionMagicVoltage(it + offset))
        while (!this.onTarget(it, offset)) Timer.delay(DELAY)
    }
}

internal fun FXSwerveModule.onTarget(target: Double, offset: Double, goodEnough: Double = GOOD_ENOUGH) =
    abs((target+offset) - (this.azimuthTalon.getPosition().valueAsDouble-offset)) < goodEnough