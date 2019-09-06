package com.example.makeev.myfirstapplication.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.utils.ApiUtils
import com.example.makeev.myfirstapplication.albums.AlbumsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class AuthFragment : Fragment() {

    companion object {

        fun newInstance(): AuthFragment {
            val args = Bundle()

            val fragment = AuthFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var email: AutoCompleteTextView
    private lateinit var password: EditText
    private lateinit var enter: Button
    private lateinit var register: Button
    private val disposable = CompositeDisposable()
    private var isErrorUserAuth = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fr_auth, container, false)

        email = v.findViewById(R.id.etLogin)
        password = v.findViewById(R.id.etPassword)
        enter = v.findViewById(R.id.buttonEnter)
        register = v.findViewById(R.id.buttonRegister)

        enter.setOnClickListener(onEnterClickListener)
        register.setOnClickListener(onRegisterClickListener)
        email.onFocusChangeListener = onLoginFocusChangeListener

        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private val onEnterClickListener = View.OnClickListener {
        if (isEmailValid() && isPasswordValid()) {
            val client = ApiUtils.getBasicAuthClient(
                    email.text.toString(),
                    password.text.toString(),
                    true)
            if (isErrorUserAuth) {
                ApiUtils.changeUserRuntime(email.text.toString(), password.text.toString())
            }

            disposable.add(ApiUtils.getApiService(client)
                    .getUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        startActivity(Intent(activity, AlbumsActivity::class.java))
                        activity!!.finish()
                    }, { throwable ->
                        if (throwable is HttpException) {
                            if (throwable.response().code() == 401) {
                                email.error = getString(R.string.auth_error)
                                password.error = getString(R.string.auth_error)
                                isErrorUserAuth = true
                            }
                            showMessage(ApiUtils.isServerError(throwable.response().code()))
                        }
                        if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.javaClass)) {
                            showMessage(R.string.check_connection)
                        }
                    })
            )
        }
    }

    private val onRegisterClickListener = View.OnClickListener {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.fragmentContainer, RegistrationFragment.newInstance())
                .addToBackStack(RegistrationFragment::class.java.name)
                .commit()
    }

    private val onLoginFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            Handler(Looper.getMainLooper()).post { email.showDropDown() }
        }
    }

    private fun isEmailValid(): Boolean {
        if (TextUtils.isEmpty(email.text)) {
            email.error = getString(R.string.empty_login)
            email.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
            email.error = getString(R.string.incorrect_format)
            email.requestFocus()
            return false
        }
        return !TextUtils.isEmpty(email.text) && Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
    }

    private fun isPasswordValid(): Boolean {
        if (TextUtils.isEmpty(password.text)) {
            password.error = getString(R.string.empty_password)
            email.requestFocus()
            return false
        }
        password.error = null
        return true
    }

    private fun showMessage(@StringRes string: Int) {
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show()
    }
}
