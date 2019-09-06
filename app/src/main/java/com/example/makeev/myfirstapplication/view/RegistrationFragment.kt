package com.example.makeev.myfirstapplication.view

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.utils.ApiUtils
import com.example.makeev.myfirstapplication.model.ErrorResponse
import com.example.makeev.myfirstapplication.model.User
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import retrofit2.HttpException

class RegistrationFragment : Fragment() {

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")

        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }

    private lateinit var login: EditText
    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var passwordAgain: EditText
    private lateinit var registration: Button
    private var disposable = CompositeDisposable()

    private val onRegistrationClickListener = View.OnClickListener {
        if (isInputValid()) {
            val user = User(
                    login.text.toString(),
                    name.text.toString(),
                    password.text.toString()
            )

           disposable.add(
                    ApiUtils.getApiService().registration(user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                showMessage(R.string.response_code_204)
                                fragmentManager?.popBackStack()
                            }, {
                                val error = it as HttpException

                                val errorResponse = Gson().fromJson(error.response().errorBody()!!.string(), ErrorResponse::class.java)
                                if (errorResponse.errors.email != null && errorResponse.errors.email.isNotEmpty()) {
                                    login.error = errorResponse.errors.email[0]
                                    login.requestFocus()
                                } else if (errorResponse.errors.name != null && errorResponse.errors.name.isNotEmpty()) {
                                    name.error = errorResponse.errors.name[0]
                                    name.requestFocus()
                                } else if (errorResponse.errors.password != null && errorResponse.errors.password.isNotEmpty()) {
                                    password.error = errorResponse.errors.password[0]
                                    passwordAgain.error = errorResponse.errors.password[0]
                                    password.requestFocus()
                                }
                                showMessage(ApiUtils.isServerError(error.response().code()))
                            })
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fr_registration, container, false)

        login = view.findViewById(R.id.etLogin)
        name = view.findViewById(R.id.etName)
        password = view.findViewById(R.id.etPassword)
        passwordAgain = view.findViewById(R.id.tvPasswordAgain)
        registration = view.findViewById(R.id.btnRegistration)

        registration.setOnClickListener(onRegistrationClickListener)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun isInputValid(): Boolean {
        return isEmailValid(login.text.toString()) && isPasswordsValid()
    }

    private fun isPasswordsValid(): Boolean {
        val password = password.text.toString()
        val passwordAgain = passwordAgain.text.toString()
        if (!(password == passwordAgain && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordAgain))) {
            this.password.error = getString(R.string.equal_password)
            this.passwordAgain.error = getString(R.string.equal_password)
            this.password.requestFocus()
            return false
        }
        when {
            password.length < 8 -> {
                this.password.error = getString(R.string.password_more)
                this.password.requestFocus()
                return false
            }
            passwordAgain.length < 8 -> {
                this.passwordAgain.error = getString(R.string.password_more)
                this.passwordAgain.requestFocus()
                return false
            }
        }
        this.password.error = null
        this.passwordAgain.error = null
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            login.error = getString(R.string.empty_login)
            login.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login.error = getString(R.string.incorrect_format)
            login.requestFocus()
            return false
        }
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showMessage(@StringRes string: Int) {
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show()
    }
}