package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.GravityTypeValue
import com.ctre.phoenix6.signals.GravityTypeValue.*
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
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

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
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

    override fun setActive(index: Int) {
        val gravity = values[index]
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

}