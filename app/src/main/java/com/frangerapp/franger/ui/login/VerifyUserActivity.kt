package com.frangerapp.franger.ui.login

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.franger.mobile.logger.FRLogger
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.login.VerifyUserModule
import com.frangerapp.franger.databinding.ActivityVerifyBinding
import com.frangerapp.franger.ui.profile.AddEditProfileActivity
import com.frangerapp.franger.viewmodel.login.VerifyUserViewModel
import com.frangerapp.franger.viewmodel.login.eventbus.VerifyUserViewEvent
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class VerifyUserActivity : LoginBaseActivity() {


    companion object {
        val TAG = "VerifyUserActivity"
        @JvmStatic
        fun newInstance(activity: Activity): Intent {
            val homeIntent = Intent(activity, VerifyUserActivity::class.java)
            return homeIntent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityVerifyBinding
    private lateinit var viewModel: VerifyUserViewModel

    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@VerifyUserActivity)
                .loginComponent()
                .plus(VerifyUserModule(this@VerifyUserActivity))
                .inject(this@VerifyUserActivity)
        invokeDataBinding()
        messageFromAliens()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    private fun messageFromAliens() {
    }

    private fun setupViews() {
    }

    private fun setupControllers() {

    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@VerifyUserActivity, factory).get(VerifyUserViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@VerifyUserActivity, R.layout.activity_verify)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        btnLogin.dispose()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: VerifyUserViewEvent) {
        when (loginViewEvent.id) {
            LoginPresentationConstants.VALID_OTP_REQUEST_SUCCESS -> {
                viewDataBinding.btnLogin.doneLoadingAnimation(getColorRes(R.color.red),
                        BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
                Toast.makeText(this@VerifyUserActivity, loginViewEvent.message, Toast.LENGTH_SHORT).show()
                moveToEditProfilePage()
            }
            LoginPresentationConstants.VALID_OTP_REQUEST_FAIL -> {
                viewDataBinding.btnLogin.revertAnimation()
                Toast.makeText(this@VerifyUserActivity, loginViewEvent.message, Toast.LENGTH_SHORT).show()
            }
            LoginPresentationConstants.VALID_OTP_REQUEST_SENT -> {
                viewDataBinding.btnLogin.startAnimation()
            }
        }
    }

    private fun moveToEditProfilePage() {
        this@VerifyUserActivity.startActivity(AddEditProfileActivity.newInstance(this@VerifyUserActivity))
    }


}
