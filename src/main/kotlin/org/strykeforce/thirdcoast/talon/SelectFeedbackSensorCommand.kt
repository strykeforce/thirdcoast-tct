package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.FeedbackDevice.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
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
    Tachometer
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
    "Tachometer"
)

class SelectFeedbackSensorCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<FeedbackDevice>(parent, key, toml, SENSORS, LABELS) {

    companion object {
        var reset = true
    }

    private val talonService: TalonService by inject()

    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    private var config = talonService.activeConfiguration

    override val activeIndex: Int
        get() {
            if (reset) {
                config = talonService.activeConfiguration
                reset = false
            }

            val sensor = when (pidIndex) {
                0 -> config.primaryPID.selectedFeedbackSensor
                else -> config.auxilaryPID.selectedFeedbackSensor
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
        talonService.active.forEach { it.configSelectedFeedbackSensor(values[index], pidIndex, talonService.timeout) }
        reset = true
    }
}