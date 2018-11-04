package org.strykeforce.thirdcoast.device

import edu.wpi.first.wpilibj.DigitalOutput

class DigitalOutputService(factory: (id: Int) -> DigitalOutput) : AbstractDeviceService<DigitalOutput>(factory)