package org.strykeforce.thirdcoast.spark

import com.revrobotics.CANSparkMax
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

private val logger = KotlinLogging.logger {}

class SelectIdleModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<CANSparkMax.IdleMode>(
    parent,
    key,
    toml,
    listOf(CANSparkMax.IdleMode.kBrake, CANSparkMax.IdleMode.kCoast),
    listOf("Brake", "Coast")
) {
    private val sparkMaxService: SparkMaxService by inject()

    override val activeIndex
        get() = values.indexOf(sparkMaxService.idleMode)

    override fun setActive(index: Int) {
        sparkMaxService.active.forEach{it.setIdleMode(values[index])}
        sparkMaxService.idleMode = values[index]
        logger.debug("Idle Mode: {}", sparkMaxService.idleMode)
        logger.debug("Index: {}, values[index]: {}", index, values[index])
    }
}