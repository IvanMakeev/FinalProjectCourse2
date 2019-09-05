package com.example.makeev.myfirstapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.makeev.myfirstapplication.model.User

class ProfileActivity : AppCompatActivity() {

    private lateinit var mLogin: TextView
    private lateinit var mName: TextView
    private lateinit var mUser: User

    companion object {
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_profile)

        mLogin = findViewById(R.id.tvEmail)
        mName = findViewById(R.id.tvName)

        val bundle = intent.extras
        mUser = bundle!!.get(USER_KEY) as User
        mLogin.text = mUser.login
        mName.text = mUser.name

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionLogout -> {
                startActivity(Intent(this, AuthActivity::class.java))

                finish()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
