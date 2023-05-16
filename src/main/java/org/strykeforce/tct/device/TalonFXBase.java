package org.strykeforce.tct.device;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXPIDSetConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;

@SuppressWarnings("unused")
public class TalonFXBase extends TalonBase implements TalonFX {
  @Override
  public void set(TalonFXControlMode mode, double value) {
    throw new NotImplementedError();
  }

  @Override
  public void set(TalonFXControlMode mode, double demand0, DemandType demand1Type, double demand1) {
    throw new NotImplementedError();
  }

  @Override
  public void setInverted(TalonFXInvertType invertType) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(
      TalonFXFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitCfg) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configStatorCurrentLimit(
      StatorCurrentLimitConfiguration currLimitCfg, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configStatorCurrentLimit(StatorCurrentLimitConfiguration currLimitCfg) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configGetSupplyCurrentLimit(
      SupplyCurrentLimitConfiguration currLimitConfigsToFill, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configGetSupplyCurrentLimit(
      SupplyCurrentLimitConfiguration currLimitConfigsToFill) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configGetStatorCurrentLimit(
      StatorCurrentLimitConfiguration currLimitConfigsToFill, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configGetStatorCurrentLimit(
      StatorCurrentLimitConfiguration currLimitConfigsToFill) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMotorCommutation(MotorCommutation motorCommutation, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMotorCommutation(MotorCommutation motorCommutation) {
    throw new NotImplementedError();
  }

  @Override
  public MotorCommutation configGetMotorCommutation(int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public MotorCommutation configGetMotorCommutation() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configIntegratedSensorAbsoluteRange(
      AbsoluteSensorRange absoluteSensorRange, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configIntegratedSensorAbsoluteRange(AbsoluteSensorRange absoluteSensorRange) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configIntegratedSensorOffset(double offsetDegrees, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configIntegratedSensorOffset(double offsetDegrees) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configIntegratedSensorInitializationStrategy(
      SensorInitializationStrategy initializationStrategy, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configIntegratedSensorInitializationStrategy(
      SensorInitializationStrategy initializationStrategy) {
    throw new NotImplementedError();
  }

  @Override
  public TalonFXSensorCollection getSensorCollection() {
    throw new NotImplementedError();
  }

  @Override
  public TalonFXSimCollection getSimCollection() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configurePID(TalonFXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configurePID(TalonFXPIDSetConfiguration pid) {
    throw new NotImplementedError();
  }

  @Override
  public void getPIDConfigs(TalonFXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void getPIDConfigs(TalonFXPIDSetConfiguration pid) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configAllSettings(TalonFXConfiguration allConfigs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configAllSettings(TalonFXConfiguration allConfigs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode getAllConfigs(TalonFXConfiguration allConfigs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode getAllConfigs(TalonFXConfiguration allConfigs) {
    throw new NotImplementedError();
  }
}
