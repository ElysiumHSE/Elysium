package ru.hse.elysiumapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class LoggedInUserView(
    val message: String
)

data class LoginErrorOccurred(
    val message: String
)

data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: LoginErrorOccurred? = null
)

data class LoginFormState(
    val isDataValid: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    // Login Connection
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState : LiveData<LoginFormState> = _loginFormState

    fun login(username: String, password: String) {
        // TODO login on server, update loginResult
        Thread.sleep(1_000)
        _loginResult.value = LoginResult(success = LoggedInUserView(message = "I'm in"))
    }

    fun loginDataChanged(username: String, password: String) {
        if (isUsernameValid(username) && isPasswordValid(password)) {
            _loginFormState.value = LoginFormState(isDataValid = true)
        } else {
            _loginFormState.value = LoginFormState(isDataValid = false)
        }
    }

    private fun isUsernameValid(username: String) : Boolean {
        return username.isNotEmpty()
    }

    private fun isPasswordValid(password: String) : Boolean {
        return password.isNotEmpty()
    }
}