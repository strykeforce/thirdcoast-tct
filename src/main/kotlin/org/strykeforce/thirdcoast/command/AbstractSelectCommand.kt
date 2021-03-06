package org.strykeforce.thirdcoast.command

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.*

private val logger = KotlinLogging.logger {}

abstract class AbstractSelectCommand<T>(
    parent: Command?,
    key: String,
    toml: TomlTable,
    protected val values: List<T>,
    protected val labels: List<String>
) : AbstractCommand(parent, key, toml) {

    private val help: String? = toml.getString("help")

    override val menu: String
        get() = formatMenu(labels[activeIndex])

    override fun execute(): Command {
        val writer = terminal.writer()
        var done = false
        while (!done) {
            labels.forEachIndexed { index, label ->
                writer.println(label.toRawMenu(index, index == activeIndex))
            }
            val choice = terminal.readRawMenu(values.size, prompt())
            when (choice) {
                in 0..(values.size - 1) -> {
                    setActive(choice)
                    logger.info { "selected ${labels[choice]}" }
                    done = true
                }
                BACK -> return super.execute()
                else -> {
                    if (help != null) terminal.info(greedyWordwrap(help))
                    terminal.invalidMenuChoice()
                }
            }
        }
        return super.execute()
    }

    abstract val activeIndex: Int
    abstract fun setActive(index: Int)
}
