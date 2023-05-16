package org.strykeforce.tct.device;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXPIDSetConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;

public interface TalonFX extends IMotorControllerEnhanced {
  void set(TalonFXControlMode mode, double value);

  void set(TalonFXControlMode mode, double demand0, DemandType demand1Type, double demand1);

  void setInverted(TalonFXInvertType invertType);

  ErrorCode configSelectedFeedbackSensor(
      TalonFXFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs);

  ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitCfg, int timeoutMs);

  ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitCfg);

  ErrorCode configStatorCurrentLimit(StatorCurrentLimitConfiguration currLimitCfg, int timeoutMs);

  ErrorCode configStatorCurrentLimit(StatorCurrentLimitConfiguration currLimitCfg);

  ErrorCode configGetSupplyCurrentLimit(
      SupplyCurrentLimitConfiguration currLimitConfigsToFill, int timeoutMs);

  ErrorCode configGetSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitConfigsToFill);

  ErrorCode configGetStatorCurrentLimit(
      StatorCurrentLimitConfiguration currLimitConfigsToFill, int timeoutMs);

  ErrorCode configGetStatorCurrentLimit(StatorCurrentLimitConfiguration currLimitConfigsToFill);

  ErrorCode configMotorCommutation(MotorCommutation motorCommutation, int timeoutMs);

  ErrorCode configMotorCommutation(MotorCommutation motorCommutation);

  MotorCommutation configGetMotorCommutation(int timeoutMs);

  MotorCommutation configGetMotorCommutation();

  ErrorCode configIntegratedSensorAbsoluteRange(
      AbsoluteSensorRange absoluteSensorRange, int timeoutMs);

  ErrorCode configIntegratedSensorAbsoluteRange(AbsoluteSensorRange absoluteSensorRange);

  ErrorCode configIntegratedSensorOffset(double offsetDegrees, int timeoutMs);

  ErrorCode configIntegratedSensorOffset(double offsetDegrees);

  ErrorCode configIntegratedSensorInitializationStrategy(
      SensorInitializationStrategy initializationStrategy, int timeoutMs);

  ErrorCode configIntegratedSensorInitializationStrategy(
      SensorInitializationStrategy initializationStrategy);

  TalonFXSensorCollection getSensorCollection();

  TalonFXSimCollection getSimCollection();

  ErrorCode configurePID(TalonFXPIDSetConfiguration pid, int pidIdx, int timeoutMs);

  ErrorCode configurePID(TalonFXPIDSetConfiguration pid);

  void getPIDConfigs(TalonFXPIDSetConfiguration pid, int pidIdx, int timeoutMs);

  void getPIDConfigs(TalonFXPIDSetConfiguration pid);

  ErrorCode configAllSettings(TalonFXConfiguration allConfigs, int timeoutMs);

  ErrorCode configAllSettings(TalonFXConfiguration allConfigs);

  ErrorCode getAllConfigs(TalonFXConfiguration allConfigs, int timeoutMs);

  ErrorCode getAllConfigs(TalonFXConfiguration allConfigs);
}
