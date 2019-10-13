package org.strykeforce.thirdcoast.spark

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.SparkMaxService

private val SLOTS = listOf(0, 1, 2, 3)

class SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<Int>(parent, key, toml, SLOTS, SLOTS.map(Int::toString)) {

    private val sparkMaxService: SparkMaxService by inject()

    override val activeIndex: Int
        get() = sparkMaxService.activeSlot.id

    override fun setActive(index: Int) {
        sparkMaxService.activeSlot.id = index
        sparkMaxService.updateParams()
    }
}
