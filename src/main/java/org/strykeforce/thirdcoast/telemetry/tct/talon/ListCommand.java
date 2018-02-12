package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.Encoder;
import org.strykeforce.thirdcoast.talon.LimitSwitch;
import org.strykeforce.thirdcoast.talon.MotionMagicTalonConfiguration;
import org.strykeforce.thirdcoast.talon.PIDTalonConfiguration;
import org.strykeforce.thirdcoast.talon.SoftLimit;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

/** Display a list of all Talons. */
@ParametersAreNonnullByDefault
public class ListCommand extends AbstractCommand {

  public static final String NAME = "List Selected Talons with Configuration";
  private static final String FORMAT_DESCRIPTION = "%14s";
  private static final String FORMAT_CONFIG_DESCRIPTION = "%-24s";
  private static final String FORMAT_DOUBLE = "%14.3f";
  private static final String FORMAT_INTEGER = "%14d";
  private static final String FORMAT_STRING = "%14s";

  private final TalonSet talonSet;

  @Inject
  ListCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader);
    this.talonSet = talonSet;
  }

  private static int intValue(@Nullable VelocityMeasPeriod period) {
    if (period == null) {
      return 0;
    }
    switch (period) {
      case Period_1Ms:
        return 1;
      case Period_2Ms:
        return 2;
      case Period_5Ms:
        return 5;
      case Period_10Ms:
        return 10;
      case Period_20Ms:
        return 20;
      case Period_25Ms:
        return 25;
      case Period_50Ms:
        return 50;
      case Period_100Ms:
        return 100;
      default:
        return 0;
    }
  }

  @Override
  public void perform() {
    PrintWriter writer = terminal.writer();

    //
    // read talons directly
    //
    Set<ThirdCoastTalon> talons = talonSet.selected();
    writer.println();
    if (talons.size() == 0) {
      writer.println(Messages.NO_TALONS);
      return;
    }
    writer.print(header());
    outputString(
        "Mode:",
        talons
            .stream()
            .map(ThirdCoastTalon::getControlMode)
            .map(Enum::name)
            .collect(Collectors.toList()));
    outputParameter("P:", ParamEnum.eProfileParamSlot_P);
    outputParameter("I:", ParamEnum.eProfileParamSlot_I);
    outputParameter("D:", ParamEnum.eProfileParamSlot_D);
    outputParameter("F:", ParamEnum.eProfileParamSlot_F);
    outputParameter("IZone:", ParamEnum.eProfileParamSlot_IZone);
    outputParameter("Allowed Err:", ParamEnum.eProfileParamSlot_AllowableErr);

    outputInteger(
        "Position:",
        talons.stream().map(t -> t.getSelectedSensorPosition(0)).collect(Collectors.toList()));
    outputInteger(
        "Pulse Width:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().getPulseWidthPosition())
            .map(pos -> pos & 0xFFF)
            .collect(Collectors.toList()));

    outputParameter("Fwd Soft:", ParamEnum.eForwardSoftLimitThreshold);
    outputParameter("Rev Soft:", ParamEnum.eReverseSoftLimitThreshold);

    outputBoolean(
        "Fwd LS Clsd:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().isFwdLimitSwitchClosed())
            .collect(Collectors.toList()));
    outputBoolean(
        "Rev LS Clsd:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().isRevLimitSwitchClosed())
            .collect(Collectors.toList()));

    outputInteger(
        "Analog:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().getAnalogInRaw())
            .collect(Collectors.toList()));

    outputParameter("VM Period:", ParamEnum.eSampleVelocityPeriod);
    outputParameter("VM Window:", ParamEnum.eSampleVelocityWindow);
    writer.println();

    //
    // read talon configuration
    //
    writer.println(Messages.bold("Current session configuration:"));
    TalonConfiguration config = talonSet.talonConfigurationBuilder().build();
    writer.println();

    stringLine("Mode:", config.getMode().name());
    doubleLine("Max Setpoint:", config.getSetpointMax());
    Encoder encoder = config.getEncoder();
    if (encoder != null) {
      writer.println();
      stringLine("Encoder:", encoder.getDevice().name());
      booleanLine("  Reversed:", encoder.isReversed());
      writer.println();
    } else {
      stringLine("Encoder:", "DEFAULT");
    }

    NeutralMode neutralMode = config.getBrakeInNeutral();
    stringLine("Neutral Mode:", neutralMode != null ? neutralMode.name() : "DEFAULT");
    booleanLine("Output Reversed:", config.getOutputReversed());
    VelocityMeasPeriod period = config.getVelocityMeasurementPeriod();
    stringLine("Vel. Meas. Period:", period != null ? period.name() : "DEFAULT");
    intLine("Vel. Meas. Window:", config.getVelocityMeasurementWindow());

    LimitSwitch limit = config.getForwardLimitSwitch();
    if (limit != null) {
      writer.println();
      booleanLine("Fwd Limit Switch:", limit.isEnabled());
      booleanLine("  Normally Open :", limit.isNormallyOpen());
    } else {
      stringLine("Fwd Limit Switch:", "DEFAULT");
    }

    limit = config.getReverseLimitSwitch();
    if (limit != null) {
      writer.println();
      booleanLine("Rev Limit Switch:", limit.isEnabled());
      booleanLine("  Normally Open :", limit.isNormallyOpen());
      writer.println();
    } else {
      stringLine("Rev Limit Switch:", "DEFAULT");
    }

    SoftLimit soft = config.getForwardSoftLimit();
    if (soft != null) {
      writer.println();
      booleanLine("Fwd Soft Limit:", soft.isEnabled());
      intLine("  Position:", soft.getPosition());
      writer.println();
    } else {
      stringLine("Fwd Soft Limit:", "DEFAULT");
    }

    soft = config.getReverseSoftLimit();
    if (soft != null) {
      writer.println();
      booleanLine("Rev Soft Limit:", soft.isEnabled());
      intLine("  Position:", soft.getPosition());
      writer.println();
    } else {
      stringLine("Rev Soft Limit:", "DEFAULT");
    }

    intLine("Current Limit:", config.getContinuousCurrentLimit());
    doubleLine("Voltage Ramp Rate:", config.getOpenLoopRampTime());

    if (config instanceof PIDTalonConfiguration) {
      PIDTalonConfiguration pid = (PIDTalonConfiguration) config;
      writer.println();
      doubleLine("P:", pid.getPGain());
      doubleLine("I:", pid.getIGain());
      doubleLine("D:", pid.getDGain());
      doubleLine("F:", pid.getFGain());
      intLine("I-zone:", pid.getIZone());
      writer.println();
      if (pid instanceof MotionMagicTalonConfiguration) {
        MotionMagicTalonConfiguration mmtc = (MotionMagicTalonConfiguration) pid;
        intLine("MM Cruise Velocity:", mmtc.getMotionMagicCruiseVelocity());
        intLine("MM Acceleration:", mmtc.getMotionMagicAcceleration());
        writer.println();
      }
      intLine("Allowable CL Error:", pid.getAllowableClosedLoopError());
      doubleLine("Nominal CL Voltage:", pid.getNominalClosedLoopVoltage());
      doubleLine("Output Voltage Max:", pid.getVoltageCompSaturation());
      doubleLine("Fwd Peak Output Voltage:", pid.getForwardOutputVoltagePeak());
      doubleLine("Rev Peak Output Voltage:", pid.getReverseOutputVoltagePeak());
      doubleLine("Fwd Nom. Output Voltage:", pid.getForwardOutputVoltageNominal());
      doubleLine("Rev Nom. Output Voltage:", pid.getReverseOutputVoltageNominal());
    }

    writer.println();
  }

  private void outputParameter(String description, ParamEnum param) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));

    talonSet
        .selected()
        .stream()
        .mapToDouble(t -> t.configGetParameter(param, 0, 0))
        .forEach(d -> formatter.format(FORMAT_DOUBLE, d));

    terminal.writer().println(sb.toString());
  }

  private void outputString(
      @SuppressWarnings("SameParameterValue") String description, List<String> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (String val : values) {
      formatter.format(FORMAT_STRING, val);
    }
    terminal.writer().println(sb.toString());
  }

  private void outputBoolean(String description, List<Boolean> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Boolean val : values) {
      formatter.format(FORMAT_STRING, val ? "YES" : "NO");
    }
    terminal.writer().println(sb.toString());
  }

  private void outputInteger(String description, List<Integer> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Integer val : values) {
      formatter.format(FORMAT_INTEGER, val);
    }
    terminal.writer().println(sb.toString());
  }

  private void outputDouble(String description, List<Double> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Double val : values) {
      formatter.format(FORMAT_DOUBLE, val);
    }
    terminal.writer().println(sb.toString());
  }

  private String header() {
    // desc(12) value(10) * 6
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    formatter.format(FORMAT_DESCRIPTION, "Talon:");
    for (ThirdCoastTalon talon : talonSet.selected()) {
      formatter.format(FORMAT_INTEGER, talon.getDeviceID());
    }
    sb.append("\n");
    return Messages.bold(sb.toString());
  }

  private void stringLine(String description, String value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_CONFIG_DESCRIPTION, description)));
    formatter.format(FORMAT_STRING, value);
    terminal.writer().println(sb.toString());
  }

  private void booleanLine(String description, @Nullable Boolean value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_CONFIG_DESCRIPTION, description)));
    formatter.format(FORMAT_STRING, value == null ? "DEFAULT" : (value ? "YES" : "NO"));
    terminal.writer().println(sb.toString());
  }

  private void intLine(String description, @Nullable Integer value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_CONFIG_DESCRIPTION, description)));
    if (value != null) {
      formatter.format(FORMAT_INTEGER, value);
    } else {
      formatter.format(FORMAT_STRING, "DEFAULT");
    }
    terminal.writer().println(sb.toString());
  }

  private void doubleLine(String description, @Nullable Double value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_CONFIG_DESCRIPTION, description)));
    if (value != null) {
      formatter.format(FORMAT_DOUBLE, value);
    } else {
      formatter.format(FORMAT_STRING, "DEFAULT");
    }
    terminal.writer().println(sb.toString());
  }
}
