package com.frangerapp.franger.ui.login.countries

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.frangerapp.franger.R
import com.frangerapp.franger.databinding.FragmentCountriesListDialogBinding
import com.frangerapp.franger.ui.BaseBottomSheetFragment
import com.frangerapp.franger.viewmodel.countries.CountriesViewModel
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent
import com.frangerapp.franger.viewmodel.countries.util.CountriesPresentationConstants
import kotlinx.android.synthetic.main.fragment_countries_list_dialog_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CountriesListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [CountriesListDialogFragment.Listener].
 */
class CountriesListDialogFragment : BaseBottomSheetFragment() {
    private var mListener: Listener? = null

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        val parent = parentFragment
//        mListener = if (parent != null) {
//            parent as Listener
//        } else {
//            context as Listener
//        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onCountriesClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_countries_list_dialog_item, parent, false)) {

        internal val text: TextView = itemView.countryName

        init {
            text.setOnClickListener {
                mListener?.let {
                    it.onCountriesClicked(adapterPosition)
                    dismiss()
                }
            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(countriesViewEvent: CountriesViewEvent) {
        when (countriesViewEvent.id) {
            CountriesPresentationConstants.COUNTRY_ITEM_CLICKED -> {
                dismiss()
            }
        }
    }
}
