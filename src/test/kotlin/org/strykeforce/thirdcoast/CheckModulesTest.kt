package org.strykeforce.thirdcoast

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.logger.SLF4JLogger
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class CheckModulesTest : KoinTest {

    @Test
    fun `dry run`() {
        checkModules{
            listOf(tctModule)
            logger(SLF4JLogger())
        }
    }

    @AfterEach
    fun after() {
        stopKoin()
    }
}
