package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class TalonService(factory: (id: Int) -> TalonSRX) : AbstractDeviceService<TalonSRX>(factory) {

    val timeout = 10
    var activeSlotIndex: Int = 0
    var controlMode = ControlMode.PercentOutput
    var neutralMode = NeutralMode.EEPROMSetting

    var activeConfiguration = TalonSRXConfiguration()
        get() {
            active.firstOrNull()?.getAllConfigs(field) ?: logger.info("no active talons, returning default config")
            return field
        }

    val outputReverse: Boolean
        get() = active.firstOrNull()?.inverted ?: false

    override fun activate(ids: Collection<Int>) {
        activeSlotIndex = 0
        controlMode = ControlMode.PercentOutput
        neutralMode = NeutralMode.EEPROMSetting
        activeConfiguration = TalonSRXConfiguration()

        super.activate(ids)
        active.forEach {
            it.setNeutralMode(neutralMode)
            it.selectProfileSlot(activeSlotIndex, 0)
            it.inverted = false
        }
    }

}
