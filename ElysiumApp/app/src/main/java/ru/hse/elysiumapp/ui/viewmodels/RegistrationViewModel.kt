package ru.hse.elysiumapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class RegisteredUser(
    val message: String
)

data class RegistrationErrorOccurred(
    val message: String
)

data class RegistrationResult(
    val success: RegisteredUser? = null,
    val error: RegistrationErrorOccurred? = null,
)

data class RegistrationFormState(
    val isDataValid: Boolean = false
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    // Registration Connection
) : ViewModel() {

    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    private val _registrationFormState = MutableLiveData<RegistrationFormState>()
    val registrationFormState: LiveData<RegistrationFormState> = _registrationFormState

    fun register(username: String, password: String) {
        // TODO: register on server
        Thread.sleep(1000)
        _registrationResult.value = RegistrationResult(success = RegisteredUser(message = "User is successfully registered"))
    }

    fun registrationDataChanged(username: String, password: String, passwordConfirm: String) {
        if (isUsernameValid(username) && isPasswordValid(password, passwordConfirm)) {
            _registrationFormState.value = RegistrationFormState(isDataValid = true)
        } else {
            _registrationFormState.value = RegistrationFormState(isDataValid = false)
        }
    }

    private fun isUsernameValid(username: String) : Boolean {
        return username.isNotEmpty()
    }

    private fun isPasswordValid(password: String, passwordConfirm: String) : Boolean {
        return password.isNotEmpty() && password == passwordConfirm
    }
}