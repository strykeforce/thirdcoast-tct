package org.strykeforce.thirdcoast

import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DeviceServiceTest {

    @Test
    fun `activate adds devices`() {
        val stringService = AbstractDeviceService { it.toString() }

        stringService.activate(listOf(1, 2, 3, 3))
        assertThat(stringService.active).containsOnly("1", "2", "3")
        assertThat(stringService.all).containsOnly("1", "2", "3")

        stringService.activate(listOf(4, 5, 4, 5))
        assertThat(stringService.active).containsOnly("4", "5")
        assertThat(stringService.all).containsOnly("1", "2", "3", "4", "5")
    }

    @Test
    fun `empty list clears active`() {
        val stringService = AbstractDeviceService { it.toString() }
        stringService.activate(listOf(1, 2, 3))
        assertThat(stringService.active).containsOnly("1", "2", "3")

        stringService.activate(emptyList())
        assertThat(stringService.active).isEmpty()
        assertThat(stringService.all).containsOnly("1", "2", "3")
    }

    @Test
    fun `service gets same Talon`() {
        val talonService = TalonService { mock() }
        val t1 = talonService.get(1)
        assertThat(talonService.get(1)).isSameAs(t1)
        assertThat(talonService.all).containsOnly(t1)
    }

    @Test
    fun `service gets same Servo`() {
        val servoService = ServoService { mock() }
        val s1 = servoService.get(1)
        assertThat(servoService.get(1)).isSameAs(s1)
        assertThat(servoService.all).containsOnly(s1)
    }

}

