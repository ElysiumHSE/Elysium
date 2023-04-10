package ru.hse.elysiumapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.elysiumapp.network.AuthProvider
import ru.hse.elysiumapp.network.ErrorCode
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
    private val authProvider: AuthProvider
) : ViewModel() {


    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    fun login(username: String, password: String) {
        val result = authProvider.login(username, password)
        Log.println(Log.INFO, "login","got result")
        if (result == ErrorCode.OK) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(message = "Welcome, $username"))
        } else if (result == ErrorCode.LOGIN_INCORRECT_DATA) {
            _loginResult.value =
                LoginResult(error = LoginErrorOccurred(message = "Wrong login or password"))
        } else if (result == ErrorCode.CALL_FAILURE) {
            _loginResult.value =
                LoginResult(error = LoginErrorOccurred(message = "Something's wrong with network"))
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (isUsernameValid(username) && isPasswordValid(password)) {
            _loginFormState.value = LoginFormState(isDataValid = true)
        } else {
            _loginFormState.value = LoginFormState(isDataValid = false)
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotEmpty()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }
}