package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.ForwardLimitSourceValue
import com.ctre.phoenix6.signals.ForwardLimitSourceValue.*
import com.ctre.phoenix6.signals.ForwardLimitTypeValue
import com.ctre.phoenix6.signals.ForwardLimitTypeValue.*
import com.ctre.phoenix6.signals.ReverseLimitSourceValue
import com.ctre.phoenix6.signals.ReverseLimitTypeValue
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectFwdHardLimitSourceCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<ForwardLimitSourceValue>(parent, key, toml,
    listOf(LimitSwitchPin, RemoteTalonFX, RemoteCANifier),
    listOf("Talon FX pin", "Remote TalonFx", "Remote CANifier")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource.ordinal

    override fun setActive(index: Int) {
        val source = values[index]
        talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitSource = source
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
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

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource.ordinal

    override fun setActive(index: Int) {
        val source = values[index]
        talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitSource = source
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
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

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType.ordinal

    override fun setActive(index: Int) {
        val type = values[index]
        talonFxService.activeConfiguration.HardwareLimitSwitch.ForwardLimitType = type
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
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

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal

    override fun setActive(index: Int) {
        val type = values[index]
        talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
    }

}