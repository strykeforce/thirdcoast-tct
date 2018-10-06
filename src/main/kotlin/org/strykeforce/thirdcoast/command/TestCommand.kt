package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.info

class TestCommand(
    parent: Command?,
    key: String, toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    override fun execute(): Command {
        val writer = terminal.writer()
        val reader = terminal.reader()
        var done = false
        val prev = terminal.enterRawMode()
        while (!done) {
            writer.print("nonblocking> ")
            terminal.flush()
            val c = reader.read().toChar()

            when (c) {
                'b' -> done = true
                else -> terminal.info("c = $c")
            }
        }
        terminal.attributes = prev
        writer.println()
        return super.execute()
    }
}
