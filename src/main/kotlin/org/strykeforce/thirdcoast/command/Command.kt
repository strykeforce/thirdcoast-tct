package org.strykeforce.thirdcoast.command

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.canifier.*
import org.strykeforce.thirdcoast.dio.RunDigitalOutputsCommand
import org.strykeforce.thirdcoast.dio.SelectDigitalOutputsCommand
import org.strykeforce.thirdcoast.servo.RunServosCommand
import org.strykeforce.thirdcoast.servo.SelectServosCommand
import org.strykeforce.thirdcoast.solenoid.RunSolenoidsCommand
import org.strykeforce.thirdcoast.solenoid.SelectSolenoidsCommand
import org.strykeforce.thirdcoast.swerve.AdjustAzimuthCommand
import org.strykeforce.thirdcoast.swerve.SaveZeroCommand
import org.strykeforce.thirdcoast.swerve.SelectAzimuthCommand
import org.strykeforce.thirdcoast.swerve.SetAzimuthCommand
import org.strykeforce.thirdcoast.talon.*

private val logger = KotlinLogging.logger {}

interface Command {
    val key: String
    val parent: Command?
    val menu: String
    val order: Int
    val children: Collection<Command>
    fun execute(): Command

    companion object {
        const val MENU_KEY = "menu"
        const val TYPE_KEY = "type"
        const val ORDER_KEY = "order"
        const val DEVICE_KEY = "device"

        fun createFromToml(toml: TomlTable, parent: MenuCommand? = null, key: String = "ROOT"): Command {

            val type = toml.getString(TYPE_KEY) ?: throw Exception("$key: $TYPE_KEY missing")
            logger.info { "type: $type, key: $key" }

            return when (type) {
                "menu" -> createMenuCommand(parent, key, toml)
                "talon.select" -> SelectTalonsCommand(parent, key, toml)
                "talon.mode" -> SelectControlModeCommand(parent, key, toml)
                "talon.brake" -> SelectBrakeModeCommand(parent, key, toml)
                "talon.run" -> RunTalonsCommand(parent, key, toml)
                "talon.status" -> TalonsStatusCommand(parent, key, toml)
                "talon.slot.select" -> SelectSlotCommand(parent, key, toml)
                "talon.param" -> TalonParameterCommand(parent, key, toml)
                "talon.sensor" -> SelectFeedbackSensorCommand(parent, key, toml)
                "talon.sensor.coefficient" -> FeedbackCoefficientCommand(parent, key, toml)
                "talon.sensor.position" -> SetSensorPositionCommand(parent, key, toml)
                "talon.hard.source" -> SelectHardLimitSourceCommand(parent, key, toml)
                "talon.hard.normal" -> SelectHardLimitNormalCommand(parent, key, toml)
                "talon.velocity.period" -> SelectVelocityMeasurmentPeriodCommand(parent, key, toml)
                "talon.commutation" -> SelectMotorCommutationCommand(parent, key, toml)
                "talon.absoluteRange" -> SelectAbsoluteSensorRange(parent, key, toml)
                "talon.initStrategy" -> SelectInitializationStrategy(parent, key, toml)
                "servo.select" -> SelectServosCommand(parent, key, toml)
                "servo.run" -> RunServosCommand(parent, key, toml)
                "solenoid.select" -> SelectSolenoidsCommand(parent, key, toml)
                "solenoid.run" -> RunSolenoidsCommand(parent, key, toml)
                "digital_output.select" -> SelectDigitalOutputsCommand(parent, key, toml)
                "digital_output.run" -> RunDigitalOutputsCommand(parent, key, toml)
                "canifier.select" -> SelectCanifierCommand(parent, key, toml)
                "canifier.status" -> CanifierStatusCommand(parent, key, toml)
                "canifier.param" -> CanifierParameterCommand(parent, key, toml)
                "canifier.inputs" -> CanifierGeneralInputsCommand(parent, key, toml)
                "canifier.pwm_in" -> CanifierPwmInputsCommand(parent, key, toml)
                "canifier.pwm_out" -> RunCanifierPwmCommand(parent, key, toml)
                "canifier.quad_in" -> CanifierQuadInputCommand(parent, key, toml)
                "swerve.azimuth" -> SetAzimuthCommand(parent, key, toml)
                "swerve.azimuth.save" -> SaveZeroCommand(parent, key, toml)
                "swerve.azimuth.select" -> SelectAzimuthCommand(parent, key, toml)
                "swerve.azimuth.adjust" -> AdjustAzimuthCommand(parent, key, toml)
                else -> DefaultCommand(parent, key, toml)
            }
        }

        private fun createMenuCommand(parent: MenuCommand?, key: String, toml: TomlTable): MenuCommand {
            val command = MenuCommand(parent, key, toml)
            toml.keySet().filter(toml::isTable)
                .forEach { k ->
                    val child = createFromToml(toml.getTable(k)!!, command, k)
                    command.children.add(child)
                    logger.info { "Create Menu: $k, ${command.validMenuChoices}" }
                }
            return command
        }
    }
}


abstract class AbstractCommand(
    final override val parent: Command?,
    final override val key: String,
    toml: TomlTable
) : Command, KoinComponent {
    override val order = toml.getLong(Command.ORDER_KEY)?.toInt() ?: 0
    protected val tomlMenu = toml.getString(Command.MENU_KEY) ?: key
    override val menu = tomlMenu
    override val children = emptySet<Command>()

    override fun execute() = parent ?: throw IllegalStateException("parent should not be null")

    val terminal: Terminal by inject()
    val reader: LineReader by inject()

    protected fun formatMenu(value: Boolean) = formatMenu(if (value) "yes" else "no")

    protected fun formatMenu(value: Int) = formatMenu(value.toString())

    protected fun formatMenu(value: Double, format: String = DOUBLE_FORMAT_3) = formatMenu(format.format(value))

    protected fun formatMenu(value: String) =
        "$tomlMenu: ${AttributedString(value, AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)).toAnsi()}"

    override fun toString(): String {
        var s = key
        val depth = depth()

        if (!children.isEmpty()) {
            s += " {\n"
            for (child in children) {
                s += "${indent(depth + 1)}$child\n"
            }
            s += "${indent(depth)}}"
        }
        return s
    }

    private fun depth(): Int {
        var depth = 0
        var arg = this.parent
        while (arg != null) {
            ++depth
            arg = arg.parent
        }
        return depth
    }

    private fun indent(nb: Int) = "  ".repeat(nb)

}
