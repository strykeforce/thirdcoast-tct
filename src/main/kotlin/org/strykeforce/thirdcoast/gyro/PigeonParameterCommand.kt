package org.strykeforce.thirdcoast.gyro

import com.ctre.phoenix.sensors.PigeonIMU
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame
import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.device.PigeonService
import org.strykeforce.thirdcoast.gyro.PigeonParameter.Enum.*

private val logger = KotlinLogging.logger {}

class PigeonParameterCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
) : AbstractCommand(parent, key, toml) {

    private val pigeonService: PigeonService by inject()
    private val param = PigeonParameter.create(this, toml.getString("param") ?: "UNKNOWN")

    override val menu: String
        get() {
            var xyzArray = DoubleArray(3)
            return when (param.enum) {
                PigeonParameter.Enum.ACCUM_Z_ANGLE -> {
                    pigeonService.active.map { it.getAccumGyro(xyzArray) }
                    formatMenu(xyzArray[2])
                }
                FUSED_HEADING -> formatMenu(pigeonService.active.map { it.fusedHeading }.joinToString())
                TEMP_COMP_DISABLE -> formatMenu(pigeonService.tempCompensation)
                YAW -> {
                    pigeonService.active.map { it.getYawPitchRoll(xyzArray) }
                    formatMenu(xyzArray[0])
                }
                GENERAL_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_1_General))
                SIX_DEG_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR))
                FUSED_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_6_SensorFusion))
                GYRO_ACCUM_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel))
                GEN_COMPASS_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_2_GeneralCompass))
                GEN_ACCEL_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel))
                SIX_QUAT_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.CondStatus_10_SixDeg_Quat))
                MAG_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.RawStatus_4_Mag))
                BIAS_GYRO_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro))
                BIAS_MAG_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_4_Mag))
                BIAS_ACCEL_STATUS -> formatMenu(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_6_Accel))
                FACTORY_DEFAULT -> tomlMenu
            }
        }

    override fun execute(): Command {
        val timeout = pigeonService.timeout
        when (param.enum) {
            PigeonParameter.Enum.ACCUM_Z_ANGLE -> {
                val xyzArray = DoubleArray(3)
                pigeonService.active.firstOrNull()?.getAccumGyro(xyzArray)
                val default = xyzArray[2]
                val paramValue = param.readDouble(reader, default)
                pigeonService.active.forEach { it.setAccumZAngle(paramValue, timeout) }
            }
            FUSED_HEADING -> {
                val default = pigeonService.active.firstOrNull()?.fusedHeading ?: 0.0
                val paramValue = param.readDouble(reader, default)
                pigeonService.active.forEach { it.setFusedHeading(paramValue, timeout) }
            }
            TEMP_COMP_DISABLE -> configBooleanParam(pigeonService.tempCompensation) { pigeon, value ->
                pigeon.setTemperatureCompensationDisable(value, timeout)
                pigeonService.tempCompensation = value
            }
            YAW -> {
                val rpyArray = DoubleArray(3)
                pigeonService.active.firstOrNull()?.getYawPitchRoll(rpyArray)
                val default = rpyArray[0]
                val paramValue = param.readDouble(reader, default)
                pigeonService.active.forEach { it.setYaw(paramValue, timeout) }
            }
            GENERAL_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_1_General)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_1_General, value, timeout)
            }
            SIX_DEG_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, value, timeout)
            }
            FUSED_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_6_SensorFusion)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_6_SensorFusion, value, timeout)
            }
            GYRO_ACCUM_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_11_GyroAccum)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_11_GyroAccum, value, timeout)
            }
            GEN_COMPASS_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_2_GeneralCompass)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_2_GeneralCompass, value, timeout)
            }
            GEN_ACCEL_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_3_GeneralAccel, value, timeout)
            }
            SIX_QUAT_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.CondStatus_10_SixDeg_Quat)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_10_SixDeg_Quat, value, timeout)
            }
            MAG_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.RawStatus_4_Mag)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.RawStatus_4_Mag, value, timeout)
            }
            BIAS_GYRO_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro, value, timeout)
            }
            BIAS_MAG_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_4_Mag)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_4_Mag, value, timeout)
            }
            BIAS_ACCEL_STATUS -> configIntParam(defaultFor(PigeonIMU_StatusFrame.BiasedStatus_6_Accel)) { pigeon, value ->
                pigeon.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_6_Accel, value, timeout)
            }
            FACTORY_DEFAULT -> configBooleanParam(false) { pigeon, value ->
                if (value) pigeon.configFactoryDefault(timeout)
            }
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