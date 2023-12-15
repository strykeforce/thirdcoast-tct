package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

private val SLOTS = listOf(0,1,2)

class P6SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<Int>(parent, key, toml, SLOTS, SLOTS.map(Int::toString)) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeSlotIndex

    override fun setActive(index: Int) {
        talonFxService.activeSlotIndex = index
    }
}