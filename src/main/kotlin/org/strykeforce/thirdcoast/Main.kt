package org.strykeforce.thirdcoast

import edu.wpi.first.wpilibj.RobotBase

class Main {
    companion object {
       @JvmStatic fun main(args : Array<String>) {
            RobotBase.startRobot(::Robot)
        }
    }
}