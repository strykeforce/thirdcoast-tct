package org.strykeforce.thirdcoast.talon.phoenix6.run

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

private val logger = KotlinLogging.logger{}
class P6SetupFollowerCommand(
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
                if (line.isEmpty()) throw EndOfFileException()
                val setpoints = line.split(',')
                val leader = setpoints[0].toInt()


                //run talon
                when (device) {
                    "fx" -> {
                        if (bus == "rio") {
                            talonFxService.active.forEach { it.setupFollower(leader) }
                        } else if (bus == "canivore") {
                            talonFxFDService.active.forEach { it.setupFollower(leader) }
                        } else throw IllegalArgumentException()
                    }

                    "fxs" -> {
                        if (bus == "rio") {
                            talonFxsService.active.forEach { it.setupFollower(leader) }
                        } else if (bus == "canivore") {
                            talonFxsFDService.active.forEach { it.setupFollower(leader) }
                        } else throw IllegalArgumentException()
                    }
                }

            } catch(e: Exception) {
                done = true
            }
        }
        return super.execute()
    }
}