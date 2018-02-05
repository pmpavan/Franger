package com.frangerapp.franger.ui.countries

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.frangerapp.franger.R
import com.frangerapp.franger.databinding.FragmentCountriesListDialogBinding
import com.frangerapp.franger.ui.BaseBottomSheetFragment
import com.frangerapp.franger.viewmodel.countries.CountriesViewModel
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent
import com.frangerapp.franger.viewmodel.countries.util.CountriesPresentationConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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

    companion object {
        fun newInstance(): CountriesListDialogFragment = CountriesListDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_countries_list_dialog, container, false)
        viewModel = CountriesViewModel()
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()

        return viewDataBinding.root
    }

    private lateinit var viewModel: CountriesViewModel


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
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
