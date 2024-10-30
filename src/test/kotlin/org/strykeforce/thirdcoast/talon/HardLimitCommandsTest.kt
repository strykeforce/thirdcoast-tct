package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal.NormallyOpen
import com.ctre.phoenix.motorcontrol.LimitSwitchSource.FeedbackConnector
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import net.consensys.cava.toml.Toml
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.logger.SLF4JLogger
import org.koin.test.KoinTest
import org.koin.test.mock.declare
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

internal class HardLimitCommandsTest : KoinTest {

    private val toml = Toml.parse(
        """
        type = "menu"
          [limit]
            type = "menu"
            device = "srx"
            order = 1
            menu = "configure soft and hard limits"
            [limit.forward_hard_source]
              type = "talon.hard.source"
              device = "srx"
              order = 1
              forward = true
              menu = "forward hard limit switch source"
            [limit.forward_hard_normal]
              type = "talon.hard.normal"
              device = "srx"
              order = 2
              forward = true
              menu = "forward hard limit switch normal"
        """
    )

    private val talon: TalonSRX = mock()

    @BeforeEach
    fun setUp() {
        startKoin{
            /*modules(module {
                single { TalonService(mock()) {talon} }
            })*/
            logger(SLF4JLogger())
        }

        declare {
            module { single { TalonService(mock()) {talon} } }
            //single { TalonService(mock()) { talon } }
        }

    }

    @Test
    fun `hard limit source configs talon`() {
        val root = Command.createFromToml(toml)
        val talonService: TalonService by inject()
        talonService.activate(listOf(1))
        val source = root.children.first().children.first() as SelectHardLimitSourceCommand
        root.children.first().children.last() as SelectHardLimitNormalCommand
        source.setActive(1) // FeedbackConnector
        verify(talon).configForwardLimitSwitchSource(FeedbackConnector, NormallyOpen)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}

