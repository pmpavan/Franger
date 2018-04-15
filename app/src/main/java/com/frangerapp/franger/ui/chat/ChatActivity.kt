package com.frangerapp.franger.ui.chat

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.chat.ChatModule
import com.frangerapp.franger.databinding.ActivityChatBinding
import com.frangerapp.franger.domain.chat.model.ChatContact
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.chat.ChatListItemUiState
import com.frangerapp.franger.viewmodel.chat.ChatViewModel
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ChatActivity : UserBaseActivity() {


    companion object {
        private const val ARG_CONTACT = "arg_contact"
        private const val ARG_IS_INCOMING = "arg_is_incoming"
        private const val ARG_CHANNEL = "arg_channel"
        private const val ARG_IS_CHANNEL_MUTED = "arg_channel_is_muted"
        fun newInstance(activity: Activity, user: ChatContact, isIncoming: Boolean, channelName: String, isChannelBlockedOrMuted: Boolean): Intent {
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(ARG_CONTACT, user)
            intent.putExtra(ARG_IS_INCOMING, isIncoming)
            intent.putExtra(ARG_CHANNEL, channelName)
            intent.putExtra(ARG_IS_CHANNEL_MUTED, isChannelBlockedOrMuted)
            return intent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var adapter: ChatListAdapter
    @Inject
    lateinit var listState: ChatListUiState

    private lateinit var viewDataBinding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@ChatActivity)
                .userComponent()
                .plus(ChatModule(this@ChatActivity))
                .inject(this@ChatActivity)

        eventBus.register(this)
        messageFromAliens()
        invokeDataBinding()
        setupViews()
        setupControllers()

    }

    override fun onResume() {
        super.onResume()
        onPageLoaded()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    private lateinit var chatContact: ChatContact

    private var isIncoming: Boolean = false

    private var channelName: String? = ""

    private var isChannelBlockedOrMuted = false

    private fun messageFromAliens() {
        if (intent != null) {
            chatContact = intent.getParcelableExtra(ARG_CONTACT)
            isIncoming = intent.getBooleanExtra(ARG_IS_INCOMING, false)
            channelName = intent.getStringExtra(ARG_CHANNEL)
            isChannelBlockedOrMuted = intent.getBooleanExtra(ARG_IS_CHANNEL_MUTED, false)
        }
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@ChatActivity, factory).get(ChatViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }


    private fun setupViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        manageStatusBar(R.color.statusbar_color)
//        manageActionBarWithTitle(toolbar, "")
    }

    private fun setupControllers() {
        adapter.handler = viewModel as ChatListItemUiState.ChatItemClickHandler
        viewDataBinding.chatList.adapter = adapter

        viewDataBinding.chats = listState
        viewModel.data.observe(this@ChatActivity, Observer { t ->
            listState.update(t)
        })
        viewModel.titleTxt.observe(this@ChatActivity, Observer { t ->
            manageActionBarWithTitle(toolbar, t)

        })
        viewModel.onViewLoaded(chatContact, isIncoming, channelName, isChannelBlockedOrMuted)
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.menu_chat, menu)
        updateMenuTitles(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateMenuTitles(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_block)
        menuItem.title = getStringValue(viewModel.blockMenuTitle)
        menuItem.setIcon(viewModel.blockMenuIcon)
        menuItem.isVisible = viewModel.blockMenuVisibility

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_block -> {
            viewModel.onBlockPressed()
            true
        }
        R.id.action_delete -> {
            viewModel.onDeletePressed()
            true
        }
        android.R.id.home -> {
            onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun invalidateMenu() {
        invalidateOptionsMenu()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(event: ChatEvent) {
        when (event.id) {
            ChatPresentationConstants.SET_TOOLBAR_TXT -> {
                manageActionBarWithTitle(toolbar, event.message)
            }
            ChatPresentationConstants.ON_BLOCK_CLKD -> {
                invalidateMenu()
            }
            ChatPresentationConstants.ON_MUTE_CLKD -> {
                invalidateMenu()
            }

        }
    }
}
