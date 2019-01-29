package org.strykeforce.thirdcoast.device

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface DeviceService<T> {
    /** All previously activated devices. */
    val active: Set<T>
    /** Currently active devices. */
    val all: Set<T>

    /** Return a activated device, reusing previously activated devices */
    fun get(id: Int): T

    /** Activate a collection of devices.
     * @param ids the ids of devices to activate.
     * @return any devices that were added to the currently active set.
     */
    fun activate(ids: Collection<Int>): Set<Int>
}

open class AbstractDeviceService<T>(private val factory: (id: Int) -> T) :
    DeviceService<T> {
    private val _active: MutableMap<Int, T> = mutableMapOf()
    override val active: Set<T>
        get() = _active.values.toSet()

    private val _all: MutableMap<Int, T> = mutableMapOf()
    override val all: Set<T>
        get() = _all.values.toSet()

    override fun get(id: Int): T {
        val device = _all[id] ?: factory(id)
        if (_all.put(id, device) == null) logger.debug("_all add: {}", device)
        return device
    }

    override fun activate(ids: Collection<Int>): Set<Int> {
        val new = ids.toSet().minus(_active.keys)
        _active.clear()
        ids.associateTo(_active) { id -> id to get(id) }
        return new
    }

    fun idsInAll(ids: Collection<Int>): Set<Int> = _all.keys.intersect(ids)
}


