package org.strykeforce.thirdcoast

import com.nhaarman.mockitokotlin2.*
import net.consensys.cava.toml.Toml
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jline.reader.LineReader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.strykeforce.thirdcoast.command.AbstractCommand
import org.strykeforce.thirdcoast.command.Command

internal class ReadersTest {

    private val reader: LineReader = mock()

    @BeforeEach
    fun init() {
        reset(reader)
    }

    @Nested
    inner class IntCases {

        @Test
        fun `read default`() {
            whenever(reader.readLine(any(), isNull(), eq("42"))).thenReturn(" ")
            assertThat(reader.readInt(default = 42)).isEqualTo(42)
        }

        @Test
        fun `read invalid`() {
            whenever(reader.readLine(any(), isNull(), any()))
                .thenReturn("27.67")
                .thenReturn("ABC")
                .thenReturn("1, 2")
                .thenReturn("42")
            assertThat(reader.readInt()).isEqualTo(42)
        }
    }

    @Nested
    inner class DoubleCases {

        @Test
        fun `read default`() {
            whenever(reader.readLine(any(), isNull(), eq("27.67"))).thenReturn(" ")
            assertThat(reader.readDouble(default = 27.67)).isEqualTo(27.67)
        }

        @Test
        fun `read invalid`() {
            whenever(reader.readLine(any(), isNull(), any()))
                .thenReturn("ABC", "1, 2", "27.67", "42")
            assertThat(reader.readDouble()).isEqualTo(27.67)
        }
    }

    @Nested
    inner class BooleanCases {

        @Test
        fun `read default`() {
            whenever(reader.readLine(any(), isNull(), eq("y"))).thenReturn(" ")
            assertThat(reader.readBoolean(default = true)).isTrue()
        }

        @Test
        fun `read yes`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("Y")
            assertThat(reader.readBoolean()).isTrue()
        }

        @Test
        fun `read no`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("N")
            assertThat(reader.readBoolean()).isFalse()
        }

        @Test
        fun `read invalid`() {
            whenever(reader.readLine(any(), isNull(), any()))
                .thenReturn("ABC", "1", "Y")
            assertThat(reader.readBoolean()).isTrue()
        }
    }

    @Nested
    inner class IntListCases {

        @Test
        fun `read default`() {
            whenever(reader.readLine(any(), isNull(), eq(""))).thenReturn("  ")
            assertThat(reader.readIntList()).isEmpty()
        }

        @Test
        fun `read single`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(" 2767 ")
            assertThat(reader.readIntList()).containsExactly(2767)
        }

        @Test
        fun `read list`() {
            whenever(reader.readLine(any(), isNull(), any()))
                .thenReturn("a,b,c")
                .thenReturn("4 5 6")
                .thenReturn(" 1, 2,  3 ")
            assertThat(reader.readIntList()).containsExactly(1, 2, 3)
        }
    }

    @Nested
    inner class MenuCases {

        private val toml = """
            type="menu"
            [sandwich]
            type="menu"
        """.trimIndent()
        private val menu = Command.createFromToml(Toml.parse(toml))

        @Test
        fun `throws if not menu`() {
            val command = object : AbstractCommand(null, "bogus", Toml.parse("")) {}

            assertThatThrownBy { reader.readMenu(command) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("MenuCommand")
        }

        @Test
        fun `gets menu choice`() {
            whenever(reader.readLine(any<String>())).thenReturn("1")
            assertThat(reader.readMenu(menu)).isEqualTo(1)
        }

        @Test
        fun `gets back`() {
            whenever(reader.readLine(any<String>())).thenReturn("b")
            assertThat(reader.readMenu(menu)).isEqualTo(BACK)
        }

        @Test
        fun `gets quit`() {
            whenever(reader.readLine(any<String>())).thenReturn("q")
            assertThat(reader.readMenu(menu)).isEqualTo(QUIT)
        }


        @Test
        fun `gets invalid input`() {
            whenever(reader.readLine(any<String>())).thenReturn("X").thenReturn("999")
            assertThat(reader.readMenu(menu)).isEqualTo(INVALID)
            assertThat(reader.readMenu(menu)).isEqualTo(INVALID)
        }
    }
}