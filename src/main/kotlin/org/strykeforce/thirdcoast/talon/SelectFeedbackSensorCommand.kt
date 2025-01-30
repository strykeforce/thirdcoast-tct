package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.FeedbackDevice.*
import com.ctre.phoenix.motorcontrol.can.BaseTalonConfiguration
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
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
    None,
    CTRE_MagEncoder_Absolute,
    CTRE_MagEncoder_Relative
)
private val LABELS = listOf(
    "Analog",
    "Quad Encoder",
    "Pulse-width Encoder",
    "Remote Sensor 0",
    "Remote Sensor 1",
    "Sensor Difference",
    "Sensor Sum",
    "Software Emulated Sensor",
    "Tachometer",
    "None",
    "CTRE Absolute",
    "CTRE Relative"
)

class SelectFeedbackSensorCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<FeedbackDevice>(parent, key, toml, SENSORS, LABELS) {

    private val talonService: TalonService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val activeIndex: Int
        get() {
            val config: BaseTalonConfiguration
            config = talonService.activeConfiguration

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

        config = talonService.activeConfiguration
        talonService.active.forEach { it.configSelectedFeedbackSensor(sensor, pidIndex, talonService.timeout) }

        when (pidIndex) {
            0 -> config.primaryPID.selectedFeedbackSensor = sensor
            else -> config.auxiliaryPID.selectedFeedbackSensor = sensor
        }

    }
}