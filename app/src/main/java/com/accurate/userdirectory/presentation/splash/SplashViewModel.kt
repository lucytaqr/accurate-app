package com.accurate.userdirectory.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accurate.userdirectory.domain.usecase.RefreshCitiesUseCase
import com.accurate.userdirectory.domain.usecase.RefreshUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val refreshCitiesUseCase: RefreshCitiesUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            refreshUsersUseCase()
            refreshCitiesUseCase()
            delay(1500)
        }
    }
}
