package org.strykeforce.thirdcoast

import org.koin.core.parameter.parametersOf
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.strykeforce.thirdcoast.command.Command

class Main : KoinComponent, Runnable {

    override fun run() {
        val toml = parseResource("/test.toml")
        val root = Command.createFromToml(toml)
        val shell: Shell by inject { parametersOf(root) }
        shell.run()
    }
}

/**
 * Entry for command-line testing on laptop.
 */
fun main() {
    StandAloneContext.startKoin(listOf(tctModule), logger = SLF4JLogger())
    Main().run()
}