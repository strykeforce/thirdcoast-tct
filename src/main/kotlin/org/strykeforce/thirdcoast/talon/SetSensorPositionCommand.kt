package org.strykeforce.thirdcoast.talon

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

private val logger = KotlinLogging.logger {}

class SetSensorPositionCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonService.timeout
    private val param = TalonParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val menu: String
        get() {
            return formatMenu(talonService.active.map { it.getSelectedSensorPosition(pidIndex) }.joinToString())
        }

    override fun execute(): Command {
        val default = 0

        val paramValue = param.readInt(reader, default)
        talonService.active.forEach { it.setSelectedSensorPosition(paramValue.toDouble(), pidIndex, timeout) }
        logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
        return super.execute()
    }

}