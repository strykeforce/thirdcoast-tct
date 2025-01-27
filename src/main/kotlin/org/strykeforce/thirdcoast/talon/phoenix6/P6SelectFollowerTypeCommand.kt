package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.*

class P6SelectFollowerTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<FollowerType>(parent, key, toml,
    listOf(FollowerType.STRICT, FollowerType.STANDARD),
    listOf("Strict Follower", "Standard Follower")) {

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
                    if(bus == "rio") return talonFxService.activeFollowerType.ordinal
                    else if(bus == "canivore") return talonFxFDService.activeFollowerType.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeFollowerType.ordinal
                    else if(bus == "canivore") return talonFxsFDService.activeFollowerType.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }
        }

    override fun setActive(index: Int) {
        val type = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") talonFxService.activeFollowerType = type
                else if(bus == "canivore") talonFxFDService.activeFollowerType = type
                else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") talonFxsService.activeFollowerType = type
                else if(bus == "canivore") talonFxsFDService.activeFollowerType = type
                else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }
    }
}