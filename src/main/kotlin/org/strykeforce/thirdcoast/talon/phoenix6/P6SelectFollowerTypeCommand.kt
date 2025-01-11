package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.FollowerType
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectFollowerTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<FollowerType>(parent, key, toml,
    listOf(FollowerType.STRICT, FollowerType.STANDARD),
    listOf("Strict Follower", "Standard Follower")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeFollowerType.ordinal
            else if(bus == "canivore") return talonFxFDService.activeFollowerType.ordinal
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        val type = values[index]
        if(bus == "rio") talonFxService.activeFollowerType = type
        else if(bus == "canivore") talonFxFDService.activeFollowerType = type
        else throw IllegalArgumentException()
    }
}