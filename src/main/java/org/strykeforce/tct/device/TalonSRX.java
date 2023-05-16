package org.strykeforce.tct.device;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

@SuppressWarnings("unused")
public interface TalonSRX extends IMotorControllerEnhanced {
  void set(TalonSRXControlMode mode, double value);

  void set(TalonSRXControlMode mode, double demand0, DemandType demand1Type, double demand1);

  SensorCollection getSensorCollection();

  TalonSRXSimCollection getSimCollection();

  ErrorCode configSelectedFeedbackSensor(
      TalonSRXFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs);

  ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitConfigs);

  ErrorCode configPeakCurrentLimit(int amps, int timeoutMs);

  ErrorCode configPeakCurrentLimit(int amps);

  ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs);

  ErrorCode configPeakCurrentDuration(int milliseconds);

  ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs);

  ErrorCode configContinuousCurrentLimit(int amps);

  void enableCurrentLimit(boolean enable);

  ErrorCode configurePID(TalonSRXPIDSetConfiguration pid, int pidIdx, int timeoutMs);

  ErrorCode configurePID(TalonSRXPIDSetConfiguration pid);

  void getPIDConfigs(TalonSRXPIDSetConfiguration pid, int pidIdx, int timeoutMs);

  void getPIDConfigs(TalonSRXPIDSetConfiguration pid);

  ErrorCode configAllSettings(TalonSRXConfiguration allConfigs, int timeoutMs);

  ErrorCode configAllSettings(TalonSRXConfiguration allConfigs);

  void getAllConfigs(TalonSRXConfiguration allConfigs, int timeoutMs);

  void getAllConfigs(TalonSRXConfiguration allConfigs);
}
