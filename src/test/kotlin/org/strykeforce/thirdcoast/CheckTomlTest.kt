package org.strykeforce.thirdcoast

import net.consensys.cava.toml.TomlTable
import org.assertj.core.api.Assertions.`as`
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.strykeforce.thirdcoast.talon.phoenix6.Phoenix6Parameter

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

    @Test
    fun `check phoenix6`() {
        val toml = parseResource("/phoenix6.toml")
        val table = toml["SUPPLY_V_TIME_CONST"] as TomlTable
        assertThat(table["type"]).isEqualTo("DOUBLE")
    }

    @Test
    fun `check cancoder`() {
        val toml = parseResource("/cancoder.toml")
        val table = toml["POSITION"] as TomlTable
        assertThat(table["type"]).isEqualTo("DOUBLE")
    }

    @Test
    fun `check canifier`() {
        val toml = parseResource("/canifier.toml")
        val table = toml["FACTORY_DEFAULTS"] as TomlTable
        assertThat(table["type"]).isEqualTo("BOOLEAN")
    }


    @Test
    fun `checkPigeon`() {
        val toml = parseResource("/pigeon.toml")
        val table = toml["ACCUM_Z_ANGLE"] as TomlTable
        assertThat(table["type"]).isEqualTo("DOUBLE")
    }
}
