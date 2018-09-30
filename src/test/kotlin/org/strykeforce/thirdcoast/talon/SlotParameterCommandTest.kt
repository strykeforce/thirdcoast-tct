package org.strykeforce.thirdcoast.talon

import net.consensys.cava.toml.Toml
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
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
            [param]
                name = "P constant"
        """.trimIndent()

        val command = SlotParameterCommand(null, "k_p", Toml.parse(toml))
//        assertThat(command.param.name).isEqualTo("P constant")
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}