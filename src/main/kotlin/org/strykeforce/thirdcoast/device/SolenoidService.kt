package org.strykeforce.thirdcoast.device

import edu.wpi.first.wpilibj.Solenoid

class SolenoidService(factory: (id: Int) -> Solenoid) : AbstractDeviceService<Solenoid>(factory)
