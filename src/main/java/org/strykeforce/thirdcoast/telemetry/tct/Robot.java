package org.strykeforce.thirdcoast.telemetry.tct;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.HAL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Robot extends RobotBase {

  private static final String TELE_WARN = "Driver Station must be in TeleOperated mode";
  private static final double DELAY = 0.01;

  private final Executor executor = Executors.newSingleThreadExecutor();
  private boolean warningReported = false;

  public void startCompetition() {
    // Tell the DS that the robot is ready to be enabled
    HAL.observeUserProgramStarting();
    executor.execute(new Main());

    while (true) {
      if (isDisabled()) {
        while (isDisabled()) {
          Timer.delay(DELAY);
        }
      } else if (isAutonomous()) {
        if (!warningReported) {
          DriverStation.reportWarning(TELE_WARN, false);
          warningReported = true;
        }
        while (isAutonomous() && !isDisabled()) {
          Timer.delay(DELAY);
        }
      } else if (isTest()) {
        if (!warningReported) {
          DriverStation.reportWarning(TELE_WARN, false);
          warningReported = true;
        }
        while (isTest() && isEnabled()) {
          Timer.delay(DELAY);
        }
      } else {
        while (isOperatorControl() && !isDisabled()) {
          Timer.delay(DELAY);
        }
      }
    } /* while loop */
  }
}
