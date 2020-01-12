package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectTalonsCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val talonService: TalonService by inject()
    private val talonFxService: TalonFxService by inject()

    val type = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override val menu: String
        get() {
            when (type) {
                "srx" -> {
                    return formatMenu(talonService.active.map(TalonSRX::getDeviceID).joinToString())
                }
                "fx" -> {
                    return formatMenu(talonFxService.active.map(TalonFX::getDeviceID).joinToString())
                }
                else -> throw IllegalArgumentException()
            }
        }

    override fun execute(): Command {
        while (true) {
            try {
                var ids: List<Int>
                var new: Set<Int>

                if (type == "srx") {
                    ids = reader.readIntList(this.prompt("ids"), talonService.active.map(TalonSRX::getDeviceID))
                    new = talonService.activate(ids)
                } else if (type == "fx") {
                    ids = reader.readIntList(this.prompt("ids"), talonFxService.active.map(TalonFX::getDeviceID))
                    new = talonFxService.activate(ids)

                } else throw IllegalArgumentException()

                if (new.isNotEmpty())
                    terminal.info(
                        "reset control mode, current limit enabled, brake, voltage compensation\nand sensor phase " +
                                "for talons: ${new.joinToString()}"
                    )
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Talon ids separated by ','")
            }
        }
    }
}