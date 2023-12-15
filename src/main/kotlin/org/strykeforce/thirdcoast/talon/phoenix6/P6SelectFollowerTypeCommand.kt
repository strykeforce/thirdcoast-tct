package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.FollowerType
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectFollowerTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<FollowerType>(parent, key, toml,
    listOf(FollowerType.STRICT, FollowerType.STANDARD),
    listOf("Strict Follower", "Standard Follower")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeFollowerType.ordinal

    override fun setActive(index: Int) {
        val type = values[index]
        talonFxService.activeFollowerType = type
    }
}