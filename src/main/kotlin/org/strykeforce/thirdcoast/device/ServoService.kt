package org.strykeforce.thirdcoast.device

import edu.wpi.first.wpilibj.Servo

class ServoService(factory: (id: Int) -> Servo) : AbstractDeviceService<Servo>(factory)
