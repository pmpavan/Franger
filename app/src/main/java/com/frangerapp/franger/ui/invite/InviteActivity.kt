package com.frangerapp.franger.ui.invite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.frangerapp.franger.R
import com.frangerapp.franger.ui.home.HomeActivity
import com.frangerapp.franger.ui.user.UserBaseActivity

import kotlinx.android.synthetic.main.activity_invite.*

class InviteActivity : UserBaseActivity() {

    companion object {
        fun newInstance(activity: Activity): Intent {
            val intent = Intent(activity, InviteActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)
        setSupportActionBar(toolbar)


    }

}
