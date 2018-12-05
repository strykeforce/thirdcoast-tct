package org.strykeforce.thirdcoast.command

import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.contains
import org.strykeforce.thirdcoast.talon.TalonParameter
import org.strykeforce.thirdcoast.talon.TalonParameter.Companion.create
import java.io.PrintWriter
import java.util.*

internal class ParameterTest {

    private val name = "Peak Closed-Loop Output"
    private val key = UUID.randomUUID().toString()
    private val type = Parameter.Type.DOUBLE

    private val parent = mock<Command> {
        on { key } doReturn "parent"
    }

    private val command = mock<Command> {
        on { parent } doReturn parent
        on { key } doReturn key
    }


    @Test
    fun `toml parsed`() {
        val param: Parameter = create(command, TalonParameter.Enum.SLOT_PEAK_OUTPUT.name)
        assertThat(param.name).isEqualTo(name)
        assertThat(param.type).isEqualTo(type)
        assertThat(param.help).contains("absolute value of peak closed-loop output")
    }

    @Test
    fun `has a prompt`() {
        val param: Parameter = create(command, TalonParameter.Enum.SLOT_PEAK_OUTPUT.name)
        assertThat((param as AbstractParameter).prompt).contains("$key> ")
    }

    @Test
    fun `checks range`() {
        val param: Parameter = create(command, TalonParameter.Enum.SLOT_PEAK_OUTPUT.name)

        val mockPrintWriter = mock<PrintWriter>()

        val mockTerminal = mock<Terminal> {
            on { writer() } doReturn mockPrintWriter
        }

        val reader = mock<LineReader> {
            on { readLine(any(), isNull(), any()) }
                .doReturnConsecutively(listOf("-11.0", "11.0", "0.5"))
            on { terminal } doReturn mockTerminal
        }
        assertThat(param.readDouble(reader)).isEqualTo(0.5)

        verify(mockPrintWriter, times(2))
            .println(contains("enter a number in range (0.0 - 1.0)"))
    }

    @Test
    fun `skip null range check`() {
        val param: Parameter = create(command, TalonParameter.Enum.SLOT_ALLOWABLE_ERR.name)

        val mockTerminal = mock<Terminal> {
            on { writer() } doReturn mock<PrintWriter>()
        }

        val reader = mock<LineReader> {
            on { readLine(any(), isNull(), any()) } doReturn "2767"
            on { terminal } doReturn mockTerminal
        }
        assertThat(param.readInt(reader)).isEqualTo(2767)
    }

}


