package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public class FollowCommand extends AbstractTalonConfigCommand {

  public static final String NAME = "Set Talon Follower";

  @Inject
  public FollowCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  public void perform() {
    int[] values = getFollowerMasterPair();
    if (values == null) {
      return;
    }
    try {
      TalonSRX follower = talonSet.get(values[0]).orElseThrow(IllegalArgumentException::new);
      TalonSRX master = talonSet.get(values[1]).orElseThrow(IllegalArgumentException::new);
      follower.follow(master);
    } catch (IllegalArgumentException e) {
      terminal
          .writer()
          .println(Messages.bold("\nOne or both Talons missing: " + Arrays.toString(values)));
    }
  }

  private int[] getFollowerMasterPair() {
    terminal.writer().println(Messages.bold("\nenter <follower>,<master> TalonSRX ids"));
    int[] values = null;
    while (values == null) {
      String line;
      try {
        line = reader.readLine(prompt()).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("{}: no value entered", name());
        break;
      }

      List<String> entries = Arrays.asList(line.split(","));
      int[] ints = new int[2];
      try {
        if (entries.size() > 0) {
          ints[0] = Integer.valueOf(entries.get(0));
        } else {
          help();
          continue;
        }
        if (entries.size() > 1) {
          ints[1] = Integer.valueOf(entries.get(1));
        } else {
          help();
          continue;
        }
      } catch (NumberFormatException nfe) {
        help();
        continue;
      }
      values = ints;
    }
    return values;
  }

  private void help() {
    terminal
        .writer()
        .println(Messages.boldRed("please enter two TalonSRX ids separated by a commma"));
  }
}
