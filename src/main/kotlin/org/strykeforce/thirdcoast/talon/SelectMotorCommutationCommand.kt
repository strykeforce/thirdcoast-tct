package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.MotorCommutation
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.AbstractSelectCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonFxService

private val COMMUTATION = listOf(
    MotorCommutation.Trapezoidal
)

private val LABELS = listOf(
    "Trapezoidal"
)

class SelectMotorCommutationCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractSelectCommand<MotorCommutation>(parent, key, toml, COMMUTATION, LABELS) {

    private val talonFxService: TalonFxService by inject()

    override val activeIndex: Int
        get() = values.indexOf(talonFxService.activeConfiguration.motorCommutation)

    override fun setActive(index: Int) {
        val mode = values[index]
        talonFxService.active.forEach { it.configMotorCommutation(mode,talonFxService.timeout) }
        talonFxService.activeConfiguration.motorCommutation = mode
    }
}