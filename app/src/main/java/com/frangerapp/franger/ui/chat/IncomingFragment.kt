package com.frangerapp.franger.ui.chat


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.frangerapp.franger.R
import com.frangerapp.franger.ui.user.UserBaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [IncomingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncomingFragment : UserBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_incoming, container, false)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IncomingFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): IncomingFragment {
            val fragment = IncomingFragment()
            return fragment
        }
    }

}// Required empty public constructor
