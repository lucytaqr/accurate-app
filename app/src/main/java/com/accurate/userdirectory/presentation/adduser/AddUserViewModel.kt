package com.accurate.userdirectory.presentation.adduser

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accurate.userdirectory.core.common.UiText
import com.accurate.userdirectory.domain.model.Gender
import com.accurate.userdirectory.domain.repository.ActivityLogRepository
import com.accurate.userdirectory.domain.usecase.AddUserUseCase
import com.accurate.userdirectory.domain.usecase.GetUserByIdUseCase
import com.accurate.userdirectory.domain.usecase.ObserveCitiesUseCase
import com.accurate.userdirectory.domain.usecase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val observeCitiesUseCase: ObserveCitiesUseCase,
    private val activityLogRepository: ActivityLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddUserUiState())
    val state: StateFlow<AddUserUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeCitiesUseCase().collect { cities ->
                _state.update { it.copy(cities = cities) }
            }
        }
        val userId = savedStateHandle.get<String>("userId")
        if (!userId.isNullOrBlank()) {
            loadUser(userId)
        }
    }

    private fun loadUser(userId: String) {
        viewModelScope.launch {
            val user = getUserByIdUseCase(userId)
            if (user != null) {
                _state.update {
                    it.copy(
                        isEditMode = true,
                        editingUserId = userId,
                        name = user.name,
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        address = user.address,
                        city = user.city,
                        gender = user.gender,
                        photoUri = user.photoUri
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name, fieldErrors = it.fieldErrors - "name") }
    }

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, fieldErrors = it.fieldErrors - "email") }
    }

    fun onPhoneNumberChanged(phone: String) {
        _state.update { it.copy(phoneNumber = phone, fieldErrors = it.fieldErrors - "phoneNumber") }
    }

    fun onAddressChanged(address: String) {
        _state.update { it.copy(address = address) }
    }

    fun onCityChanged(city: String) {
        _state.update { it.copy(city = city, fieldErrors = it.fieldErrors - "city") }
    }

    fun onGenderChanged(gender: Gender) {
        _state.update { it.copy(gender = gender, fieldErrors = it.fieldErrors - "gender") }
    }

    fun onPhotoSelected(uri: String?) {
        _state.update { it.copy(photoUri = uri) }
    }

    fun onSubmit() {
        val currentState = _state.value
        val errors = AddUserFormValidator.validate(currentState)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, fieldErrors = emptyMap()) }

            if (currentState.isEditMode && currentState.editingUserId != null) {
                val result = updateUserUseCase(
                    localId = currentState.editingUserId,
                    name = currentState.name.trim(),
                    email = currentState.email.trim(),
                    phoneNumber = currentState.phoneNumber.trim(),
                    address = currentState.address.trim(),
                    city = currentState.city.trim(),
                    genderApiValue = currentState.gender!!.apiValue,
                    photoUri = currentState.photoUri
                )
                result.fold(
                    onSuccess = {
                        activityLogRepository.addLog("edit_user", "User Diperbarui", "User ${it.name} berhasil diperbarui")
                        _state.update { it.copy(isSubmitting = false, submitSuccess = true) }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isSubmitting = false, message = UiText.error(e.message ?: "Gagal memperbarui user")) }
                    }
                )
            } else {
                val result = addUserUseCase(
                    name = currentState.name.trim(),
                    email = currentState.email.trim(),
                    phoneNumber = currentState.phoneNumber.trim(),
                    address = currentState.address.trim(),
                    city = currentState.city.trim(),
                    genderApiValue = currentState.gender!!.apiValue,
                    photoUri = currentState.photoUri
                )
                result.fold(
                    onSuccess = {
                        activityLogRepository.addLog("add_user", "User Ditambahkan", "User ${it.name} berhasil ditambahkan")
                        _state.update { it.copy(isSubmitting = false, submitSuccess = true) }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isSubmitting = false, message = UiText.error(e.message ?: "Gagal menambahkan user")) }
                        activityLogRepository.addLog("add_user_failed", "Gagal Menambahkan User", e.message ?: "Unknown")
                    }
                )
            }
        }
    }

    fun onClearMessage() {
        _state.update { it.copy(message = null) }
    }
}
