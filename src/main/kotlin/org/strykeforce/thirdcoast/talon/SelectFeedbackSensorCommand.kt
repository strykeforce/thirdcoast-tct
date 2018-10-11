package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.FeedbackDevice.*
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command

private val SENSORS = listOf(
    Analog,
    CTRE_MagEncoder_Absolute,
    CTRE_MagEncoder_Relative,
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
    "CTRE Absolute",
    "CTRE Relative",
    "Quad Encoder",
    "Pulse-width Encoded Position",
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

    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    private var config = talonService.activeConfiguration

    override fun activeIndex(): Int {
        if (reset) {
            config = talonService.activeConfiguration
            reset = false
        }

        return SENSORS.indexOf(
            when (pidIndex) {
                0 -> config.primaryPID.selectedFeedbackSensor
                1 -> config.auxilaryPID.selectedFeedbackSensor
                else -> IllegalStateException("no such pid index: $pidIndex")
            }
        )}

    override fun setActive(index: Int) {
        talonService.active.forEach { it.configSelectedFeedbackSensor(SENSORS[index], pidIndex, talonService.timeout) }
        reset = true
    }
}