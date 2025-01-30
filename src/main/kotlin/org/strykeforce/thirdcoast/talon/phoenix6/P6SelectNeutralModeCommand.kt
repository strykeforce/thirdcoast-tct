package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.NeutralModeValue
import com.ctre.phoenix6.signals.NeutralModeValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService


private val MODE = listOf(
    Coast,
    Brake
)

private val LABELS = listOf(
    "Coast",
    "Brake"
)

class P6SelectNeutralModeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<NeutralModeValue>(parent, key, toml, MODE, LABELS) {

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
                    if(bus == "rio") return talonFxService.activeConfiguration.MotorOutput.NeutralMode.ordinal
                    else if(bus == "canivore") return talonFxFDService.activeConfiguration.MotorOutput.NeutralMode.ordinal
                    else throw  IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.MotorOutput.NeutralMode.ordinal
                    else if(bus == "canivore") return talonFxsFDService.activeConfiguration.MotorOutput.NeutralMode.ordinal
                    else throw  IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }

        }

    override fun setActive(index: Int) {
        val neutral = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.MotorOutput.NeutralMode = neutral
                    talonFxService.active.forEach { it.configurator.apply(talonFxService.activeConfiguration.MotorOutput) }
                } else if(bus == "canivore") {
                    talonFxFDService.activeConfiguration.MotorOutput.NeutralMode = neutral
                    talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration.MotorOutput) }
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.MotorOutput.NeutralMode = neutral
                    talonFxsService.active.forEach { it.configurator.apply(talonFxsService.activeConfiguration.MotorOutput) }
                } else if(bus == "canivore") {
                    talonFxsFDService.activeConfiguration.MotorOutput.NeutralMode = neutral
                    talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration.MotorOutput) }
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}