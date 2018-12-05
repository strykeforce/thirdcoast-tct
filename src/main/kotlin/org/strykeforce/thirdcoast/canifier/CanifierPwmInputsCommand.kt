package org.strykeforce.thirdcoast.canifier

import com.ctre.phoenix.CANifier
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CanifierService
import org.strykeforce.thirdcoast.format

class CanifierPwmInputsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val canifierService: CanifierService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        canifierService.active.forEach { canifier ->
            val pulseWidthAndPeriod = DoubleArray(2)
            CANifier.PWMChannel.values().forEach { channel ->
                canifier.getPWMInput(channel, pulseWidthAndPeriod)
                printChannel(canifier.deviceID, channel, pulseWidthAndPeriod)
            }
        }
        return super.execute()
    }

    private fun printChannel(id: Int, channel: CANifier.PWMChannel, pulseWidthAndPeriod: DoubleArray) {
        val writer = terminal.writer()
        val pulseWidth = pulseWidthAndPeriod[0].format(1)
        val period = pulseWidthAndPeriod[1].format(1)
        val position = (4096.0 * pulseWidthAndPeriod[0] /  pulseWidthAndPeriod[1]).format(0)
        writer.println("$id.$channel: pulsewidth = $pulseWidth us, period = $period us, position = $position")
    }
}