package com.frangerapp.franger.ui.chat

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.chat.ChatModule
import com.frangerapp.franger.databinding.ActivityChatBinding
import com.frangerapp.franger.ui.user.UserBaseActivity
import com.frangerapp.franger.viewmodel.chat.ChatViewModel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ChatActivity : UserBaseActivity() {


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


        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
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

    }
}
