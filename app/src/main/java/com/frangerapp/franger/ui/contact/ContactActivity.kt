package com.frangerapp.franger.ui.contact

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.contact.ContactModule
import com.frangerapp.franger.databinding.ActivityContactBinding
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.contact.ContactViewModel
import com.frangerapp.franger.viewmodel.contact.eventbus.ContactEvent
import kotlinx.android.synthetic.main.activity_contact.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class ContactActivity : UserBaseActivity() {


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

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@ContactActivity, factory).get(ContactViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@ContactActivity, R.layout.activity_contact)
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
        viewModel.checkForContactsPermission(this@ContactActivity)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: ContactEvent) {
        when (loginViewEvent.id) {
//            ContactPresentaionConstants.ON_NEXT_BTN_CLICKED -> {
//            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contact, menu)

        val myActionMenuItem = menu.findItem(R.id.action_search)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onQueryTextChanged(newText)
                return true
            }

        })

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onViewDestroyed()
    }
}