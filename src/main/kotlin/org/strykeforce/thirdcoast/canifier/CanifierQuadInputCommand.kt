package org.strykeforce.thirdcoast.canifier

import com.ctre.phoenix.CANifier
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CanifierService

class CanifierQuadInputCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val canifierService: CanifierService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        canifierService.active.forEach { canifier ->
            val pos = canifier.quadraturePosition
            val spd = canifier.quadratureVelocity
            writer.println("${canifier.deviceID}.quadrature encoder: position = $pos, speed = $spd")
        }
        return super.execute()
    }
}