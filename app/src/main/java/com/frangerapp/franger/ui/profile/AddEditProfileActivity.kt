package com.frangerapp.franger.ui.profile

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.profile.ProfileModule
import com.frangerapp.franger.databinding.ActivityEditProfileBinding
import com.frangerapp.franger.ui.invite.InviteActivity
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.profile.ProfileViewModel
import com.frangerapp.franger.viewmodel.profile.eventbus.ProfileViewEvent
import com.frangerapp.franger.viewmodel.profile.util.ProfilePresentationConstants
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import android.databinding.adapters.TextViewBindingAdapter.setText
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener


class AddEditProfileActivity : UserBaseActivity() {

    companion object {
        fun newInstance(activity: Activity): Intent {
            val intent = Intent(activity, AddEditProfileActivity::class.java)
            return intent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@AddEditProfileActivity)
                .userComponent()
                .plus(ProfileModule(this@AddEditProfileActivity, true))
                .inject(this@AddEditProfileActivity)
        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
    }

    private fun setupControllers() {
//        viewDataBinding.btnLogin.revertAnimation({
//            if (viewModel.ifRequestSucceeded)
//                moveToInviteActivity()
//        })
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@AddEditProfileActivity, factory).get(ProfileViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@AddEditProfileActivity, R.layout.activity_edit_profile)
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
        viewModel.onViewDestroyed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: ProfileViewEvent) {
        when (loginViewEvent.id) {
            ProfilePresentationConstants.ON_PROFILE_REQUEST_SUCCESS -> {
                viewDataBinding.btnLogin.doneLoadingAnimation(getColorRes(R.color.red),
                        BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
                Handler().postDelayed({ moveToInviteActivity() }, 500)
            }
            ProfilePresentationConstants.ON_PROFILE_REQUEST_FAILURE -> {
                viewDataBinding.btnLogin.revertAnimation()
                Toast.makeText(this@AddEditProfileActivity, loginViewEvent.message, Toast.LENGTH_SHORT).show()
            }
            ProfilePresentationConstants.ON_PROFILE_REQUEST_SENT -> {
                viewDataBinding.btnLogin.startAnimation()
            }
        }
    }

    private fun moveToInviteActivity() {
        val intent = InviteActivity.newInstance(this@AddEditProfileActivity)
        startActivity(intent)
        finish()
    }
}
