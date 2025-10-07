package org.strykeforce.thirdcoast.cancoder

import org.strykeforce.thirdcoast.command.Command
import net.consensys.cava.toml.TomlTable
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.device.CancoderFDService
import org.strykeforce.thirdcoast.device.CancoderService
import org.koin.core.component.inject

class CancoderDefaultStatusFrameCommand(parent: Command?, key: String, toml: TomlTable): AbstractCommand(parent, key, toml) {
    private val cancoderService: CancoderService by inject()
    private val cancoderFDService : CancoderFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")

    override fun execute(): Command {
        val timeout = cancoderService.timeout
        if(bus == "rio") {
            cancoderService.active.forEach{
                cancoderService.grapherStatusFrameHz = 0.0;
                it.magnetHealth.setUpdateFrequency(4.0, timeout)
                it.position.setUpdateFrequency(100.0, timeout)
                it.positionSinceBoot.setUpdateFrequency(4.0, timeout)
                it.absolutePosition.setUpdateFrequency(100.0, timeout)
                it.supplyVoltage.setUpdateFrequency(4.0, timeout)
                it.unfilteredVelocity.setUpdateFrequency(4.0, timeout)
                it.velocity.setUpdateFrequency(100.0, timeout)
            }

        } else if(bus == "canivore") {
            cancoderFDService.active.forEach {
                cancoderService.grapherStatusFrameHz = 0.0;
                it.magnetHealth.setUpdateFrequency(100.0, timeout)
                it.position.setUpdateFrequency(100.0, timeout)
                it.positionSinceBoot.setUpdateFrequency(100.0, timeout)
                it.absolutePosition.setUpdateFrequency(100.0, timeout)
                it.supplyVoltage.setUpdateFrequency(100.0, timeout)
                it.unfilteredVelocity.setUpdateFrequency(100.0, timeout)
                it.velocity.setUpdateFrequency(100.0, timeout)
            }
        }
        return super.execute();
    }
}