package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonService

private val SLOTS = listOf(0, 1, 2, 3)

class SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<Int>(parent, key, toml, SLOTS, SLOTS.map(Int::toString)) {

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    private val talonService: TalonService by inject()
    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() {
            if(type == "srx") return talonService.activeSlotIndex
            else if(type == "fx") return talonFxService.activeSlotIndex
            else throw IllegalArgumentException()
        }

        override fun setActive(index: Int) {
            if(type == "srx") talonService.activeSlotIndex = index
            else if(type == "fx") talonFxService.activeSlotIndex = index
        }
}