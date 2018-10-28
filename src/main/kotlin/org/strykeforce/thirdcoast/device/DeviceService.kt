package org.strykeforce.thirdcoast.device

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface DeviceService<T> {
    val active: Set<T>
    val all: Set<T>
    fun get(id: Int): T
    fun activate(ids: Collection<Int>)
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

    override fun activate(ids: Collection<Int>) {
        _active.clear()
        ids.associateTo(_active) { id -> id to get(id) }
    }

    fun idsInAll(ids: Collection<Int>): Set<Int> = _all.keys.intersect(ids)
}

