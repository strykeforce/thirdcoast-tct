package org.strykeforce.thirdcoast.talon.phoenix6.run

import edu.wpi.first.wpilibj.Timer
import net.consensys.cava.toml.TomlTable
import org.strykeforce.controller.CTRE_Units
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.prompt
import org.strykeforce.thirdcoast.device.TalonFXsFDService
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.device.TalonFxsService
import org.koin.core.component.inject
import org.strykeforce.thirdcoast.warn
import mu.KotlinLogging
import org.jline.reader.EndOfFileException

private val logger = KotlinLogging.logger{}

class P6RunOpenLoopCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
):AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override fun execute(): Command {
        var done = false;
        while(!done) {
            try {
                val line = reader.readLine(prompt())
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val setpoint = setpoints[0].toDouble()
                val duration = if (setpoints.size > 1) setpoints[1].toDouble() else 0.0
                var units: CTRE_Units = CTRE_Units.Percent

                when(device) {
                    "fx" -> {
                        units = if(bus == "rio") talonFxService.active.firstOrNull()?.getOpenLoopUnits()?: CTRE_Units.Percent else talonFxFDService.active.firstOrNull()?.getOpenLoopUnits()?: CTRE_Units.Percent
                    }
                    "fxs" -> {
                        units = if(bus == "rio") talonFxsService.active.firstOrNull()?.getOpenLoopUnits()?: CTRE_Units.Percent else talonFxsFDService.active.firstOrNull()?.getOpenLoopUnits()?: CTRE_Units.Percent
                    }
                }

                //sanity checks
                if(units == CTRE_Units.Percent && !(-1.0..1.0).contains(setpoint)) {
                    terminal.warn("setpoint must be in range -1.0 to 1.0 for percent modes")
                    continue
                }

                logger.info { "Setpoint: $setpoint" }

                //run talon
                when(device) {
                    "fx" -> {
                        if(bus == "rio") {
                            talonFxService.active.forEach { it.runOpenLoop(setpoint) }
                        } else if(bus == "canivore") {
                            talonFxFDService.active.forEach { it.runOpenLoop(setpoint) }
                        } else throw IllegalArgumentException()
                    }
                    "fxs" -> {
                        if(bus == "rio") {
                            talonFxsService.active.forEach { it.runOpenLoop(setpoint) }
                        } else if(bus == "canivore") {
                            talonFxsFDService.active.forEach { it.runOpenLoop(setpoint) }
                        } else throw IllegalArgumentException()
                    }
                }

                //check timeout
                if(duration > 0.0) {
                    logger.debug{ "run duration = $duration seconds"}
                    Timer.delay(duration)
                    logger.debug{"run duration expired, setting to 0.0"}
                    when(device) {
                        "fx" -> {
                            if(bus == "rio") {
                                talonFxService.active.forEach { it.runOpenLoop(0.0) }
                            } else if(bus == "canivore") {
                                talonFxFDService.active.forEach { it.runOpenLoop(0.0) }
                            } else throw IllegalArgumentException()
                        }
                        "fxs" -> {
                            if(bus == "rio") {
                                talonFxsService.active.forEach { it.runOpenLoop(0.0) }
                            } else if(bus == "canivore") {
                                talonFxsFDService.active.forEach { it.runOpenLoop(0.0) }
                            } else throw IllegalArgumentException()
                        }
                    }
                }
            } catch(e : Exception) {
                done = true
            }
        }
        return super.execute()
    }
}