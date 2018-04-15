package com.frangerapp.franger.ui.common


import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.franger.mobile.logger.FRLogger
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.user.common.AlertDialogModule
import com.frangerapp.franger.viewmodel.common.AlertDialogViewModel
import com.frangerapp.franger.viewmodel.common.eventbus.AlertDialogEvent
import com.frangerapp.franger.viewmodel.common.eventbus.EventBusConstants
import org.greenrobot.eventbus.EventBus
import com.frangerapp.franger.databinding.FragmentAlertDialogBinding
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class AlertDialogFragment : DialogFragment() {


    private var message: String? = null
    private var yesStr: String? = null
    private var noStr: String? = null

    companion object {

        private val FS_DIALOG_MSG = "dialog_msg"
        private val FS_DIALOG_YES = "dialog_yes"
        private val FS_DIALOG_NO = "dialog_no"

        fun newInstance(message: String, yesStr: String, noStr: String): AlertDialogFragment {
            val bundle = Bundle()
            bundle.putString(FS_DIALOG_MSG, message)
            bundle.putString(FS_DIALOG_YES, yesStr)
            bundle.putString(FS_DIALOG_NO, noStr)

            val alertDialogFragment = AlertDialogFragment()
            alertDialogFragment.arguments = bundle
            return alertDialogFragment
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageFromAliens()
    }

    private fun messageFromAliens() {
        val dataBundle = arguments
        message = dataBundle?.getString(FS_DIALOG_MSG)
        yesStr = dataBundle?.getString(FS_DIALOG_YES)
        noStr = dataBundle?.getString(FS_DIALOG_NO)
    }

    private lateinit var viewDataBinding: FragmentAlertDialogBinding

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewModel: AlertDialogViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        FrangerApp.get(activity)
                .userComponent()
                .plus(AlertDialogModule(this@AlertDialogFragment))
                .inject(this@AlertDialogFragment)
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alert_dialog, container, false)
        viewModel = ViewModelProviders.of(this@AlertDialogFragment, factory).get(AlertDialogViewModel::class.java)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.executePendingBindings()

        return viewDataBinding.root
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDialogButtonClicked(event: AlertDialogEvent) {
        dismiss()

        when (event.id) {
            EventBusConstants.DIALOG_NO_CLICKED -> FRLogger.msg("DIALOG_NO_CLICKED")

            EventBusConstants.DIALOG_YES_CLICKED -> {
                FRLogger.msg("DIALOG_YES_CLICKED")
            }
        }
    }


}
