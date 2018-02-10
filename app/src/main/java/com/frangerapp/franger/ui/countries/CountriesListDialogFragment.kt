package com.frangerapp.franger.ui.countries

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.login.CountriesListModule
import com.frangerapp.franger.databinding.FragmentCountriesListDialogBinding
import com.frangerapp.franger.ui.BaseBottomSheetFragment
import com.frangerapp.franger.viewmodel.countries.CountriesViewModel
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent
import com.frangerapp.franger.viewmodel.countries.util.CountriesPresentationConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CountriesListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class CountriesListDialogFragment : BaseBottomSheetFragment() {

    private lateinit var viewDataBinding: FragmentCountriesListDialogBinding

    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewModel: CountriesViewModel

    companion object {
        fun newInstance(): CountriesListDialogFragment = CountriesListDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        FrangerApp.get(activity)
                .loginComponent()
                .plus(CountriesListModule(this@CountriesListDialogFragment))
                .inject(this@CountriesListDialogFragment)
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_countries_list_dialog, container, false)
        viewModel = ViewModelProviders.of(this@CountriesListDialogFragment, factory).get(CountriesViewModel::class.java)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()

        return viewDataBinding.root
    }


    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(countriesViewEvent: CountriesViewEvent) {
        when (countriesViewEvent.id) {
            CountriesPresentationConstants.COUNTRY_ITEM_CLICKED -> {
                dismiss()
            }
        }
    }
}
