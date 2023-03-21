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

@HiltViewModel
class LoginViewModel @Inject constructor(
    // Login Connection
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // TODO login on server, update loginResult
        Thread.sleep(1_000)
        _loginResult.value = LoginResult(success = LoggedInUserView(message = "I'm in"))
    }
}