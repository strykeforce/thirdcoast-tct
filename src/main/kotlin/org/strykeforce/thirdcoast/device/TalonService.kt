package org.strykeforce.thirdcoast.device

import com.ctre.phoenix.motorcontrol.can.TalonSRX

class TalonService(factory: (id: Int) -> TalonSRX) : AbstractDeviceService<TalonSRX>(factory)
