package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.CANifier
import org.strykeforce.telemetry.TelemetryService
import org.strykeforce.telemetry.measurable.CanifierMeasurable

class CanifierService(private val telemetryService: TelemetryService, factory: (id: Int) -> CANifier) :
    AbstractDeviceService<CANifier>(factory) {
    val timeout = 10

    override fun activate(ids: Collection<Int>): Set<Int> {
        val new = super.activate(ids)
        telemetryService.stop()
        active.forEach { telemetryService.register(CanifierMeasurable(it)) }
        telemetryService.start()
        return new
    }
}