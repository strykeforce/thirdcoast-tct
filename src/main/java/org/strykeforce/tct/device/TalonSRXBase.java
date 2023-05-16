package org.strykeforce.tct.device;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

@SuppressWarnings("unused")
public class TalonSRXBase extends TalonBase implements TalonSRX {
  @Override
  public void set(TalonSRXControlMode mode, double value) {
    throw new NotImplementedError();
  }

  @Override
  public void set(
      TalonSRXControlMode mode, double demand0, DemandType demand1Type, double demand1) {
    throw new NotImplementedError();
  }

  @Override
  public SensorCollection getSensorCollection() {
    throw new NotImplementedError();
  }

  @Override
  public TalonSRXSimCollection getSimCollection() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(
      TalonSRXFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitConfigs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configPeakCurrentLimit(int amps) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configPeakCurrentDuration(int milliseconds) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configContinuousCurrentLimit(int amps) {
    throw new NotImplementedError();
  }

  @Override
  public void enableCurrentLimit(boolean enable) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configurePID(TalonSRXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configurePID(TalonSRXPIDSetConfiguration pid) {
    throw new NotImplementedError();
  }

  @Override
  public void getPIDConfigs(TalonSRXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void getPIDConfigs(TalonSRXPIDSetConfiguration pid) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configAllSettings(TalonSRXConfiguration allConfigs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configAllSettings(TalonSRXConfiguration allConfigs) {
    throw new NotImplementedError();
  }

  @Override
  public void getAllConfigs(TalonSRXConfiguration allConfigs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void getAllConfigs(TalonSRXConfiguration allConfigs) {
    throw new NotImplementedError();
  }
}
