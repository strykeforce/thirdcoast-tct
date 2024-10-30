package org.strykeforce.thirdcoast.talon.phoenix6

import com.ctre.phoenix6.signals.NeutralModeValue
import net.consensys.cava.toml.TomlTable
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService

class P6SelectNeutralOutputCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractSelectCommand<NeutralModeValue>(parent, key, toml,
    listOf(NeutralModeValue.Coast, NeutralModeValue.Brake),
    listOf("Coast", "Brake")) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val activeIndex: Int
        get() {
            if(bus == "rio") return talonFxService.activeNeutralOut.ordinal
            else if(bus == "canivore") return talonFxFDService.activeNeutralOut.ordinal
            else throw  IllegalArgumentException()
        }

    override fun setActive(index: Int) {
        val neutral = values[index]
        if(bus == "rio") talonFxService.activeNeutralOut = neutral
        else if(bus == "canivore") talonFxFDService.activeNeutralOut = neutral
        else throw  IllegalArgumentException()
    }
}