package org.strykeforce.thirdcoast

import com.nhaarman.mockitokotlin2.*
import net.consensys.cava.toml.Toml
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jline.reader.LineReader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.strykeforce.thirdcoast.command.Command
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_3
import org.strykeforce.thirdcoast.command.DOUBLE_FORMAT_4
import org.strykeforce.thirdcoast.command.MenuCommand

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
        fun `read int`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("42")
            assertThat(reader.readInt()).isEqualTo(42)
        }

        @Test
        fun `read double`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("27.67")
            assertThat(reader.readInt()).isEqualTo(27)
        }

        @ParameterizedTest
        @ValueSource(strings = ["ABC", "1, 2"])
        fun `rejects invalid input`(candidate: String) {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(candidate)
            assertThatExceptionOfType(NumberFormatException::class.java).isThrownBy {
                reader.readInt()
            }
        }
    }

    @Nested
    inner class DoubleCases {

        @Test
        fun `read default`() {
            whenever(reader.readLine(any(), isNull(), eq(DOUBLE_FORMAT_4.format(27.67)))).thenReturn(" ")
            assertThat(reader.readDouble(default = 27.67)).isEqualTo(27.67)
        }

        @Test
        fun `read double`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("27.67")
            assertThat(reader.readDouble()).isEqualTo(27.67)
        }

        @ParameterizedTest
        @ValueSource(strings = ["ABC", "1, 2"])
        fun `rejects invalid input`(candidate: String) {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(candidate)
            assertThatExceptionOfType(NumberFormatException::class.java).isThrownBy {
                reader.readDouble()
            }
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

        @ParameterizedTest
        @ValueSource(strings = ["1", "true"])
        fun `rejects invalid input`(candidate: String) {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(candidate)
            assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
                reader.readBoolean()
            }
        }
    }

    @Nested
    inner class IntListCases {

        @Test
        fun `read default`() {
            val default = listOf(1, 2)
            whenever(reader.readLine(any(), isNull(), eq(default.joinToString())))
                .thenReturn("  ")
            assertThat(reader.readIntList(default = default)).containsExactly(1, 2)
        }

        @Test
        fun `read single`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(" 2767 ")
            assertThat(reader.readIntList()).containsExactly(2767)
        }

        @Test
        fun `read list`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(" 1, 2,  3 ")
            assertThat(reader.readIntList()).containsExactly(1, 2, 3)
        }

        @ParameterizedTest
        @ValueSource(strings = ["1.23", "a,b,c", "4 5 6"])
        fun `rejects invalid input`(candidate: String) {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn(candidate)
            assertThatExceptionOfType(NumberFormatException::class.java).isThrownBy {
                reader.readIntList()
            }
        }
    }

    @Nested
    inner class MenuCases {

        private val toml = """
            ${Command.TYPE_KEY}="menu"
            [sandwich]
            ${Command.TYPE_KEY}="menu"
        """.trimIndent()
        private val menu = Command.createFromToml(Toml.parse(toml)) as MenuCommand

        @Test
        fun `gets menu choice`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("1")
            assertThat(reader.readMenu(menu)).isEqualTo(1)
        }

        @Test
        fun `gets back`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("b")
            assertThat(reader.readMenu(menu)).isEqualTo(BACK)
        }

        @Test
        fun `gets quit`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("Q")
            assertThat(reader.readMenu(menu)).isEqualTo(QUIT)
        }


        @Test
        fun `rejects invalid input`() {
            whenever(reader.readLine(any(), isNull(), any())).thenReturn("X").thenReturn("999")
            assertThat(reader.readMenu(menu)).isEqualTo(INVALID)
            assertThat(reader.readMenu(menu)).isEqualTo(INVALID)
        }
    }
}