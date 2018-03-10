package com.frangerapp.franger.ui.contact

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.contact.ContactModule
import com.frangerapp.franger.databinding.ActivityContactBinding
import com.frangerapp.franger.databinding.ActivityInviteBinding
import com.frangerapp.franger.ui.invite.InviteActivity
import com.frangerapp.franger.viewmodel.contact.ContactViewModel
import com.frangerapp.franger.viewmodel.invite.InviteUserViewModel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ContactActivity : AppCompatActivity() {


    companion object {
        fun newInstance(activity: Activity): Intent {
            val intent = Intent(activity, ContactActivity::class.java)
            return intent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityContactBinding
    private lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@ContactActivity)
                .userComponent()
                .plus(ContactModule(this@ContactActivity))
                .inject(this@ContactActivity)
        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@ContactActivity, factory).get(ContactViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@ContactActivity, R.layout.activity_contact)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun setupViews() {


    }

    private fun setupControllers() {


    }

    private fun onPageLoaded() {


    }
}
