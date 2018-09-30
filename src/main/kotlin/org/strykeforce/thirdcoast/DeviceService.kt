package org.strykeforce.thirdcoast

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Servo
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface DeviceService<T> {
  val active: Set<T>
  val all: Set<T>
  fun get(id: Int): T
  fun activate(ids: Collection<Int>)
}

typealias TalonService = DeviceService<TalonSRX>
typealias ServoService = DeviceService<Servo>

abstract class AbstractDeviceService<T> : DeviceService<T> {
  private val _active: MutableMap<Int, T> = mutableMapOf()
  override val active: Set<T>
    get() = _active.values.toSet()

  private val _all: MutableMap<Int, T> = mutableMapOf()
  override val all: Set<T>
    get() = _all.values.toSet()

  override fun get(id: Int): T {
    val device = _all[id] ?: create(id)
    if (_all.put(id, device) == null) logger.debug("_all add: {}", device)
    return device
  }

  override fun activate(ids: Collection<Int>) {
    _active.clear()
    ids.associateTo(_active) { id -> id to get(id) }
  }

  abstract fun create(id: Int): T
}

interface DeviceFactory<T> {
  fun create(id: Int): T
}

typealias TalonFactory = DeviceFactory<TalonSRX>
typealias ServoFactory = DeviceFactory<Servo>

class TalonServiceImpl(private val factory: TalonFactory) : AbstractDeviceService<TalonSRX>() {
  override fun create(id: Int) = factory.create(id)
}

class TalonFactoryImpl : TalonFactory {
  override fun create(id: Int) = TalonSRX(id)
}

class ServoServiceImpl(private val factory: ServoFactory) : AbstractDeviceService<Servo>() {
  override fun create(id: Int) = factory.create(id)
}

class ServoFactoryImpl : ServoFactory {
  override fun create(id: Int) = Servo(id)
}

