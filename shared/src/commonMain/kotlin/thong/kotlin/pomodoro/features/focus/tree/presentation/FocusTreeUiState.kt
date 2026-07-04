package thong.kotlin.pomodoro.features.focus.tree.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import thong.kotlin.pomodoro.features.focus.tree.data.FocusTreeRepository
import thong.kotlin.pomodoro.features.focus.tree.domain.FocusTreeRecord

data class FocusTreeUiState(
    val focusTreeRecord: FocusTreeRecord? = null,
    val isLoading: Boolean = false
)

class FocusTreeViewModel(
    private val repository: FocusTreeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusTreeUiState())
    val uiState: StateFlow<FocusTreeUiState> = _uiState

    fun loadFocusTree() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val tree = repository.getFocusTree()

            _uiState.value = FocusTreeUiState(
                focusTreeRecord = tree,
                isLoading = false
            )
        }
    }

    fun resetTree() {
        viewModelScope.launch {
            repository.resetFocusTree()
            loadFocusTree()
        }
    }
}