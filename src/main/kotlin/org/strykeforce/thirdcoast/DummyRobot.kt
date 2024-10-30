package org.strykeforce.thirdcoast

import edu.wpi.first.wpilibj.TimedRobot
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
//import org.koin.standalone.KoinComponent

private val logger = KotlinLogging.logger {}

class DummyRobot : TimedRobot(), KoinComponent {
    override fun robotInit() {
        logger.info { "Starting TCT Dummy Robot" }
    }
    override fun robotPeriodic() {}
    override fun disabledInit() {}
    override fun disabledPeriodic() {}
    override fun autonomousInit() {}
    override fun autonomousPeriodic() {}
    override fun teleopInit() {}
    override fun teleopPeriodic() {}
    override fun testInit() {}
    override fun testPeriodic() {}
}