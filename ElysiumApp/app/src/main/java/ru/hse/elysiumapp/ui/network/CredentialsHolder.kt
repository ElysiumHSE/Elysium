package ru.hse.elysiumapp.ui.network

object CredentialsHolder {
    var BASE_URL = "http://10.0.2.2:8080/elysium/"
    private var _token: String = ""
    val token: String
        get() = _token

    fun updateToken(newToken: String) {
        _token = newToken
    }

}