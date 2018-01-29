package com.frangerapp.franger.ui.login

import android.os.Bundle
import android.support.design.widget.Snackbar
import com.frangerapp.franger.R

import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : LoginBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
