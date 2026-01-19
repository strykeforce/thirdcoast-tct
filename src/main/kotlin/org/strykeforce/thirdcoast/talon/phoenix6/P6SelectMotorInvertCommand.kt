package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.InvertedValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService

class P6SelectMotorInvertCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<InvertedValue>(parent, key, toml,
    listOf(CounterClockwise_Positive, Clockwise_Positive),
    listOf("CCW positive", "CW positive")) {

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
                    if(bus == "rio") return talonFxService.activeConfiguration.MotorOutput.Inverted.ordinal
                    else if(bus == "canivore") return talonFxFDService.activeConfiguration.MotorOutput.Inverted.ordinal
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeConfiguration.MotorOutput.Inverted.ordinal
                    else if(bus == "canivore") return talonFxsFDService.activeConfiguration.MotorOutput.Inverted.ordinal
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }


        }

    override fun setActive(index: Int) {
        val invert = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    talonFxService.activeConfiguration.MotorOutput.Inverted = invert
                    talonFxService.active.forEach { it.talonFX.configurator.apply(talonFxService.activeConfiguration.MotorOutput) }
                } else if(bus == "canivore") {
                    talonFxFDService.activeConfiguration.MotorOutput.Inverted = invert
                    talonFxFDService.active.forEach { it.talonFX.configurator.apply(talonFxFDService.activeConfiguration.MotorOutput) }
                } else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") {
                    talonFxsService.activeConfiguration.MotorOutput.Inverted = invert
                    talonFxsService.active.forEach { it.talonFXS.configurator.apply(talonFxsService.activeConfiguration.MotorOutput) }
                } else if(bus == "canivore") {
                    talonFxsFDService.activeConfiguration.MotorOutput.Inverted = invert
                    talonFxsFDService.active.forEach { it.talonFXS.configurator.apply(talonFxsFDService.activeConfiguration.MotorOutput) }
                } else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}