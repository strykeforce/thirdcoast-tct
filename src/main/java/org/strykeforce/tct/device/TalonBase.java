package org.strykeforce.tct.device;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;

public class TalonBase implements IMotorControllerEnhanced {
  @Override
  public ErrorCode configSelectedFeedbackSensor(
      FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSupplyCurrentLimit(
      SupplyCurrentLimitConfiguration currLimitCfg, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public double getOutputCurrent() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configVelocityMeasurementPeriod(SensorVelocityMeasPeriod period, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  @Deprecated
  public ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configForwardLimitSwitchSource(
      LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configReverseLimitSwitchSource(
      LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void set(ControlMode Mode, double demand) {
    throw new NotImplementedError();
  }

  @Override
  public void set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1) {
    throw new NotImplementedError();
  }

  @Override
  public void neutralOutput() {
    throw new NotImplementedError();
  }

  @Override
  public void setNeutralMode(NeutralMode neutralMode) {
    throw new NotImplementedError();
  }

  @Override
  public void setSensorPhase(boolean PhaseSensor) {
    throw new NotImplementedError();
  }

  @Override
  public void setInverted(boolean invert) {
    throw new NotImplementedError();
  }

  @Override
  public void setInverted(InvertType invertType) {
    throw new NotImplementedError();
  }

  @Override
  public boolean getInverted() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void enableVoltageCompensation(boolean enable) {
    throw new NotImplementedError();
  }

  @Override
  public double getBusVoltage() {
    throw new NotImplementedError();
  }

  @Override
  public double getMotorOutputPercent() {
    throw new NotImplementedError();
  }

  @Override
  public double getMotorOutputVoltage() {
    throw new NotImplementedError();
  }

  @Override
  public double getTemperature() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(
      RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSelectedFeedbackCoefficient(
      double coefficient, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configRemoteFeedbackFilter(
      int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configRemoteFeedbackFilter(
      CANCoder canCoderRef, int remoteOrdinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configRemoteFeedbackFilter(
      BaseTalon talonRef, int remoteOrdinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSensorTerm(
      SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public double getSelectedSensorPosition(int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public double getSelectedSensorVelocity(int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode setSelectedSensorPosition(double sensorPos, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configForwardLimitSwitchSource(
      RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose,
      int deviceID,
      int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configReverseLimitSwitchSource(
      RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose,
      int deviceID,
      int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void overrideLimitSwitchesEnable(boolean enable) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configForwardSoftLimitThreshold(double forwardSensorLimit, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configReverseSoftLimitThreshold(double reverseSensorLimit, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public void overrideSoftLimitsEnable(boolean enable) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode config_IntegralZone(int slotIdx, double izone, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configAllowableClosedloopError(
      int slotIdx, double allowableCloseLoopError, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configAuxPIDPolarity(boolean invert, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public double getClosedLoopError(int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public double getIntegralAccumulator(int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public double getErrorDerivative(int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public void selectProfileSlot(int slotIdx, int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public double getClosedLoopTarget(int pidIdx) {
    throw new NotImplementedError();
  }

  @Override
  public double getActiveTrajectoryPosition() {
    throw new NotImplementedError();
  }

  @Override
  public double getActiveTrajectoryVelocity() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMotionCruiseVelocity(double sensorUnitsPer100ms, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMotionAcceleration(double sensorUnitsPer100msPerSec, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode clearMotionProfileTrajectories() {
    throw new NotImplementedError();
  }

  @Override
  public int getMotionProfileTopLevelBufferCount() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
    throw new NotImplementedError();
  }

  @Override
  public boolean isMotionProfileTopLevelBufferFull() {
    throw new NotImplementedError();
  }

  @Override
  public void processMotionProfileBuffer() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode changeMotionControlFramePeriod(int periodMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode getLastError() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode getFaults(Faults toFill) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode getStickyFaults(StickyFaults toFill) {
    return null;
  }

  @Override
  public ErrorCode clearStickyFaults(int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public int getFirmwareVersion() {
    throw new NotImplementedError();
  }

  @Override
  public boolean hasResetOccurred() {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public int configGetCustomParam(int paramIndex, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSetParameter(
      ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public ErrorCode configSetParameter(
      int param, double value, int subValue, int ordinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public double configGetParameter(int paramEnum, int ordinal, int timeoutMs) {
    throw new NotImplementedError();
  }

  @Override
  public int getBaseID() {
    throw new NotImplementedError();
  }

  @Override
  public int getDeviceID() {
    throw new NotImplementedError();
  }

  @Override
  public ControlMode getControlMode() {
    throw new NotImplementedError();
  }

  @Override
  public void follow(IMotorController masterToFollow) {
    throw new NotImplementedError();
  }

  @Override
  public void valueUpdated() {
    throw new NotImplementedError();
  }
}
