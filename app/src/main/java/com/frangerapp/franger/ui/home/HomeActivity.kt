package com.frangerapp.franger.ui.home

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.AppUtils
import com.frangerapp.franger.app.util.di.module.user.home.HomeModule
import com.frangerapp.franger.databinding.ActivityHomeBinding
import com.frangerapp.franger.domain.chat.model.ChatContact
import com.frangerapp.franger.ui.chat.ChatActivity
import com.frangerapp.franger.ui.contact.ContactActivity
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.ui.util.ActivityConstants
import com.frangerapp.franger.viewmodel.home.HomeViewModel
import com.frangerapp.franger.viewmodel.home.eventbus.HomeEvent
import com.frangerapp.franger.viewmodel.home.eventbus.IncomingListEvent
import com.frangerapp.franger.viewmodel.home.eventbus.OutgoingListEvent
import com.frangerapp.franger.viewmodel.home.util.HomePresentationConstants
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HomeActivity : UserBaseActivity() {


    companion object {
        fun newInstance(activity: Activity): Intent {
            val intent = Intent(activity, HomeActivity::class.java)
            return intent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppUtils.isLollipopCompatible())
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)


        FrangerApp.get(this@HomeActivity)
                .userComponent()
                .plus(HomeModule(this@HomeActivity))
                .inject(this@HomeActivity)

        eventBus.register(this)

        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@HomeActivity, factory).get(HomeViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)

        setupAdapter()

    }

    private fun setupAdapter() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

    }

    private fun setupControllers() {
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    private fun goToContactsPage() {
        val intent = ContactActivity.newInstance(this@HomeActivity)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment = IncomingFragment.newInstance()
            when (position) {
                0 -> fragment = IncomingFragment.newInstance()
                1 -> fragment = OutgoingFragment.newInstance()
            }
            return fragment
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }
    }

    private fun showChatPage(chatContact: ChatContact, isIncoming: Boolean, channelName: String) {
        val intent = ChatActivity.newInstance(this@HomeActivity, chatContact, isIncoming, channelName)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivityForResult(intent, ActivityConstants.RESULT_CODE_CHAT_ACTIVITY)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIncomingListEventReceived(event: IncomingListEvent) {
        when (event.id) {
            HomePresentationConstants.ON_INCOMING_CHANNEL_CLICKED -> {
                showChatPage(event.contact, event.isIncoming, event.channelName)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOutgoingListEventReceived(event: OutgoingListEvent) {
        when (event.id) {
            HomePresentationConstants.ON_OUTGOING_CHANNEL_CLICKED -> {
                showChatPage(event.contact, event.isIncoming, event.channelName)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(event: HomeEvent) {
        when (event.id) {
            HomePresentationConstants.ON_FAB_CLICKED -> {
                goToContactsPage()
            }
        }
    }
}
