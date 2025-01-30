package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.ForwardLimitSourceValue
import com.ctre.phoenix6.signals.ForwardLimitSourceValue.*
import com.ctre.phoenix6.signals.ForwardLimitTypeValue
import com.ctre.phoenix6.signals.ForwardLimitTypeValue.*
import com.ctre.phoenix6.signals.ReverseLimitSourceValue
import com.ctre.phoenix6.signals.ReverseLimitTypeValue
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService

class P6SelectFwdHardLimitSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ForwardLimitSourceValue>(parent, key, toml,
    listOf(LimitSwitchPin, RemoteTalonFX, RemoteCANifier, RemoteCANcoder, RemoteCANrange, RemoteCANdiS1, RemoteCANdiS2, Disabled),
    listOf("Talon FXS pin", "Remote TalonFx", "Remote CANifier", "Remote CANcoder", "Remote CANrange", "Remote CANdi S1", "Remote CANdi S2", "Disabled")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal
                    else if(bus == "canivore") return talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal
                    else if(bus == "canivore") return talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }

        }

    override fun setActive(index: Int) {
        val source = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
                    talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
                } else if(bus == "canivore") {
                    talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
                    talonFxFDService.active.forEach{ it.configurator.apply(talonFxFDService.activeConfiguration)}
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
                    talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration) }
                } else if(bus == "canivore") {
                    talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
                    talonFxsFDService.active.forEach{ it.configurator.apply(talonFxsFDService.activeConfiguration)}
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}

class P6SelectRevHardLimitSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ReverseLimitSourceValue>(parent, key, toml,
    listOf(ReverseLimitSourceValue.LimitSwitchPin, ReverseLimitSourceValue.RemoteTalonFX, ReverseLimitSourceValue.RemoteCANifier, ReverseLimitSourceValue.RemoteCANcoder, ReverseLimitSourceValue.RemoteCANrange, ReverseLimitSourceValue.RemoteCANdiS1, ReverseLimitSourceValue.RemoteCANdiS2, ReverseLimitSourceValue.Disabled),
    listOf("Talon FXS pin", "Remote TalonFX", "Remote CANifier", "Remote CANcoder", "Remote CANrange", "Remote CANdi S1", "Remote CANdi S2", "Disabled")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal
                    else if(bus == "canivore") return  talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal
                    else if(bus == "canivore") return  talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }


        }

    override fun setActive(index: Int) {
        val source = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
                    talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
                } else if(bus == "canivore") {
                    talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
                    talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration) }
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
                    talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration) }
                } else if(bus == "canivore") {
                    talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
                    talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration) }
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }

}

class P6SelectFwdHardLimitNormalCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ForwardLimitTypeValue>(parent, key, toml,
    listOf(NormallyOpen, NormallyClosed),
    listOf("Normally Open", "Normally Closed")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal
                    else if(bus == "canivore") return talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal
                    else if(bus == "canivore") return talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }

        }

    override fun setActive(index: Int) {
        val type = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
                    talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
                } else if (bus == "canivore") {
                    talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
                    talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration) }
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
                    talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration) }
                } else if (bus == "canivore") {
                    talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
                    talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration) }
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}

class P6SelectRevHardLimitNormalCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ReverseLimitTypeValue>(parent, key, toml,
    listOf(ReverseLimitTypeValue.NormallyOpen, ReverseLimitTypeValue.NormallyClosed),
    listOf("Normally Open", "Normally Closed")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")


    override val activeIndex: Int
        get() {
            when(device) {
                "fx" -> {
                    if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal
                    else if(bus == "canivore") return  talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal
                    else if(bus == "canivore") return  talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }


        }

    override fun setActive(index: Int) {
        val type = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
                    talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
                } else if(bus == "canivore") {
                    talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
                    talonFxFDService.active.forEach{ it.configurator.apply(talonFxFDService.activeConfiguration)}
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
                    talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration) }
                } else if(bus == "canivore") {
                    talonFxsFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
                    talonFxsFDService.active.forEach{ it.configurator.apply(talonFxsFDService.activeConfiguration)}
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }

}