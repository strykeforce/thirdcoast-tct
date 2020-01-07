package org.strykeforce.thirdcoast

import edu.wpi.first.wpilibj.RobotBase

class DummyMain {
    companion object {
       @JvmStatic fun main(args : Array<String>) {
            RobotBase.startRobot(::DummyRobot)
        }
    }
}