package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.FeedbackDevice.*
import com.ctre.phoenix.motorcontrol.can.BaseTalonConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.LegacyTalonFxService
import org.strykeforce.thirdcoast.device.TalonService

private val SENSORS = listOf(
    Analog,
    QuadEncoder,
    PulseWidthEncodedPosition,
    RemoteSensor0,
    RemoteSensor1,
    SensorDifference,
    SensorSum,
    SoftwareEmulatedSensor,
    Tachometer,
    IntegratedSensor,
    None
)
private val LABELS = listOf(
    "Analog",
    "CTRE Relative or Quad Encoder",
    "CTRE Absolute or Pulse-width Encoder",
    "Remote Sensor 0",
    "Remote Sensor 1",
    "Sensor Difference",
    "Sensor Sum",
    "Software Emulated Sensor",
    "Tachometer",
    "Integrated Sensor",
    "None"
)

class SelectFeedbackSensorCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<FeedbackDevice>(parent, key, toml, SENSORS, LABELS) {

    private val talonService: TalonService by inject()
    private val legacyTalonFxService: LegacyTalonFxService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val activeIndex: Int
        get() {
            val config: BaseTalonConfiguration
            if (type == "srx") config = talonService.activeConfiguration
            else if (type == "fx") config = legacyTalonFxService.activeConfiguration
            else throw IllegalArgumentException()

            val sensor = when (pidIndex) {
                0 -> config.primaryPID.selectedFeedbackSensor
                else -> config.auxiliaryPID.selectedFeedbackSensor
            }

            return values.indexOf(
                when (sensor) {
                    CTRE_MagEncoder_Absolute -> PulseWidthEncodedPosition
                    CTRE_MagEncoder_Relative -> QuadEncoder
                    else -> sensor
                }
            )
        }

    override fun setActive(index: Int) {
        val sensor = values[index]
        val config: BaseTalonConfiguration
        if (type == "srx") {
            config = talonService.activeConfiguration
            talonService.active.forEach { it.configSelectedFeedbackSensor(sensor, pidIndex, talonService.timeout) }
        } else if (type == "fx") {
            config = legacyTalonFxService.activeConfiguration
            legacyTalonFxService.active.forEach { it.configSelectedFeedbackSensor(sensor, pidIndex, legacyTalonFxService.timeout) }
        } else throw IllegalArgumentException()
        when (pidIndex) {
            0 -> config.primaryPID.selectedFeedbackSensor = sensor
            else -> config.auxiliaryPID.selectedFeedbackSensor = sensor
        }

    }
}