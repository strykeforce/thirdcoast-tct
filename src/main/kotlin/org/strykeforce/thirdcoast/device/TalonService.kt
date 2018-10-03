package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class TalonService(factory: (id: Int) -> TalonSRX) : AbstractDeviceService<TalonSRX>(factory) {

    val activeConfiguration = TalonSRXConfiguration()
        get() {
            active.firstOrNull()?.getAllConfigs(field) ?: logger.info("get default config")
            return field
        }

}
