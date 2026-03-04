package com.example.bloodsugarindicator

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.bloodsugarindicator.ui.theme.HighColor
import com.example.bloodsugarindicator.ui.theme.LowColor
import com.example.bloodsugarindicator.ui.theme.NormalColor
import com.example.bloodsugarindicator.ui.theme.PrediabeticColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GlucoseUiState(
    val mmolInput: String = "",
    val mgDlOutput: String = "0",
    val status: String = "Enter Value",
    val statusColor: Color = NormalColor,
    val isValid: Boolean = true
)

class GlucoseViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GlucoseUiState())
    val uiState: StateFlow<GlucoseUiState> = _uiState.asStateFlow()

    fun onMmolInputChange(newValue: String) {
        // Validation: Only allow digits and one decimal point
        if (newValue.isNotEmpty() && !newValue.matches(Regex("""^\d*\.?\d*$"""))) {
            return
        }

        _uiState.update { it.copy(mmolInput = newValue) }
        convert(newValue)
    }

    private fun convert(input: String) {
        val mmol = input.toDoubleOrNull()
        if (mmol == null || input.isEmpty()) {
            _uiState.update {
                it.copy(
                    mgDlOutput = "0",
                    status = "Enter Value",
                    statusColor = NormalColor,
                    isValid = input.isEmpty()
                )
            }
            return
        }

        val mgDl = (mmol * 18).toInt()
        val (status, color) = getStatusAndColor(mmol)

        _uiState.update {
            it.copy(
                mgDlOutput = mgDl.toString(),
                status = status,
                statusColor = color,
                isValid = true
            )
        }
    }

    private fun getStatusAndColor(mmol: Double): Pair<String, Color> {
        return when {
            mmol < 3.9 -> "Low Range" to LowColor
            mmol in 3.9..7.8 -> "Normal Range" to NormalColor
            mmol in 7.8..11.0 -> "Prediabetic Range" to PrediabeticColor
            else -> "High Range" to HighColor
        }
    }
}
