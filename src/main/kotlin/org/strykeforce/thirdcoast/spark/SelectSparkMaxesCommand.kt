package org.strykeforce.thirdcoast.spark

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.revrobotics.CANSparkMax
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.SparkMaxService
import org.strykeforce.thirdcoast.info
import org.strykeforce.thirdcoast.readIntList
import org.strykeforce.thirdcoast.warn

class SelectSparkMaxesCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {
    private val sparkMaxService: SparkMaxService by inject()

    override val menu: String
        get() = formatMenu(sparkMaxService.active.map(CANSparkMax::getDeviceId).joinToString())


    override fun execute(): Command {
        while (true) {
            try {
                val ids = reader.readIntList(this.prompt("ids"), sparkMaxService.active.map(CANSparkMax::getDeviceId))
                val new = sparkMaxService.activate(ids)
                if (new.isNotEmpty())
                    terminal.info(
                        "reset motor type, control mode, secondary and smart current limits, and voltage compensation" +
                                "\nfor spark maxes: ${new.joinToString()}"
                    )
                return super.execute()
            } catch (e: IllegalArgumentException) {
                terminal.warn("Please enter a list of Spark Max ids separated by ','")
            }
        }
    }
}