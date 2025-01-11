package org.strykeforce.thirdcoast

import net.consensys.cava.toml.TomlTable
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

internal class CheckTomlTest {

    @Test
    fun `check commands`() {
        val toml = parseResource("/commands.toml")
        val table = toml["rio.talon.output.open-loop_ramp"] as TomlTable
        assertThat(table["type"]).isEqualTo("talon.param")
    }

    @Test
    fun `check talon`() {
        val toml = parseResource("/talon.toml")
        val table = toml["SLOT_P"] as TomlTable
        assertThat(table["type"]).isEqualTo("DOUBLE")
    }
}
