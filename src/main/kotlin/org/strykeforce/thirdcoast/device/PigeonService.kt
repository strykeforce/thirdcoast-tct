package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.sensors.PigeonIMU
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame
import org.strykeforce.thirdcoast.telemetry.item.PigeonIMUItem
import mu.KotlinLogging
import org.strykeforce.thirdcoast.telemetry.TelemetryService

private val logger = KotlinLogging.logger {}

private const val DEFAULT_TEMP_COMP = false


class PigeonService(private val telemetryService: TelemetryService, factory: (id: Int) -> PigeonIMU) :
    AbstractDeviceService<PigeonIMU>(factory) {

    val timeout = 10
    var tempCompensation = DEFAULT_TEMP_COMP


    override fun activate(ids: Collection<Int>): Set<Int> {
        val new = super.activate(ids)
        telemetryService.stop()
        active.forEach {telemetryService.register(PigeonIMUItem(it)) }
        active.forEach {
            it.setTemperatureCompensationDisable(tempCompensation)
        }

        telemetryService.start()
        return new
    }

}