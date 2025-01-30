package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*
import org.strykeforce.thirdcoast.device.SetpointType.*

class P6SelectModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<SetpointType>(parent, key, toml,
    listOf(OPEN_LOOP, POSITION, VELOCITY, MOTION_MAGIC, DIFFERENTIAL, FOLLOWER, NEUTRAL, MUSIC),
    listOf("Open Loop", "Position", "Velocity", "Motion Magic", "Differential", "Follower", "Neutral Output", "Music Tone")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.setpointType.ordinal
                    if(bus == "canivore") return talonFxFDService.setpointType.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.setpointType.ordinal
                    if(bus == "canivore") return talonFxsFDService.setpointType.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }

        }

    override fun setActive(index: Int) {
        val mode = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") talonFxService.setpointType = mode
                else if(bus == "canivore") talonFxFDService.setpointType = mode
                else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") talonFxsService.setpointType = mode
                else if(bus == "canivore") talonFxsFDService.setpointType = mode
                else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }


    }
}