package dev.jazalewski1.matchpoint.feature.match.setup

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

private const val validName1 = "One"
private const val validName2 = "Two"
private const val blankName = ""
private val tooLongName = "a".repeat(MAX_NAME_LENGTH + 1)

private val uninitializedPlayer = NameFormUiState.Player.Uninitialized
private val validPlayer1UiState =
    NameFormUiState.Player.Initialized(name = validName1, errors = emptyList())
private val validPlayer2UiState =
    NameFormUiState.Player.Initialized(name = validName2, errors = emptyList())
private val defaultUiState = NameFormUiState(player1 = uninitializedPlayer, player2 = uninitializedPlayer)

class NameInputStateHolderTest {
    @Test
    fun `initializes state`() {
        val holder = NameInputStateHolder()
        assertThat(holder.uiState).isEqualTo(defaultUiState)
        assertThat(holder.uiState.isComplete).isFalse()
        assertThat(holder.uiState.errors).isEmpty()
    }

    @Test
    fun `changes player1 name`() {
        val holder = NameInputStateHolder()
        holder.changePlayer1(validName1)
        assertThat(holder.uiState).isEqualTo(defaultUiState.copy(player1 = validPlayer1UiState))
        assertThat(holder.uiState.isComplete).isFalse()
        assertThat(holder.uiState.errors).isEmpty()
    }

    @Test
    fun `changes player2 name`() {
        val holder = NameInputStateHolder()
        holder.changePlayer2(validName2)
        assertThat(holder.uiState).isEqualTo(defaultUiState.copy(player2 = validPlayer2UiState))
        assertThat(holder.uiState.isComplete).isFalse()
        assertThat(holder.uiState.errors).isEmpty()
    }

    @Test
    fun `is complete`() {
        val holder = NameInputStateHolder()
        holder.changePlayer1(validName1)
        holder.changePlayer2(validName2)
        assertThat(holder.uiState).isEqualTo(
            NameFormUiState(player1 = validPlayer1UiState, player2 = validPlayer2UiState)
        )
        assertThat(holder.uiState.isComplete).isTrue()
        assertThat(holder.uiState.errors).isEmpty()
    }

    @Test
    fun `invalidates player1 name`() {
        val holder = NameInputStateHolder()

        holder.changePlayer1(blankName)
        assertThat(holder.uiState).isEqualTo(
            defaultUiState.copy(
                player1 =
                    NameFormUiState.Player.Initialized(name = blankName, errors = listOf(NameError.BLANK)),
            )
        )
        assertThat(holder.uiState.isComplete).isFalse()
        assertThat(holder.uiState.errors).containsExactly(NameError.BLANK.toString())

        holder.changePlayer1(tooLongName)
        assertThat(holder.uiState).isEqualTo(
            defaultUiState.copy(
                player1 =
                    NameFormUiState.Player.Initialized(name = tooLongName, errors = listOf(NameError.TOO_LONG)),
            )
        )
        assertThat(holder.uiState.errors).containsExactly(NameError.TOO_LONG.toString())
    }

    @Test
    fun `invalidates player2 name`() {
        val holder = NameInputStateHolder()

        holder.changePlayer2(blankName)
        assertThat(holder.uiState).isEqualTo(
            defaultUiState.copy(
                player2 =
                    NameFormUiState.Player.Initialized(name = blankName, errors = listOf(NameError.BLANK)),
            )
        )
        assertThat(holder.uiState.errors).containsExactly(NameError.BLANK.toString())

        holder.changePlayer2(tooLongName)
        assertThat(holder.uiState).isEqualTo(
            defaultUiState.copy(
                player2 =
                    NameFormUiState.Player.Initialized(name = tooLongName, errors = listOf(NameError.TOO_LONG)),
            )
        )
        assertThat(holder.uiState.errors).containsExactly(NameError.TOO_LONG.toString())
    }

    @Test
    fun `consolidates errors from both players`() {
        val holder = NameInputStateHolder()
        holder.changePlayer1(blankName)
        holder.changePlayer2(tooLongName)

        assertThat(holder.uiState.errors).containsExactly(NameError.BLANK.toString(), NameError.TOO_LONG.toString())
    }

    @Test
    fun `consolidates unique errors from both players`() {
        val holder = NameInputStateHolder()
        holder.changePlayer1(blankName)
        holder.changePlayer2(blankName)

        assertThat(holder.uiState.errors).containsExactly(NameError.BLANK.toString())
    }
}
