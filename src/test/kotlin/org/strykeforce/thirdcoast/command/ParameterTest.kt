package org.strykeforce.thirdcoast.command

import com.nhaarman.mockitokotlin2.*
import net.consensys.cava.toml.Toml
import net.consensys.cava.toml.TomlTable
import org.assertj.core.api.Assertions.assertThat
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declare
import org.strykeforce.thirdcoast.command.Parameter.Type.DOUBLE
import java.io.PrintWriter
import java.util.*

internal class ParameterTest : KoinTest {

    private val name = UUID.randomUUID().toString()
    private val desc = UUID.randomUUID().toString()
    private val key = UUID.randomUUID().toString()
    private val type = Parameter.Type.values().asList().shuffled().first()

    private val toml = """
            name = "$name"
            type = "$type"
            desc = "$desc"
        """.trimIndent()

    private val command = mock<Command> {
        on { parent } doReturn mock<Command>()
        on { key } doReturn key
    }

    @BeforeEach
    fun setUp() {
        startKoin(emptyList(), logger = SLF4JLogger())
        declare {
            factory<Parameter> { (tomlTable: TomlTable) ->
                ParameterImpl(command, tomlTable)
            }
        }
    }


    @Test
    fun `toml parsed`() {
        val param: Parameter by inject { parametersOf(Toml.parse(toml)) }
        assertThat(param.name).isEqualTo(name)
        assertThat(param.type).isEqualTo(type)
        assertThat(param.desc).isEqualTo(desc)
    }

    @Test
    fun `has a prompt`() {
        val param: Parameter by inject { parametersOf(Toml.parse(toml)) }
        assertThat((param as ParameterImpl).prompt).isEqualTo("$key : $name> ")
    }

    @Test
    fun `checks range`() {
        val t = """
            name = "$name"
            type = "$DOUBLE"
            range = [-10.0, 10.0]
        """.trimIndent()
        val param: Parameter by inject { parametersOf(Toml.parse(t)) }

        val mockPrintWriter = mock<PrintWriter>()

        val mockTerminal = mock<Terminal> {
            on { writer() } doReturn mockPrintWriter
        }

        val reader = mock<LineReader> {
            on { readLine(any(), isNull(), any()) }
                .doReturnConsecutively(listOf("-11.0", "11.0", "-1.0"))
            on { terminal } doReturn mockTerminal
        }
        assertThat(param.readDouble(reader)).isEqualTo(-1.0)

        verify(mockPrintWriter, times(2))
            .println("enter a number in range (-10.0 - 10.0)")
    }

    @Test
    fun `skip null range check`() {
        val t = """
            name = "$name"
            type = "$DOUBLE"
        """.trimIndent()
        val param: Parameter by inject { parametersOf(Toml.parse(t)) }

        val mockTerminal = mock<Terminal> {
            on { writer() } doReturn mock<PrintWriter>()
        }

        val reader = mock<LineReader> {
            on { readLine(any(), isNull(), any()) } doReturn "2767"
            on { terminal } doReturn mockTerminal
        }
        assertThat(param.readInt(reader)).isEqualTo(2767)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}


