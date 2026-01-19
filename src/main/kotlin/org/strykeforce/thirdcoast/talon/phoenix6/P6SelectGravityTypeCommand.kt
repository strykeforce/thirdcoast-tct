package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.GravityTypeValue
import com.ctre.phoenix6.signals.GravityTypeValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.strykeforce.thirdcoast.warn

private val GRAVITY = listOf(
    Elevator_Static,
    Arm_Cosine
)

private val LABELS = listOf(
    "Static - Elevator",
    "Cosine - Arm"
)

class P6SelectGravityTypeCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<GravityTypeValue>(parent, key, toml, GRAVITY, LABELS) {

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
                    if(bus == "rio") {
                        val slot = talonFxService.activeSlotIndex
                        when(slot){
                            0 -> return talonFxService.activeConfiguration.Slot0.GravityType.ordinal
                            1 -> return  talonFxService.activeConfiguration.Slot1.GravityType.ordinal
                            2 -> return  talonFxService.activeConfiguration.Slot2.GravityType.ordinal
                            else -> {
                                terminal.warn("slot value ${slot} is invalid")
                                return 2767
                            }
                        }
                    } else if(bus == "canivore") {
                        val slot = talonFxFDService.activeSlotIndex
                        when(slot) {
                            0 -> return  talonFxFDService.activeConfiguration.Slot0.GravityType.ordinal
                            1 -> return  talonFxFDService.activeConfiguration.Slot1.GravityType.ordinal
                            2 -> return talonFxFDService.activeConfiguration.Slot2.GravityType.ordinal
                            else -> {
                                terminal.warn("slot value ${slot} is invalid")
                                return 2767
                            }
                        }
                    } else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") {
                        val slot = talonFxsService.activeSlotIndex
                        when(slot){
                            0 -> return talonFxsService.activeConfiguration.Slot0.GravityType.ordinal
                            1 -> return  talonFxsService.activeConfiguration.Slot1.GravityType.ordinal
                            2 -> return  talonFxsService.activeConfiguration.Slot2.GravityType.ordinal
                            else -> {
                                terminal.warn("slot value ${slot} is invalid")
                                return 2767
                            }
                        }
                    } else if(bus == "canivore") {
                        val slot = talonFxsFDService.activeSlotIndex
                        when(slot) {
                            0 -> return  talonFxsFDService.activeConfiguration.Slot0.GravityType.ordinal
                            1 -> return  talonFxsFDService.activeConfiguration.Slot1.GravityType.ordinal
                            2 -> return talonFxsFDService.activeConfiguration.Slot2.GravityType.ordinal
                            else -> {
                                terminal.warn("slot value ${slot} is invalid")
                                return 2767
                            }
                        }
                    } else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }
        }

    override fun setActive(index: Int) {
        val gravity = values[index]
        when(device) {
            "fx" -> {
                if(bus == "rio") {
                    val slot = talonFxService.activeSlotIndex
                    when(slot) {
                        0 -> talonFxService.activeConfiguration.Slot0.GravityType = gravity
                        1 -> talonFxService.activeConfiguration.Slot1.GravityType = gravity
                        2 -> talonFxService.activeConfiguration.Slot2.GravityType = gravity
                        else -> terminal.warn("slot value ${slot} is invalid")
                    }
                    talonFxService.active.forEach {
                        it.configurator.apply(talonFxService.activeConfiguration)
                    }
                } else if(bus == "canivore") {
                    val slot = talonFxFDService.activeSlotIndex
                    when(slot) {
                        0 -> talonFxFDService.activeConfiguration.Slot0.GravityType = gravity
                        1 -> talonFxFDService.activeConfiguration.Slot1.GravityType = gravity
                        2 -> talonFxFDService.activeConfiguration.Slot2.GravityType = gravity
                        else -> terminal.warn("slot value ${slot} is invalid")
                    }
                    talonFxFDService.active.forEach { it.configurator.apply(talonFxFDService.activeConfiguration) }
                }
            }
            "fxs" -> {
                if(bus == "rio") {
                    val slot = talonFxsService.activeSlotIndex
                    when(slot) {
                        0 -> talonFxsService.activeConfiguration.Slot0.GravityType = gravity
                        1 -> talonFxsService.activeConfiguration.Slot1.GravityType = gravity
                        2 -> talonFxsService.activeConfiguration.Slot2.GravityType = gravity
                        else -> terminal.warn("slot value ${slot} is invalid")
                    }
                    talonFxsService.active.forEach {
                        it.configurator.apply(talonFxsService.activeConfiguration)
                    }
                } else if(bus == "canivore") {
                    val slot = talonFxsFDService.activeSlotIndex
                    when(slot) {
                        0 -> talonFxsFDService.activeConfiguration.Slot0.GravityType = gravity
                        1 -> talonFxsFDService.activeConfiguration.Slot1.GravityType = gravity
                        2 -> talonFxsFDService.activeConfiguration.Slot2.GravityType = gravity
                        else -> terminal.warn("slot value ${slot} is invalid")
                    }
                    talonFxsFDService.active.forEach { it.configurator.apply(talonFxsFDService.activeConfiguration) }
                }
            }
            else -> throw IllegalArgumentException()
        }

    }

}