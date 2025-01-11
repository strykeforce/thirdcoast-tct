package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.DifferentialType
import org.strykeforce.thirdcoast.device.DifferentialType.*
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectDifferentialTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<DifferentialType>(parent, key, toml,
    listOf(OPEN_LOOP, POSITION, VELOCITY, MOTION_MAGIC, FOLLOWER),
    listOf("Open Loop", "Position", "Velocity", "Motion Magic", "Follower")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.differentialType.ordinal
            else if(bus == "canivore") return talonFxFDService.differentialType.ordinal
            else throw IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val type = values[index]
        if(bus == "rio") {
            talonFxService.differentialType = type
        } else if(bus == "canivore") {
            talonFxFDService.differentialType = type
        } else throw IllegalArgumentException()
    }
}