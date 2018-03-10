package com.frangerapp.franger.ui.invite

import android.app.Activity
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.invite.InviteModule
import com.frangerapp.franger.databinding.ActivityInviteBinding
import com.frangerapp.franger.ui.countries.CountriesListDialogFragment
import com.frangerapp.franger.ui.home.HomeActivity
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.invite.InviteUserViewModel
import com.frangerapp.franger.viewmodel.invite.eventbus.InviteUserEvent
import com.frangerapp.franger.viewmodel.invite.util.InviteUserPresentationConstants
import com.frangerapp.franger.viewmodel.login.eventbus.LoginViewEvent
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants
import kotlinx.android.synthetic.main.activity_invite.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@InviteActivity, factory).get(InviteUserViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@InviteActivity, R.layout.activity_invite)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()

    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        manageStatusBar(R.color.colorPrimary)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.menu_invite, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_next -> {
            viewModel.onNextClicked()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: InviteUserEvent) {
        when (loginViewEvent.id) {
            InviteUserPresentationConstants.ON_NEXT_BTN_CLICKED -> {
                goToHome()
            }
        }
    }
}
