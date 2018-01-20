package com.frangerapp.franger.ui.splash

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.frangerapp.franger.R
import com.frangerapp.franger.databinding.ActivitySplashBinding
import com.frangerapp.franger.presentation.splash.SplashViewModel
import com.frangerapp.franger.ui.BaseActivity

class SplashActivity : BaseActivity() {

    companion object {
        val TAG = "SplashActivity"
    }

    private lateinit var viewDataBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invokeDataBinding()
    }

    private fun invokeDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
        viewDataBinding.vm = SplashViewModel()
        viewDataBinding.executePendingBindings()
    }

}
