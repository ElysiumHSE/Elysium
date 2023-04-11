package ru.hse.elysiumapp.network

enum class LoginError {
    OK,
    CALL_FAILURE,
    UNKNOWN_RESPONSE,
    INCORRECT_DATA,
}

enum class RegisterError {
    OK,
    CALL_FAILURE,
    UNKNOWN_RESPONSE,
    USER_ALREADY_EXISTS
}