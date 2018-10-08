package org.strykeforce.thirdcoast.command

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.BACK
import org.strykeforce.thirdcoast.readRawMenu
import org.strykeforce.thirdcoast.warn

private val logger = KotlinLogging.logger {}

abstract class AbstractSelectCommand<T>(
    parent: Command?,
    key: String,
    toml: TomlTable,
    private val values: List<T>,
    private val labels: List<String>
) : AbstractCommand(parent, key, toml) {

    override val menu: String
        get() = "${super.menu} ${labels[activeIndex()]}"

    override fun execute(): Command {
        val writer = terminal.writer()
        var done = false
        while (!done) {
            labels.forEachIndexed { index, label ->
                writer.println(label.toMenu(index, index == activeIndex()))
            }
            val choice = terminal.readRawMenu(values.size, prompt("mode"))
            when (choice) {
                in 0..(values.size - 1) -> {
                    setActive(choice)
                    logger.info { "selected ${talonService.controlMode}" }
                    done = true
                }
                BACK -> return super.execute()
                else -> terminal.warn("Please select from the menu or <ENTER> to cancel")
            }
        }
        return super.execute()
    }

    abstract fun activeIndex(): Int

    abstract fun setActive(index: Int)
}
