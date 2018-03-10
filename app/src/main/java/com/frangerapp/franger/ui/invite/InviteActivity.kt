package com.frangerapp.franger.ui.invite

import android.app.Activity
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.invite.InviteModule
import com.frangerapp.franger.databinding.ActivityInviteBinding
import com.frangerapp.franger.ui.home.HomeActivity
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.invite.InviteUserViewModel
import kotlinx.android.synthetic.main.activity_invite.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class InviteActivity : UserBaseActivity(), LifecycleObserver {

    companion object {
        fun newInstance(activity: Activity): Intent {
            val intent = Intent(activity, InviteActivity::class.java)
            return intent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityInviteBinding
    private lateinit var viewModel: InviteUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@InviteActivity)
                .userComponent()
                .plus(InviteModule(this@InviteActivity))
                .inject(this@InviteActivity)
        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()

    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@InviteActivity, factory).get(InviteUserViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@InviteActivity, R.layout.activity_invite)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()

    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
    }

    private fun setupControllers() {

    }

    private fun onPageLoaded() {
        viewModel.checkForContactsPermission(this@InviteActivity)
    }

    private fun goToHome() {
        val intent = HomeActivity.newInstance(this@InviteActivity)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_next -> {
            goToHome()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
