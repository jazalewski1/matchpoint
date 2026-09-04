package dev.jazalewski1.matchpoint.feature.match.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

internal const val MAX_NAME_LENGTH = 24

internal data class NameFormUiState(
    val player1: Player = Player.Uninitialized,
    val player2: Player = Player.Uninitialized,
) {
    sealed interface Player {
        val name: String
        val isValid: Boolean

        data object Uninitialized : Player {
            override val name = ""
            override val isValid = true
        }

        data class Initialized(
            override val name: String,
            val errors: List<NameError> = validate(name),
        ) : Player {
            override val isValid: Boolean
                get() = errors.isEmpty()
        }
    }

    val isComplete: Boolean
        get() =
            if (player1 is Player.Initialized && player2 is Player.Initialized) {
                player1.isValid && player2.isValid
            } else {
                false
            }

    val errors: List<String>
        get() {
            val player1Errors = (player1 as? Player.Initialized)?.errors ?: emptyList()
            val player2Errors = (player2 as? Player.Initialized)?.errors ?: emptyList()
            return (player1Errors + player2Errors).distinct().map(NameError::toString)
        }
}

internal class NameInputStateHolder {
    var uiState by mutableStateOf(NameFormUiState())
        private set

    fun changePlayer1(newValue: String) {
        uiState = uiState.copy(player1 = NameFormUiState.Player.Initialized(newValue))
    }

    fun changePlayer2(newValue: String) {
        uiState = uiState.copy(player2 = NameFormUiState.Player.Initialized(newValue))
    }

    fun preparePlayer1() = uiState.player1.name.trim()

    fun preparePlayer2() = uiState.player2.name.trim()
}

private fun validate(text: String): List<NameError> {
    val newErrors = mutableListOf<NameError>()
    if (text.isBlank()) {
        newErrors.add(NameError.BLANK)
    }
    if (text.length > MAX_NAME_LENGTH) {
        newErrors.add(NameError.TOO_LONG)
    }
    return newErrors
}

internal enum class NameError {
    BLANK {
        override fun toString() = "Name cannot be empty."
    },
    TOO_LONG {
        override fun toString() = "Name cannot be longer than $MAX_NAME_LENGTH characters."
    },
}

@Composable internal fun rememberNameInputStateHolder() = remember { NameInputStateHolder() }
