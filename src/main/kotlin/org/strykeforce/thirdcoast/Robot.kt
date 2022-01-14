package org.strykeforce.thirdcoast

import edu.wpi.first.wpilibj.TimedRobot
import mu.KotlinLogging
import net.consensys.cava.toml.Toml
import net.consensys.cava.toml.TomlTable
import org.koin.core.parameter.parametersOf
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.strykeforce.telemetry.TelemetryService
import org.strykeforce.thirdcoast.command.Command
import kotlin.concurrent.thread
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class Robot : TimedRobot(), KoinComponent {

    override fun robotInit() {
        startKoin(listOf(tctModule, swerveModule), logger = SLF4JLogger())
        val telemetryService: TelemetryService by inject()
        thread(name = "tct", start = true) {
            val toml = parseResource("/commands.toml")
            val root = Command.createFromToml(toml)
            val shell: Shell by inject { parametersOf(root) }
            shell.run()
            exitProcess(0)
        }
    }

    override fun robotPeriodic() {}
    override fun disabledInit() {}
    override fun disabledPeriodic() {}
    override fun autonomousInit() {}
    override fun autonomousPeriodic() {}
    override fun teleopInit() {}
    override fun teleopPeriodic() {}
    override fun testInit() {}
    override fun testPeriodic() {}
}

fun parseResource(path: String): TomlTable {
    val toml = Toml.parse(loadResource(path))
    if (!toml.hasErrors()) return toml

    toml.errors().forEach {
        logger.error("{} at {}", it, it.position())
    }
    exitProcess(-1)
}

fun loadResource(resource: String): String = try {
    object {}.javaClass.getResource(resource).readText(Charsets.UTF_8)
} catch (all: Exception) {
    throw RuntimeException("Failed to load resource=$resource!", all)
}