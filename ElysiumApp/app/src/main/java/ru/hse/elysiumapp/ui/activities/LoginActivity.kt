package ru.hse.elysiumapp.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import ru.hse.elysiumapp.R
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.elysiumapp.databinding.ActivityLoginBinding
import ru.hse.elysiumapp.ui.viewmodels.LoggedInUserView
import ru.hse.elysiumapp.ui.viewmodels.LoginErrorOccurred
import ru.hse.elysiumapp.ui.viewmodels.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val register = binding.register
        val loading = binding.loading

        login.setBackgroundColor(resources.getColor(R.color.inactiveBlue))

        register.setOnClickListener {
            closeKeyboard()
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                provideLoginError(loginResult.error)
            } else if (loginResult.success != null) {
                provideLoginSuccess(loginResult.success)
                setResult(RESULT_OK)
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        })

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginFormState = it ?: return@Observer

            login.isEnabled = loginFormState.isDataValid
            if (login.isEnabled) {
                login.setBackgroundColor(resources.getColor(R.color.colorAccent))
            } else {
                login.setBackgroundColor(resources.getColor(R.color.inactiveBlue))
            }
        })

        login.setOnClickListener {
            closeKeyboard()
            loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString())
        }

        username.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun provideLoginError(error: LoginErrorOccurred) {
        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
    }

    private fun provideLoginSuccess(success: LoggedInUserView) {
        Toast.makeText(applicationContext, success.message, Toast.LENGTH_SHORT).show()
    }

    private fun closeKeyboard() {
        val view = this@LoginActivity.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}