package ru.hse.elysiumapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.elysiumapp.network.AuthProvider
import ru.hse.elysiumapp.network.LoginError
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
        authProvider.login(username, password) { result ->
            when (result) {
                LoginError.OK -> {
                    _loginResult.postValue(
                        LoginResult(success = LoggedInUserView(message = "Welcome, $username"))
                    )
                    Log.println(Log.INFO, "login", "logged in")
                }
                LoginError.INCORRECT_DATA -> {
                    _loginResult.postValue(
                        LoginResult(error = LoginErrorOccurred(message = "Wrong login or password"))
                    )
                    Log.println(Log.INFO, "login", "Wrong login or password")
                }
                LoginError.CALL_FAILURE -> {
                    _loginResult.postValue(
                        LoginResult(error = LoginErrorOccurred(message = "Something's wrong with network"))
                    )
                    Log.println(Log.INFO, "login", "Something's wrong with network")
                }
                LoginError.UNKNOWN_RESPONSE -> {
                    Log.println(Log.ERROR, "login", "Unknown response")
                }
            }
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