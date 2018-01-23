package com.frangerapp.franger.ui.splash

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.frangerapp.franger.R
import com.frangerapp.franger.databinding.ActivitySplashBinding
import com.frangerapp.franger.ui.BaseActivity
import com.frangerapp.franger.ui.login.LoginActivity
import com.frangerapp.franger.viewmodel.splash.SplashViewModel
import com.frangerapp.franger.viewmodel.splash.eventbus.SplashViewEvent
import com.frangerapp.franger.viewmodel.splash.util.SplashPresentationConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SplashActivity : BaseActivity() {

    companion object {
        val TAG = "SplashActivity"
    }

    private lateinit var viewDataBinding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        invokeDataBinding()
        onPageLoaded()
    }

    private fun invokeDataBinding() {
        viewModel = SplashViewModel()
        viewDataBinding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(splashViewEvent: SplashViewEvent) {
        when (splashViewEvent.id) {
            SplashPresentationConstants.NAVIGATE_TO_HOME_EVENT -> {

            }
            SplashPresentationConstants.NAVIGATE_TO_LOGIN_EVENT -> {
                moveToLoginPage()
            }
        }
    }

    private fun moveToLoginPage() {
        this@SplashActivity.startActivity(LoginActivity.newInstance(this@SplashActivity))
    }

}
