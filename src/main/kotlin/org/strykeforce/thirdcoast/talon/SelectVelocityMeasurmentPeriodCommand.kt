package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonService

class SelectVelocityMeasurmentPeriodCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<SensorVelocityMeasPeriod>(
    parent,
    key,
    toml,
    listOf(Period_1Ms, Period_2Ms, Period_5Ms, Period_10Ms, Period_20Ms, Period_50Ms, Period_100Ms),
    listOf("1 ms", "2 ms", "5 ms", "10 ms", "20 ms", "50 ms", "100 ms")
) {
    private val talonService: TalonService by inject()
    private val talonFxService: TalonFxService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    override val activeIndex: Int
        get() {
            if(type == "srx") return values.indexOf(talonService.activeConfiguration.velocityMeasurementPeriod)
            else if(type == "fx") return values.indexOf(talonFxService.activeConfiguration.velocityMeasurementPeriod)
            else throw IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val period = values[index]
        if(type == "srx"){
            talonService.active.forEach { it.configVelocityMeasurementPeriod(period, talonService.timeout) }
            talonService.activeConfiguration.velocityMeasurementPeriod = period
        }else if(type == "fx"){
            talonFxService.active.forEach { it.configVelocityMeasurementPeriod(period, talonFxService.timeout) }
            talonFxService.activeConfiguration.velocityMeasurementPeriod = period
        }
    }
}
