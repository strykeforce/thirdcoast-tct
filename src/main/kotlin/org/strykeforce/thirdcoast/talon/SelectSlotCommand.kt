package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

private val SLOTS = listOf(0, 1, 2, 3)

class SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<Int>(parent, key, toml, SLOTS, SLOTS.map(Int::toString)) {

    private val talonService: TalonService by inject()

    override val activeIndex
        get() = talonService.activeSlotIndex

    override fun setActive(index: Int) {
        talonService.activeSlotIndex = index
        TalonParameterCommand.reset = true
    }
}