package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.DifferentialSensorSourceValue
import com.ctre.phoenix6.signals.DifferentialSensorSourceValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService

private val SENSOR = listOf(
    Disabled,
    RemoteCANcoder,
    RemotePigeon2_Pitch,
    RemotePigeon2_Roll,
    RemotePigeon2_Yaw,
    RemoteTalonFX_Diff
)

private val LABELS = listOf(
    "Disabled",
    "Remote CANcoder",
    "Remote Pigeon2 Pitch",
    "Remote Pigeon2 Roll",
    "Remote Pigeon2 Yaw",
    "Remote TalonFX Differential"
)

class P6SelectDifferentialSensorSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<DifferentialSensorSourceValue>(parent, key, toml, SENSOR, LABELS) {

    private  val  talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.activeConfiguration.DifferentialSensors.DifferentialSensorSource.ordinal
                    else if(bus == "canivoer") return talonFxFDService.activeConfiguration.DifferentialSensors.DifferentialSensorSource.ordinal
                    else throw  IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.DifferentialSensors.DifferentialSensorSource.ordinal
                    else if(bus == "canivoer") return talonFxsFDService.activeConfiguration.DifferentialSensors.DifferentialSensorSource.ordinal
                    else throw  IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }

        }

    override fun setActive(index: Int) {
        val sensor = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.DifferentialSensors.DifferentialSensorSource = sensor
                    talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
                } else if(bus == "canivoer") {
                    talonFxFDService.activeConfiguration.DifferentialSensors.DifferentialSensorSource = sensor
                    talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration) }
                }
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.DifferentialSensors.DifferentialSensorSource = sensor
                    talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration) }
                } else if(bus == "canivoer") {
                    talonFxsFDService.activeConfiguration.DifferentialSensors.DifferentialSensorSource = sensor
                    talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration) }
                }
            }
            else -> throw IllegalArgumentException()
        }

    }
}