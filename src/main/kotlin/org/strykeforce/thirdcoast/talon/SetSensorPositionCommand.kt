package org.strykeforce.thirdcoast.talon

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command

private val logger = KotlinLogging.logger {}

class SetSensorPositionCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val timeout = talonService.timeout
    private val param = CtreParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val menu: String
        get() = formatMenu(talonService.active.map { it.getSelectedSensorPosition(pidIndex) }.joinToString())

    override fun execute(): Command {
        val default = 0

        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { it.setSelectedSensorPosition(paramValue, pidIndex, timeout) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }

        return super.execute()
    }

}