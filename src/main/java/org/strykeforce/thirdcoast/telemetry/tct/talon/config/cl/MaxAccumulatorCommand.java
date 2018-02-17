package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class MaxAccumulatorCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Max Accumulator";

  @Inject
  public MaxAccumulatorCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) { // TODO
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configMaxIntegralAccumulator(0, value, TIMEOUT_MS);
  }
}
