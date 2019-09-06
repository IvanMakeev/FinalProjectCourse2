package com.example.makeev.myfirstapplication.albums

import android.support.v4.app.Fragment
import com.example.makeev.myfirstapplication.view.SingleFragmentActivity

class AlbumsActivity : SingleFragmentActivity() {
    override val fragment: Fragment
        get() = AlbumsFragment.newInstance()

}