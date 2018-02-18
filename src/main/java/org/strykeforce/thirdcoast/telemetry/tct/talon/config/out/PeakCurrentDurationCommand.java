package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class PeakCurrentDurationCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Peak Current Duration";

  @Inject
  public PeakCurrentDurationCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().peakCurrentDuration(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configPeakCurrentDuration(value, TIMEOUT_MS);
  }
}
