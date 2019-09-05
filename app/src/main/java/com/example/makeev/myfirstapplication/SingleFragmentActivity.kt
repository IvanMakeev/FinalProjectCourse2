package com.example.makeev.myfirstapplication

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract val fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_single_fragment)
        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0)
            fragmentManager.popBackStack()
        else
            finish()
    }
}