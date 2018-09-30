package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable

class MenuCommand(parent: Command?, key: String, toml: TomlTable) : AbstractCommand(parent, key, toml) {

    private val _children = mutableListOf<Command>()
    override val children
        get() = _children.sortedWith(compareBy { it.order })

    fun addChild(child: Command) = _children.add(child)
}
