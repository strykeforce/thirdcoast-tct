package org.strykeforce.thirdcoast.command

import com.nhaarman.mockitokotlin2.mock
import net.consensys.cava.toml.Toml
import net.consensys.cava.toml.TomlTable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declare

internal class ParameterTest : KoinTest {

    val toml = """
            name = "P constant"
            type = "Double"
            desc = "Sets the 'P' constant in the given parameter slot."
        """.trimIndent()

    val parent = mock<Command>().apply {  }

    @BeforeEach
    fun setUp() {
        startKoin(emptyList(), logger = SLF4JLogger())
        declare {
            factory<Parameter> { (toml: TomlTable) ->
                ParameterImpl(mock(), toml)
            }
        }
    }


    @Test
    fun `toml parsed`() {
        val param: Parameter by inject { parametersOf(Toml.parse(toml)) }

        param.apply {
            assertThat(name).isEqualTo("P constant")
            assertThat(type).isEqualTo("Double")
            assertThat(desc).isEqualTo("Sets the 'P' constant in the given parameter slot.")
        }
    }

    @Disabled
    @Test
    fun `has a prompt`() {
        val param: Parameter by inject { parametersOf(Toml.parse(toml)) }
        assertThat((param as ParameterImpl).prompt).isEqualTo("talon : slot : P constant> ")
    }


    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}