package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.nhaarman.mockitokotlin2.*
import net.consensys.cava.toml.Toml
import org.assertj.core.api.Assertions.assertThat
import org.jline.reader.LineReader
import org.jline.utils.AttributedString
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
import org.strykeforce.thirdcoast.device.TalonService

internal class SlotParameterCommandTest : KoinTest {

    @Nested
    inner class WithoutRange {
        private val talon: TalonSRX = mock()

        private val reader: LineReader = mock {
            on { readLine(any(), isNull(), eq("0.0")) } doReturn "27.67"
            on { readLine(any(), isNull(), eq("0")) } doReturn "27"
        }

        @BeforeEach
        fun setUp() {
            startKoin(listOf(), logger = SLF4JLogger())
            declare {
                single { TalonService { _ -> talon } }
                single { reader }
            }
        }

        @Test
        fun `param is parsed`() {
            val toml = """
            name = "P"
            param = "eProfileParamSlot_P"
        """.trimIndent()
            val command = SlotParameterCommand(null, "foo", Toml.parse(toml))
            assertThat(command.param.name).isEqualTo("P")
            assertThat(AttributedString.stripAnsi(command.menu)).isEqualTo("foo: 0.0")
        }

        @Test
        fun `config P`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_P\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kP(0, 27.67, 10)
        }

        @Test
        fun `config I`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_I\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kI(0, 27.67, 10)
        }

        @Test
        fun `config D`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_D\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kD(0, 27.67, 10)
        }

        @Test
        fun `config F`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_F\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kF(0, 27.67, 10)
        }

        @Test
        fun `config IZone`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_IZone\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_IntegralZone(0, 27, 10)
        }

        @Test
        fun `config AllowableErr`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_AllowableErr\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).configAllowableClosedloopError(0, 27, 10)
        }

        @Test
        fun `config MaxIAccum`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_MaxIAccum\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).configMaxIntegralAccumulator(0, 27.67, 10)
        }

    }

    @Nested
    inner class WithRange {
        private val talon: TalonSRX = mock()

        private val reader: LineReader = mock {
            on { readLine(any(), isNull(), any()) } doReturn "0.2767"
        }

        @BeforeEach
        fun setUp() {
            startKoin(listOf(), logger = SLF4JLogger())
            declare {
                single { TalonService { _ -> talon } }
                single { reader }
            }
        }


        @Test
        fun `config PeakOutput`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(mock(), "foo", Toml.parse("param=\"eProfileParamSlot_PeakOutput\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).configClosedLoopPeakOutput(0, 0.2767, 10)
        }


    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }
}
