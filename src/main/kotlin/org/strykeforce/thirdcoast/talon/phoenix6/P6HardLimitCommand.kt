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
    listOf(LimitSwitchPin, RemoteCANifier, RemoteTalonFX),
    listOf("Talon FX pin", "Remote CANifier", "Remote TalonFx")) {

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
    listOf(ReverseLimitSourceValue.LimitSwitchPin, ReverseLimitSourceValue.RemoteCANifier, ReverseLimitSourceValue.RemoteTalonFX),
    listOf("Talon FX pin", "Remote CANifier", "Remote TalonFX")) {

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
    listOf(NormallyClosed, NormallyOpen),
    listOf("Normally Closed", "Normally Open")) {

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
    listOf(ReverseLimitTypeValue.NormallyClosed, ReverseLimitTypeValue.NormallyOpen),
    listOf("Normally Closed", "Normally Open")) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType.ordinal

    override fun setActive(index: Int) {
        val type = values[index]
        talonFxService.activeConfiguration.HardwareLimitSwitch.ReverseLimitType = type
        talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration) }
    }

}