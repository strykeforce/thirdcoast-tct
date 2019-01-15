package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

class SelectVelocityMeasurmentPeriodCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<VelocityMeasPeriod>(
    parent,
    key,
    toml,
    listOf(Period_1Ms, Period_2Ms, Period_5Ms, Period_10Ms, Period_20Ms, Period_50Ms, Period_100Ms),
    listOf("1 ms", "2 ms", "5 ms", "10 ms", "20 ms", "50 ms", "100 ms")
) {
    private val talonService: TalonService by inject()
    override val activeIndex
        get() = values.indexOf(talonService.activeConfiguration.velocityMeasurementPeriod)

    override fun setActive(index: Int) {
        val period = values[index]
        talonService.active.forEach { it.configVelocityMeasurementPeriod(period, talonService.timeout) }
        talonService.activeConfiguration.velocityMeasurementPeriod = period
    }
}
