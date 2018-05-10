package com.frangerapp.franger.ui.contact

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.contact.ContactModule
import com.frangerapp.franger.databinding.ActivityContactBinding
import com.frangerapp.franger.ui.chat.ChatActivity
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.contact.ContactListItemViewModel
import com.frangerapp.franger.viewmodel.contact.ContactViewModel
import com.frangerapp.franger.viewmodel.contact.eventbus.ContactEvent
import com.frangerapp.franger.viewmodel.contact.util.ContactPresentaionConstants
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
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
    }

    override fun onResume() {
        super.onResume()
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
        manageStatusBar(R.color.statusbar_color)

    }

    private fun setupControllers() {

    }

    private fun onPageLoaded() {
        checkForContactsPermission(this@ContactActivity)
        viewModel.onPageLoaded()
    }

    private fun checkForContactsPermission(activity: Activity) {
        val rxPermissions = RxPermissions(activity)
        val disposable = rxPermissions.request(Manifest.permission.READ_CONTACTS)
                .subscribe { granted ->
                    viewModel.handleReadContactsPermission(granted)
                }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: ContactEvent) {
        when (loginViewEvent.id) {
            ContactPresentaionConstants.ON_CONTACT_ITEM_CLCKD -> {
                val contact = loginViewEvent.contactObj
                goToChatActivity(contact)
            }
        }
    }

    private fun goToChatActivity(contact: ContactListItemViewModel) {
        val intent = ChatActivity.newInstance(this@ContactActivity, viewModel.getContactModel(contact), false, "", false)
        startActivity(intent)
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            killPage()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun killPage() {
        this@ContactActivity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onViewDestroyed()
    }


}
