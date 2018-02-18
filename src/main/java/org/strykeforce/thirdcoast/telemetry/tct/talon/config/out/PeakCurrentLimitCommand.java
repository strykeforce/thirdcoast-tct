package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class PeakCurrentLimitCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Peak Current Limit";

  @Inject
  public PeakCurrentLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().peakCurrentLimit(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configPeakCurrentLimit(value, TIMEOUT_MS);
  }
}
