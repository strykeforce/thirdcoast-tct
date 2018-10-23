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
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT
import org.strykeforce.thirdcoast.device.TalonService

internal class SlotParameterCommandTest : KoinTest {

    private val parent: Command = mock {
        on { key } doReturn "parent"
    }

    @Nested
    inner class WithoutRange {
        private val talon: TalonSRX = mock()

        private val reader: LineReader = mock {
            on { readLine(any(), isNull(), eq(DOUBLE_FORMAT.format(0.0))) } doReturn "27.67"
            on { readLine(any(), isNull(), eq("0")) } doReturn "27"
        }

        @BeforeEach
        fun setUp() {
            startKoin(listOf(), logger = SLF4JLogger())
            declare {
                single { TalonService(mock()) { talon } }
                single { reader }
            }
        }

        @Test
        fun `param is parsed`() {
            val toml = """
            name = "P"
            param = "SLOT_P"
        """.trimIndent()
            val command = SlotParameterCommand(null, "foo", Toml.parse(toml))
            assertThat(AttributedString.stripAnsi(command.menu)).isEqualTo("foo: ${DOUBLE_FORMAT.format(0.0)}")
        }

        @Test
        fun `config P`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_P\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kP(0, 27.67, 10)
        }

        @Test
        fun `config I`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_I\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kI(0, 27.67, 10)
        }

        @Test
        fun `config D`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_D\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kD(0, 27.67, 10)
        }

        @Test
        fun `config F`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_F\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_kF(0, 27.67, 10)
        }

        @Test
        fun `config IZone`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_I_ZONE\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).config_IntegralZone(0, 27, 10)
        }

        @Test
        fun `config AllowableErr`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_ALLOWABLE_ERR\""))
            talonService.activate(listOf(1))
            command.execute()
            verify(talon).configAllowableClosedloopError(0, 27, 10)
        }

        @Test
        fun `config MaxIAccum`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_MAX_I_ACCUM\""))
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
                single { TalonService(mock()) { talon } }
                single { reader }
            }
        }


        @Test
        fun `config PeakOutput`() {
            val talonService: TalonService by inject()
            val command = SlotParameterCommand(parent, "foo", Toml.parse("param=\"SLOT_PEAK_OUTPUT\""))
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
