package org.strykeforce.thirdcoast

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.log.Logger.SLF4JLogger
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.checkModules

class CheckModulesTest : KoinTest {

  @Test
  fun `dry run`() {
    checkModules(listOf(tctModule), logger = SLF4JLogger())
  }

  @AfterEach
  fun after() {
    stopKoin()
  }
}
