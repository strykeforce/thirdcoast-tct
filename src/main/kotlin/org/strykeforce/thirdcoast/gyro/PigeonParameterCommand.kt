package org.strykeforce.thirdcoast.gyro

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.sensors.PigeonIMU
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.PigeonService

private val logger = KotlinLogging.logger {}

class PigeonParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val pigeonService: PigeonService by inject()
    private val param = PigeonParameter.create(this, toml.getString("param")?: "UNKNOWN")

    override val menu: String
        get() {
            var xyzArray = DoubleArray(3)
            return when(param.enum){
                PigeonParameter.Enum.ACCUM_Z_ANGLE -> {
                    pigeonService.active.map { it.getAccumGyro(xyzArray) }
                    formatMenu(xyzArray[2])
                }
                PigeonParameter.Enum.FUSED_HEADING -> formatMenu(pigeonService.active.map { it.fusedHeading }.joinToString())
                PigeonParameter.Enum.TEMP_COMP_DISABLE -> formatMenu(pigeonService.tempCompensation)
                PigeonParameter.Enum.YAW -> {
                    pigeonService.active.map { it.getYawPitchRoll(xyzArray) }
                    formatMenu(xyzArray[0])
                }
                PigeonParameter.Enum.GENERAL_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_1_General))
                PigeonParameter.Enum.SIX_DEG_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR))
                PigeonParameter.Enum.FUSED_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_6_SensorFusion))
                PigeonParameter.Enum.GYRO_ACCUM_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel))
                PigeonParameter.Enum.GEN_COMPASS_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_2_GeneralCompass))
                PigeonParameter.Enum.GEN_ACCEL_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel))
                PigeonParameter.Enum.SIX_QUAT_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_10_SixDeg_Quat))
                PigeonParameter.Enum.MAG_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.RawStatus_4_Mag))
                PigeonParameter.Enum.BIAS_GYRO_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro))
                PigeonParameter.Enum.BIAS_MAG_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_4_Mag))
                PigeonParameter.Enum.BIAS_ACCEL_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_6_Accel))
                PigeonParameter.Enum.FACTORY_DEFAULT -> tomlMenu
                else -> TODO("${param.enum} not implemented")
            }
        }

    override fun execute(): Command {
        val timeout = pigeonService.timeout
        when(param.enum){
            PigeonParameter.Enum.ACCUM_Z_ANGLE -> {
                val xyzArray = DoubleArray(3)
                pigeonService.active.firstOrNull()?.getAccumGyro(xyzArray)
                val default = xyzArray[2]
                val paramValue = param.readDouble(reader, default)
                pigeonService.active.forEach { it.setAccumZAngle(paramValue, timeout) }
            }
            PigeonParameter.Enum.FUSED_HEADING -> {
                val default = pigeonService.active.firstOrNull()?.fusedHeading?: 0.0
                val paramValue = param.readDouble(reader, default)
                pigeonService.active.forEach { it.setFusedHeading(paramValue, timeout) }
            }
            PigeonParameter.Enum.TEMP_COMP_DISABLE -> configBooleanParam(pigeonService.tempCompensation){ pigeon, value ->
                pigeon.setTemperatureCompensationDisable(value, timeout)
                pigeonService.tempCompensation = value
            }
            PigeonParameter.Enum.YAW -> {
                val rpyArray = DoubleArray(3)
                pigeonService.active.firstOrNull()?.getYawPitchRoll(rpyArray)
                val default = rpyArray[0]
                val paramValue = param.readDouble(reader, default)
                pigeonService.active.forEach { it.setYaw(paramValue, timeout) }
            }
            PigeonParameter.Enum.GENERAL_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_1_General)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_1_General, value, timeout)
            }
            PigeonParameter.Enum.SIX_DEG_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, value, timeout)
            }
            PigeonParameter.Enum.FUSED_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_6_SensorFusion)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_6_SensorFusion, value, timeout)
            }
            PigeonParameter.Enum.GYRO_ACCUM_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_11_GyroAccum)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_11_GyroAccum, value, timeout)
            }
            PigeonParameter.Enum.GEN_COMPASS_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_2_GeneralCompass)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_2_GeneralCompass, value, timeout)
            }
            PigeonParameter.Enum.GEN_ACCEL_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel, value, timeout)
            }
            PigeonParameter.Enum.SIX_QUAT_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_10_SixDeg_Quat)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_10_SixDeg_Quat, value, timeout)
            }
            PigeonParameter.Enum.MAG_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.RawStatus_4_Mag)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.RawStatus_4_Mag, value, timeout)
            }
            PigeonParameter.Enum.BIAS_GYRO_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro, value, timeout)
            }
            PigeonParameter.Enum.BIAS_MAG_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_4_Mag)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_4_Mag, value, timeout)
            }
            PigeonParameter.Enum.BIAS_ACCEL_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_6_Accel)){ pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_6_Accel, value, timeout)
            }
            PigeonParameter.Enum.FACTORY_DEFAULT -> configBooleanParam(false){ pigeon, value ->
                if(value) pigeon.configFactoryDefault(timeout)
            }
            else -> TODO("${param.enum} not implemented")
        }
        return super.execute()
    }

    private fun configBooleanParam(default: Boolean, config: (PigeonIMU, Boolean) -> Unit) {
        val paramValue = param.readBoolean(reader, default)
        pigeonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${pigeonService.active.size} pigeon ${param.name}: $paramValue" }
    }

    private fun configDoubleParam(default: Double, config: (PigeonIMU, Double) -> Unit) {
        val paramValue = param.readDouble(reader, default)
        pigeonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${pigeonService.active.size} pigeon ${param.name}: $paramValue" }
    }

    private fun configIntParam(default: Int, config: (PigeonIMU, Int) -> Unit) {
        val paramValue = param.readInt(reader, default)
        pigeonService.active.forEach { config(it, paramValue) }
        logger.debug { "set ${pigeonService.active.size} pigeon ${param.name}: $paramValue" }
    }

    private fun defaultFor(frame: PigeonIMU_StatusFrame): Int {
        return pigeonService.active.first().getStatusFramePeriod(frame)
    }
}