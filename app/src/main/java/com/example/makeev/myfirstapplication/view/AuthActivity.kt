package com.example.makeev.myfirstapplication.view

import android.support.v4.app.Fragment

class AuthActivity : SingleFragmentActivity() {

     override val fragment: Fragment
        get() = AuthFragment.newInstance()
}