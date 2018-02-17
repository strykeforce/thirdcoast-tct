package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.phoenix.ErrorCode;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.Errors;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class DumpAccumulatorCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Dump Accumulator";

  @Inject
  DumpAccumulatorCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, boolean value) {
    logger.info("dumping accumulator for {}", value, talon.getDescription());
    ErrorCode err = talon.setIntegralAccumulator(0, 0, 10);
    Errors.check(talon, "setIntegralAccumulator", err, logger);
  }

  @Override
  protected void saveConfig(boolean value) {}
}
