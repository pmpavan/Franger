package com.frangerapp.franger.ui.chat

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.chat.ChatModule
import com.frangerapp.franger.databinding.ActivityChatBinding
import com.frangerapp.franger.domain.chat.model.ChatContact
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.chat.ChatViewModel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ChatActivity : UserBaseActivity() {


    companion object {
        private val ARG_CONTACT = "arg_contact"
        private val ARG_IS_INCOMING = "arg_is_incoming"
        fun newInstance(activity: Activity, user: ChatContact, isIncoming: Boolean): Intent {
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(ARG_CONTACT, user)
            intent.putExtra(ARG_IS_INCOMING, isIncoming)
            return intent
        }
    }

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@ChatActivity)
                .userComponent()
                .plus(ChatModule(this@ChatActivity))
                .inject(this@ChatActivity)

        messageFromAliens()
        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    private lateinit var chatContact: ChatContact

    private var isIncoming: Boolean = false

    private fun messageFromAliens() {
        if (intent != null) {
            chatContact = intent.getParcelableExtra(ARG_CONTACT)
            isIncoming = intent.getBooleanExtra(ARG_IS_INCOMING, false)
        }
    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@ChatActivity, factory).get(ChatViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }


    private fun setupViews() {

    }

    private fun setupControllers() {
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded(chatContact, isIncoming)
    }
}
