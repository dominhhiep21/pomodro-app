//package thong.kotlin.pomodoro.features.settings.presentation
//
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import thong.kotlin.pomodoro.core.data.AppPreferences
//import thong.kotlin.pomodoro.features.settings.domain.AuraBackgroundTheme
//
//data class SettingsUiState(
//    val selectedTheme: AuraBackgroundTheme = AuraBackgroundTheme.DEFAULT,
//    val unlockedThemeIds: Set<String> = emptySet(), // Danh sách ID các hình nền đã sở hữu
//    val availableThemes: List<AuraBackgroundTheme> = AuraBackgroundTheme.entries,
//    val themeToBuy: AuraBackgroundTheme? = null     // Trạng thái chờ xác nhận mua
//)
//
//class SettingsViewModel(
//    private val preferences: AppPreferences,
//    private val viewModelScope: CoroutineScope
//) {
//    private val _uiState = MutableStateFlow(SettingsUiState())
//    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
//
//    init {
//        loadUserData()
//    }
//
//    private fun loadUserData() {
//        viewModelScope.launch {
//            val savedId = preferences.getSelectedBackgroundId()
//            val unlockedIds = preferences.getUnlockedBackgroundIds() // Lấy từ DB
//
//            val theme = AuraBackgroundTheme.entries.find { it.id == savedId } ?: AuraBackgroundTheme.DEFAULT
//
//            _uiState.update { it ->
//                it.copy(
//                    selectedTheme = theme,
//                    // Mặc định nạp luôn các theme miễn phí vào danh sách "Đã sở hữu"
//                    unlockedThemeIds = unlockedIds + AuraBackgroundTheme.entries.filter { !it.isPremium }.map { it.id }.toSet()
//                )
//            }
//        }
//    }
//
//    // Khi người dùng bấm vào một tấm thẻ trong cửa hàng
//    fun handleThemeClick(theme: AuraBackgroundTheme) {
//        val isUnlocked = _uiState.value.unlockedThemeIds.contains(theme.id)
//
//        if (isUnlocked) {
//            // Đã mua hoặc miễn phí -> Đổi hình nền ngay
//            _uiState.update { it.copy(selectedTheme = theme) }
//            viewModelScope.launch { preferences.saveSelectedBackgroundId(theme.id) }
//        } else {
//            // Chưa mua -> Bật hộp thoại hỏi mua
//            _uiState.update { it.copy(themeToBuy = theme) }
//        }
//    }
//
//    // Tắt hộp thoại mua hàng
//    fun cancelPurchase() {
//        _uiState.update { it.copy(themeToBuy = null) }
//    }
//
//    // Thực hiện giao dịch mua
//    fun confirmPurchase(theme: AuraBackgroundTheme) {
//        viewModelScope.launch {
//            // 1. Logic trừ tiền/trừ điểm Pomo ở đây...
//
//            // 2. Thêm vào danh sách đã mở khóa
//            val newUnlockedList = _uiState.value.unlockedThemeIds + theme.id
//            preferences.saveUnlockedBackgroundIds(newUnlockedList)
//
//            // 3. Tự động áp dụng hình nền vừa mua và đóng hộp thoại
//            _uiState.update {
//                it.copy(
//                    unlockedThemeIds = newUnlockedList,
//                    selectedTheme = theme,
//                    themeToBuy = null
//                )
//            }
//        }
//    }
//}