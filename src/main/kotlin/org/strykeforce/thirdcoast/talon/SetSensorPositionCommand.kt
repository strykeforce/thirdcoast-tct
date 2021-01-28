package org.strykeforce.thirdcoast.talon

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonService

private val logger = KotlinLogging.logger {}

class SetSensorPositionCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val talonService: TalonService by inject()
    private val talonFxService: TalonFxService by inject()
    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")
    private val timeout = talonService.timeout
    private val param = TalonParameter.create(this, toml.getString("param") ?: "UNKNOWN")
    private val pidIndex = toml.getLong("pid")?.toInt() ?: 0

    override val menu: String
        get() {
            if(type == "srx") return formatMenu(talonService.active.map { it.getSelectedSensorPosition(pidIndex) }.joinToString())
            else if(type == "fx") return formatMenu(talonFxService.active.map{ it.getSelectedSensorPosition(pidIndex)}.joinToString())
            else throw IllegalArgumentException()
        }

    override fun execute(): Command {
        val default = 0

        val paramValue = param.readInt(reader, default)
        if(type == "srx") {
            talonService.active.forEach { it.setSelectedSensorPosition(paramValue.toDouble(), pidIndex, timeout) }
            logger.debug { "set ${talonService.active.size} talon ${param.name}: $paramValue" }
        }
        else if(type == "fx") {
            talonFxService.active.forEach { it.setSelectedSensorPosition(paramValue.toDouble(), pidIndex, talonFxService.timeout) }
            logger.debug { "set ${talonFxService.active.size} talonfx ${param.name}: $paramValue" }
        }
        return super.execute()
    }

}