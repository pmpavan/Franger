package com.frangerapp.franger.ui.profile

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.profile.ProfileModule
import com.frangerapp.franger.databinding.ActivityEditProfileBinding
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.profile.ProfileViewModel

import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

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
                .plus(ProfileModule(this@AddEditProfileActivity,true))
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

}
