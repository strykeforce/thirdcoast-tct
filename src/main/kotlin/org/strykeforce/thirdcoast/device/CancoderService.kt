package org.strykeforce.thirdcoast.device

import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import mu.KotlinLogging
import org.strykeforce.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

class CancoderService(
    private val telemetryService: TelemetryService, factory: (id: Int) -> CANcoder
): AbstractDeviceService<CANcoder>(factory) {

    val timeout = 10.0
    var dirty = true
    var grapherStatusFrameHz : Double = 0.0;

    var activeConfiguration = CANcoderConfiguration()
        get() {
            if(!dirty) return  field
            active.firstOrNull()?.configurator?.refresh(field)
                ?: logger.debug("no active CANcoder's, returning default config")
            dirty = false
            return field
        }

    override fun activate(ids: Collection<Int>): Set<Int> {
        dirty = true

        val new = super.activate(ids)
        telemetryService.stop()
        active.filter { new.contains(it.deviceID) }.forEach{
            telemetryService.register(it)
        }
        telemetryService.start()
        active.firstOrNull()
        return  new
    }


}