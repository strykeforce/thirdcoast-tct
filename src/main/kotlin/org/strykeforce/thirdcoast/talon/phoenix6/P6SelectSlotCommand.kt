package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService

private val SLOTS = listOf(0,1,2)

class P6SelectSlotCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<Int>(parent, key, toml, SLOTS, SLOTS.map(Int::toString)) {

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
                    if(bus == "rio") return talonFxService.activeSlotIndex
                    else if(bus == "canivore") return talonFxFDService.activeSlotIndex
                    else throw IllegalArgumentException()
                }
                "fxs" -> {
                    if(bus == "rio") return talonFxsService.activeSlotIndex
                    else if(bus == "canivore") return talonFxsFDService.activeSlotIndex
                    else throw IllegalArgumentException()
                }
                else -> throw IllegalArgumentException()
            }
        }

    override fun setActive(index: Int) {
        when(device) {
            "fx" -> {
                if(bus == "rio") talonFxService.activeSlotIndex = index
                else if(bus == "canivore") talonFxFDService.activeSlotIndex = index
                else throw IllegalArgumentException()
            }
            "fxs" -> {
                if(bus == "rio") talonFxsService.activeSlotIndex = index
                else if(bus == "canivore") talonFxsFDService.activeSlotIndex = index
                else throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }

    }
}