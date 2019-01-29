package org.strykeforce.thirdcoast.command

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.*

const val DOUBLE_FORMAT_3 = "%7.3f"
const val DOUBLE_FORMAT_4 = "%8.4f"

private val logger = KotlinLogging.logger {}

interface Parameter {
    val name: String
    val type: Type
    val help: String
    fun readInt(reader: LineReader, default: Int = 0): Int
    fun readDouble(reader: LineReader, default: Double = 0.0): Double
    fun readBoolean(reader: LineReader, default: Boolean = false): Boolean

    enum class Type { DOUBLE, INTEGER, BOOLEAN }
}


open class AbstractParameter(command: Command, toml: TomlTable) : Parameter {

    override val name = toml.getString("name") ?: "NO NAME"
    override val type = Parameter.Type.valueOf(toml.getString("type") ?: "NULL")
    override val help = toml.getString("help") ?: "NO DESCRIPTION"

    private val range = toml.getArray("range")?.let { it.getDouble(0).rangeTo(it.getDouble(1)) }
    val prompt = command.prompt()

    override fun readInt(reader: LineReader, default: Int): Int {
        while (true) {
            try {
                return checkRange(reader.readInt(prompt, default))
            } catch (e: IllegalArgumentException) {
                invalidInput(reader.terminal)
            }
        }
    }


    override fun readDouble(reader: LineReader, default: Double): Double {
        while (true) {
            try {
                return checkRange(reader.readDouble(prompt, default))
            } catch (e: IllegalArgumentException) {
                invalidInput(reader.terminal)
            }
        }
    }

    override fun readBoolean(reader: LineReader, default: Boolean): Boolean {
        while (true) {
            try {
                return reader.readBoolean(prompt, default)
            } catch (e: IllegalArgumentException) {
                invalidInput(reader.terminal)
            }
        }
    }

    private fun checkRange(value: Double): Double {
        if (range == null) return value
        if (range.contains(value)) {
            return value
        } else {
            logger.debug { "range check failed: $range" }
            throw java.lang.IllegalArgumentException()
        }
    }


    private fun checkRange(value: Int) = checkRange(value.toDouble()).toInt()

    private fun invalidInput(terminal: Terminal) {
        val messageType = when (type) {
            Parameter.Type.INTEGER -> "integer"
            Parameter.Type.DOUBLE -> "number"
            Parameter.Type.BOOLEAN -> "'y' or 'n'"
        }
        val messageRange = if (range != null) " in range (${range.start} - ${range.endInclusive})" else ""
        terminal.info(greedyWordwrap(help))
        terminal.warn("Please enter a $messageType$messageRange")
    }

    override fun toString(): String {
        return "Parameter(name='$name', type='$type',  help='$help')"
    }
}