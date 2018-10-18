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
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declare
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.TalonService

internal class HardLimitCommandsTest : KoinTest {

    private val toml = Toml.parse(
        """
        type = "menu"
          [limit]
            type = "menu"
            order = 1
            menu = "configure soft and hard limits"
            [limit.forward_hard_source]
              type = "talon.hard.source"
              order = 1
              forward = true
              menu = "forward hard limit switch source"
            [limit.forward_hard_normal]
              type = "talon.hard.normal"
              order = 2
              forward = true
              menu = "forward hard limit switch normal"
        """
    )

    private val talon: TalonSRX = mock()

    @BeforeEach
    fun setUp() {
        StandAloneContext.startKoin(listOf(), logger = SLF4JLogger())
        declare {
            single { TalonService(mock()) { talon } }
        }

    }

    @Test
    fun `hard limit source configs talon`() {
        val root = Command.createFromToml(toml)
        val talonService: TalonService by inject()
        talonService.activate(listOf(1))
        val source = root.children.first().children.first() as SelectHardLimitSourceCommand
        val normal = root.children.first().children.last() as SelectHardLimitNormalCommand
        source.setActive(1) // FeedbackConnector
        verify(talon).configForwardLimitSwitchSource(FeedbackConnector, NormallyOpen)
    }

    @AfterEach
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

}

