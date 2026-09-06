package dev.jazalewski1.matchpoint.core.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

fun Modifier.contentDesc(string: String) = this.semantics { contentDescription = string }
