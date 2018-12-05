package org.strykeforce.thirdcoast.canifier

import com.ctre.phoenix.CANifier
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.CanifierService

class CanifierGeneralInputsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val canifierService: CanifierService by inject()

    override fun execute(): Command {
        val writer = terminal.writer()
        canifierService.active.forEach {
            val pins = CANifier.PinValues()
            it.getGeneralInputs(pins)
            writer.println("${it.deviceID}.LIMF:          ${pins.LIMF}")
            writer.println("${it.deviceID}.LIMR:          ${pins.LIMR}")
            writer.println("${it.deviceID}.QUAD_A:        ${pins.QUAD_A}")
            writer.println("${it.deviceID}.QUAD_B:        ${pins.QUAD_B}")
            writer.println("${it.deviceID}.QUAD_IDX:      ${pins.QUAD_IDX}")
            writer.println("${it.deviceID}.SCL:           ${pins.SCL}")
            writer.println("${it.deviceID}.SPI_CLK_PWM0:  ${pins.SPI_CLK_PWM0}")
            writer.println("${it.deviceID}.SPI_MOSI_PWM1: ${pins.SPI_MOSI_PWM1}")
            writer.println("${it.deviceID}.SPI_MISO_PWM2: ${pins.SPI_MISO_PWM2}")
            writer.println("${it.deviceID}.SPI_CS_PWM3:   ${pins.SPI_CS_PWM3}")
        }
        return super.execute()
    }
}