package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.Parameter
import org.strykeforce.thirdcoast.info

private val logger = KotlinLogging.logger {}

class SlotParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val param: Parameter by inject {
        parametersOf(this, toml.getTableOrEmpty("param"))
    }

    init {
        logger.debug { param }
    }

    override fun execute(): Command {
        val pidConfig = TalonSRXPIDSetConfiguration()
        terminal.info(pidConfig.toString("pid"))
        return super.execute()
    }

    private fun executeForInt() {
        val answer = param.readInt(reader, 42)
    }

//    val param = Parameter(toml.getTableOrEmpty("param"))
//
//    private val configTalon: (talon: TalonSRX) -> Unit  = when (key) {
//        "k_p" -> { t -> t.config_kP(0, 0.0, 10) }
//        "k_i" -> { t -> t.config_kI(0, 0.0, 10) }
//        else -> TODO("not implemented")
//    }
//
//    override fun execute(terminal: Terminal): Command {
//        when (key) {
//            "k_p" -> forEachTalon { t -> t.config_kP(0, 0.0, 10) }
//        }
//        terminal.info("key = $key")
//        return super.execute(terminal)
//    }
//
//    private fun forEachTalon(block: (talon: TalonSRX) -> Unit) {
//        talonService.active.forEach(block)
//    }
//
//    fun Set<TalonSRX>.configure() {}
//
}

