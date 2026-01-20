package org.strykeforce.thirdcoast.talon.phoenix6

import net.consensys.cava.toml.TomlTable
import org.strykeforce.controller.CTRE_ClosedLoopType
import org.strykeforce.controller.MotionMagicType
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

class P6LoadFromJsonCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): AbstractCommand(parent, key, toml) {

    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()
    private val talonFxsService: TalonFxsService by inject()
    private val talonFxsFDService: TalonFXsFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")
    val device = toml.getString(Command.DEVICE_KEY) ?: throw Exception("$key: ${Command.DEVICE_KEY} missing")

    override fun execute(): Command {
        var done = false
        while(!done) {
            try {

                val line = reader.readLine(prompt())
//                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val suffix = setpoints[0].toString()

                when(device) {
                    "fx" -> {
                        if(bus == "rio"){
                            talonFxService.active.forEach { it.loadFromJSON(suffix) }
                            talonFxService.dirty = true
                            talonFxService.dirty1 = true
                            talonFxService.dirty2 = true
                            talonFxService.dirty3 = true
                            talonFxService.dirty4 = true
                            talonFxService.dirty5 = true
                        }
                        else if(bus == "canivore"){
                            talonFxFDService.active.forEach { it.loadFromJSON(suffix) }
                            talonFxFDService.dirty = true
                            talonFxFDService.dirty1 = true
                            talonFxFDService.dirty2 = true
                            talonFxFDService.dirty3 = true
                            talonFxFDService.dirty4 = true
                            talonFxFDService.dirty5 = true
                        }
                        else throw IllegalArgumentException()
                    }
                    "fxs" -> {
                        if(bus == "rio"){
                            talonFxsService.active.forEach { it.loadFromJSON(suffix) }
                            talonFxsService.dirty = true
                            talonFxsService.dirty1 = true
                            talonFxsService.dirty2 = true
                            talonFxsService.dirty3 = true
                            talonFxsService.dirty4 = true
                            talonFxsService.dirty5 = true
                        }
                        else if(bus == "canivore"){
                            talonFxsFDService.active.forEach { it.loadFromJSON(suffix) }
                            talonFxsFDService.dirty = true
                            talonFxsFDService.dirty1 = true
                            talonFxsFDService.dirty2 = true
                            talonFxsFDService.dirty3 = true
                            talonFxsFDService.dirty4 = true
                            talonFxsFDService.dirty5 = true
                        }
                        else throw IllegalArgumentException()
                    }
                }
                done = true

            } catch (e: Exception) {
                done = true
            }
        }

        return super.execute()
    }
}