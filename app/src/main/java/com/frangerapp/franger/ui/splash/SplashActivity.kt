package com.frangerapp.franger.ui.splash

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.login.SplashModule
import com.frangerapp.franger.databinding.ActivitySplashBinding
import com.frangerapp.franger.ui.home.HomeActivity
import com.frangerapp.franger.ui.invite.InviteActivity
import com.frangerapp.franger.ui.login.LoginActivity
import com.frangerapp.franger.ui.login.LoginBaseActivity
import com.frangerapp.franger.ui.profile.AddEditProfileActivity
import com.frangerapp.franger.viewmodel.splash.SplashViewModel
import com.frangerapp.franger.viewmodel.splash.eventbus.SplashViewEvent
import com.frangerapp.franger.viewmodel.splash.util.SplashPresentationConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class SplashActivity : LoginBaseActivity() {

    companion object {
        val TAG = "SplashActivity"
    }

    private lateinit var viewDataBinding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@SplashActivity)
                .loginComponent()
                .plus(SplashModule(this@SplashActivity))
                .inject(this@SplashActivity)
        eventBus.register(this)
        invokeDataBinding()
        onPageLoaded()
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@SplashActivity, factory).get(SplashViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(splashViewEvent: SplashViewEvent) {
        when (splashViewEvent.id) {
            SplashPresentationConstants.NAVIGATE_TO_HOME_EVENT -> {
                moveToHomePage()
            }
            SplashPresentationConstants.NAVIGATE_TO_LOGIN_EVENT -> {
                moveToLoginPage()
            }
            SplashPresentationConstants.NAVIGATE_TO_USER_PROFILE_EVENT -> {
                moveToEditProfilePage()
            }
            SplashPresentationConstants.NAVIGATE_TO_ONBOARD_EVENT -> {
                moveToInvitePage()
            }
        }
    }

    private fun moveToLoginPage() {
        this@SplashActivity.startActivity(LoginActivity.newInstance(this@SplashActivity))
    }

    private fun moveToInvitePage() {
        this@SplashActivity.startActivity(InviteActivity.newInstance(this@SplashActivity))
    }

    private fun moveToEditProfilePage() {
        this@SplashActivity.startActivity(AddEditProfileActivity.newInstance(this@SplashActivity))
    }

    private fun moveToHomePage() {
        this@SplashActivity.startActivity(HomeActivity.newInstance(this@SplashActivity))
    }

}
