package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.Toml
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.strykeforce.thirdcoast.command.Parameter
import org.strykeforce.thirdcoast.tctModule

internal class SlotParameterCommandTest : KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin(listOf(tctModule), logger = SLF4JLogger())
    }

    @Test
    fun `param is parsed`() {
        val toml = """
            type = "slot.param"
            menu = "P"
            param = "eProfileParamSlot_P"
        """.trimIndent()

        val command = SlotParameterCommand(null, "foo", Toml.parse(toml))
        assertThat(command.param.name).isEqualTo("P")
        assertThat(command.menu).isEqualTo("P")
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}