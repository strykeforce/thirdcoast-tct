package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

private val SLOTS = listOf(0,1,2)

class P6SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<Int>(parent, key, toml, SLOTS, SLOTS.map(Int::toString)) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeSlotIndex
            else if(bus == "canivore") return talonFxFDService.activeSlotIndex
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        if(bus == "rio") talonFxService.activeSlotIndex = index
        else if(bus == "canivore") talonFxFDService.activeSlotIndex = index
        else throw IllegalArgumentException()
    }
}