package org.strykeforce.thirdcoast.command

import net.consensys.cava.toml.Toml
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MenuCommandTest {

    private val toml = """
        type = "menu"
        [entry_1]
          type = "default"
          order = 1
          menu = "entry 1"
        [entry_2]
          type = "default"
          order = 2
          menu = "entry 2"
        [entry_3]
          type = "menu"
          order = 3
          menu = "entry 3"
        [entry_4]
          type = "default"
        [entry_5]
          type = "default"
        [entry_6]
          type = "default"
        [entry_7]
          type = "default"
        [entry_8]
          type = "default"
        [entry_9]
          type = "default"
        [entry_a]
          type = "default"
        [entry_c]
          type = "default"
        [entry_d]
          type = "default"
          [entry_d.entry_1]
            type = "default"
            order = 1
            menu = "entry d.1"
    """.trimIndent()

    @Test
    fun `add children`() {
        val menu = Command.createFromToml(Toml.parse(toml))
        assertThat(menu.children).hasSize(12)
    }

    @Test
    fun `read raw menu`() {
        val menu = Command.createFromToml(Toml.parse(toml)) as MenuCommand
        assertThat(menu.validMenuChoices).containsOnly(
            '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'c', 'd'
        )
    }
}