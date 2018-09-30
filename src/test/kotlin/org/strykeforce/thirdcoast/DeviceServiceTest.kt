package org.strykeforce.thirdcoast

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import edu.wpi.first.wpilibj.Servo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declare
import org.koin.test.declareMock

internal class DeviceServiceTest : KoinTest {

    @Test
    fun `activate adds devices`() {
        val stringService = object : AbstractDeviceService<String>() {
            override fun create(id: Int) = id.toString()
        }
        stringService.activate(listOf(1, 2, 3, 3))
        assertThat(stringService.active).containsOnly("1", "2", "3")
        assertThat(stringService.all).containsOnly("1", "2", "3")

        stringService.activate(listOf(4, 5, 4, 5))
        assertThat(stringService.active).containsOnly("4", "5")
        assertThat(stringService.all).containsOnly("1", "2", "3", "4", "5")
    }

    @Test
    fun `empty list clears active`() {
        val stringService = object : AbstractDeviceService<String>() {
            override fun create(id: Int) = id.toString()
        }
        stringService.activate(listOf(1, 2, 3))
        assertThat(stringService.active).containsOnly("1", "2", "3")

        stringService.activate(emptyList())
        assertThat(stringService.active).isEmpty()
        assertThat(stringService.all).containsOnly("1", "2", "3")
    }

    @Nested
    inner class Talons {
        val talonFactory: TalonFactory by inject()
        val talonService: TalonService by inject()

        @BeforeEach
        fun setUp() {
            startKoin(emptyList(), logger = SLF4JLogger())
            declare {
                single<TalonService> { TalonServiceImpl(get()) }
            }
            declareMock<TalonFactory>()
        }

        @Test
        fun `service gets same Talon`() {
            val t1 = mock<TalonSRX>()
            val t2 = mock<TalonSRX>()
            whenever(talonFactory.create(1)).thenReturn(t1).thenReturn(t2)
            assertThat(talonService.get(1)).isSameAs(t1)
            assertThat(talonService.get(1)).isSameAs(t1)
            assertThat(talonService.all).containsOnly(t1)
        }
    }

    @Nested
    inner class Servos {
        val servoFactory: ServoFactory by inject()
        val servoService: ServoService by inject()

        @BeforeEach
        fun setUp() {
            startKoin(emptyList(), logger = SLF4JLogger())
            declare {
                single<ServoService> { ServoServiceImpl(get()) }
            }
            declareMock<ServoFactory>()
        }

        @Test
        fun `service gets same Servo`() {
            val s1 = mock<Servo>()
            val s2 = mock<Servo>()
            whenever(servoFactory.create(1)).thenReturn(s1).thenReturn(s2)
            assertThat(servoService.get(1)).isSameAs(s1)
            assertThat(servoService.get(1)).isSameAs(s1)
            assertThat(servoService.all).containsOnly(s1)
        }
    }


    @AfterEach
    fun tearDown() {
        stopKoin()
    }
}

