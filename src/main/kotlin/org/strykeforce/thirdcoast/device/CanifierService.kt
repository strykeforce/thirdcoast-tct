package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.CANifier
import org.strykeforce.thirdcoast.telemetry.TelemetryService
import org.strykeforce.thirdcoast.telemetry.item.CanifierItem

class CanifierService(private val telemetryService: TelemetryService, factory: (id: Int) -> CANifier) :
    AbstractDeviceService<CANifier>(factory) {
    val timeout = 10

    override fun activate(ids: Collection<Int>) {
        super.activate(ids)
        telemetryService.stop()
        active.forEach { telemetryService.register(CanifierItem(it)) }
        telemetryService.start()
    }
}