package ru.hse.elysiumapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.elysiumapp.ui.network.AuthProvider
import ru.hse.elysiumapp.ui.network.ErrorCode
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
    private val authProvider: AuthProvider
) : ViewModel() {

    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    private val _registrationFormState = MutableLiveData<RegistrationFormState>()
    val registrationFormState: LiveData<RegistrationFormState> = _registrationFormState

    fun register(username: String, password: String) {
        val result = authProvider.register(username, password)
        Thread.sleep(1000)
        if (result == ErrorCode.OK) {
            _registrationResult.value =
                RegistrationResult(success = RegisteredUser(message = "$username is successfully registered"))
        } else if (result == ErrorCode.REGISTER_USER_ALREADY_EXISTS) {
            _registrationResult.value =
                RegistrationResult(error = RegistrationErrorOccurred(message = "$username is already registered"))
        } else if (result == ErrorCode.CALL_FAILURE) {
            _registrationResult.value =
                RegistrationResult(error = RegistrationErrorOccurred(message = "Something's wrong with network"))
        }
    }

    fun registrationDataChanged(username: String, password: String, passwordConfirm: String) {
        if (isUsernameValid(username) && isPasswordValid(password, passwordConfirm)) {
            _registrationFormState.value = RegistrationFormState(isDataValid = true)
        } else {
            _registrationFormState.value = RegistrationFormState(isDataValid = false)
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotEmpty()
    }

    private fun isPasswordValid(password: String, passwordConfirm: String): Boolean {
        return password.isNotEmpty() && password == passwordConfirm
    }
}