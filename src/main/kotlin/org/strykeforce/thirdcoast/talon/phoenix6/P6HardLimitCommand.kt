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
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectFwdHardLimitSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ForwardLimitSourceValue>(parent, key, toml,
    listOf(LimitSwitchPin, RemoteTalonFX, RemoteCANifier),
    listOf("Talon FX pin", "Remote TalonFx", "Remote CANifier")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal
            else if(bus == "canivore") return talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal
            else throw IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val source = values[index]
        if(bus == "rio") {
            talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
            talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
        } else if(bus == "canivore") {
            talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
            talonFxFDService.active.forEach{ it.configurator.apply(talonFxFDService.activeConfiguration)}
        } else throw IllegalArgumentException()
    }
}

class P6SelectRevHardLimitSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ReverseLimitSourceValue>(parent, key, toml,
    listOf(ReverseLimitSourceValue.LimitSwitchPin, ReverseLimitSourceValue.RemoteTalonFX, ReverseLimitSourceValue.RemoteCANifier),
    listOf("Talon FX pin", "Remote TalonFX", "Remote CANifier")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal
            else if(bus == "canivore") return  talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        val source = values[index]
        if(bus == "rio") {
            talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
            talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
        } else if(bus == "canivore") {
            talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
            talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration) }
        } else throw IllegalArgumentException()
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

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal
            else if(bus == "canivore") return talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal
            else throw IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val type = values[index]
        if(bus == "rio") {
            talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
            talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
        } else if (bus == "canivore") {
            talonFxFDService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
            talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration) }
        } else throw IllegalArgumentException()
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

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal
            else if(bus == "canivore") return  talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal
            else throw IllegalArgumentException()

        }

    override fun setActive(index: Int) {
        val type = values[index]
        if(bus == "rio") {
            talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
            talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
        } else if(bus == "canivore") {
            talonFxFDService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
            talonFxFDService.active.forEach{ it.configurator.apply(talonFxFDService.activeConfiguration)}
        } else throw IllegalArgumentException()
    }

}