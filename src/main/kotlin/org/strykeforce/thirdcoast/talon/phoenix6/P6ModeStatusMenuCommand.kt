package org.strykeforce.thirdcoast.talon.phoenix6

import mu.KotlinLogging
import net.consensys.cava.toml.TomlTable
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.*
import org.strykeforce.thirdcoast.command.*
import org.strykeforce.thirdcoast.device.TalonFxFDService
import org.strykeforce.thirdcoast.device.TalonFxService
import org.strykeforce.thirdcoast.invalidMenuChoice
import java.util.*

private val logger = KotlinLogging.logger {}

class P6ModeStatusMenuCommand(
    parent: Command?,
    key: String,
    toml: TomlTable
): MenuCommand(parent, key, toml) {
    private val talonFxService: TalonFxService by inject()
    private val talonFxFDService: TalonFxFDService by inject()

    val bus = toml.getString(Command.BUS_KEY) ?: throw Exception("$key: ${Command.BUS_KEY} missing")


    override val children = TreeSet<Command>(compareBy(Command::order, Command::key))

    override val menu: String
        get() {
            logger.info { "Getting Menu Option..." }
            if(bus == "rio") return formatMenu(talonFxService.controlMode)
            else if(bus == "canivore") return  formatMenu(talonFxFDService.controlMode)
            else throw IllegalArgumentException()

        }

    override fun execute(): Command {
        while(true){
            printMenu()
            val choice = readRawMenuChoice()
            when(choice) {
                INVALID -> terminal.invalidMenuChoice()
                BACK -> return parent ?: this
                QUIT -> return QuitCommand()
                else -> return children.elementAt(choice)
            }
        }
    }

    private fun printMenu() {
        logger.info { "Printing menu... " + children.size }
        val writer = terminal.writer()
        writer.println()
        children.forEachIndexed{ index, cmd ->
            writer.println(cmd.menu.toRawMenu(index))
            logger.info { "Child: " + cmd.menu + " " + cmd.key }
        }
        if(parent != null)
            writer.println("back to previous menu".toMenu("b"))
        writer.println("quit TCT".toMenu("Q"))
    }

    private fun readRawMenuChoice() = terminal.readRawMenu(children.size, prompt(), quit = true)
}