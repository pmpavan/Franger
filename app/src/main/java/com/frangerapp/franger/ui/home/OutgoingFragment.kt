package com.frangerapp.franger.ui.home


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.franger.mobile.logger.FRLogger
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.home.OutgoingListModule
import com.frangerapp.franger.databinding.FragmentOutgoingBinding
import com.frangerapp.franger.ui.user.UserBaseFragment
import com.frangerapp.franger.viewmodel.home.OutgoingListViewModel
import com.frangerapp.franger.viewmodel.home.eventbus.OutgoingListEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [OutgoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutgoingFragment : UserBaseFragment() {


    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var adapter: OutgoingListAdapter
    @Inject
    lateinit var listState: OutgoingListUiState

    private lateinit var viewDataBinding: FragmentOutgoingBinding
    private lateinit var viewModel: OutgoingListViewModel

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment OutgoingFragment.
         */
        fun newInstance(): OutgoingFragment {
            val fragment = OutgoingFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FrangerApp.get(context)
                .userComponent()
                .plus(OutgoingListModule())
                .inject(this)

        eventBus.register(this)

    }

    private fun invokeDataBinding(inflater: LayoutInflater, container: ViewGroup?) {

        viewModel = ViewModelProviders.of(this, factory).get(OutgoingListViewModel::class.java)
        viewDataBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_outgoing, container, false)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        invokeDataBinding(inflater, container)
        setupViews()
        setupControllers()
        onPageLoaded()
        return viewDataBinding.root
    }

    private fun setupViews() {


    }

    private fun setupControllers() {
        viewDataBinding.outgoingListView.adapter = adapter

        viewDataBinding.uiState = listState
        viewDataBinding.handler = viewModel
        viewModel.data.observe(this, Observer { t ->
            FRLogger.msg("received list ${t.toString()}")
            listState.update(t)
        })

    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onViewDestroyed()
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(event: OutgoingListEvent) {
        when (event.id) {

        }
    }
}// Required empty public constructor
