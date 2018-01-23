package com.frangerapp.franger.ui.login

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import com.frangerapp.franger.R
import com.frangerapp.franger.databinding.ActivityLoginBinding
import com.frangerapp.franger.ui.BaseActivity
import com.frangerapp.franger.ui.util.UiUtils
import com.frangerapp.franger.viewmodel.login.LoginViewModel
import com.frangerapp.franger.viewmodel.login.eventbus.LoginViewEvent
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.frangerapp.franger.ui.login.countries.CountriesListDialogFragment
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent
import com.frangerapp.franger.viewmodel.countries.util.CountriesPresentationConstants


/**
 * Created by Pavan on 20/01/18.
 */
class LoginActivity : BaseActivity() {

    companion object {
        val TAG = "LoginActivity"
        @JvmStatic
        fun newInstance(activity: Activity): Intent {
            val homeIntent = Intent(activity, LoginActivity::class.java)
            return homeIntent
        }
    }

    private lateinit var viewDataBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    private fun setupViews() {
        UiUtils.setEditTextAsClickable(viewDataBinding.countryCodeTxt)
    }

    private fun setupControllers() {
        viewDataBinding.btnLogin.setOnClickListener({
            viewDataBinding.btnLogin.startAnimation()
            Handler().postDelayed({
                this@LoginActivity.runOnUiThread {
                    viewDataBinding.btnLogin.doneLoadingAnimation(getColorRes(R.color.red),
                            BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
//                    viewDataBinding.btnLogin.revertAnimation()
                }
            }, 500)
        })

    }

    private fun invokeDataBinding() {
        viewModel = LoginViewModel()
        viewDataBinding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        btnLogin.dispose()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: LoginViewEvent) {
        when (loginViewEvent.id) {
            LoginPresentationConstants.VALID_NUMBER_CHECK_SUCCESS -> {
                viewDataBinding.btnLogin.doneLoadingAnimation(getColorRes(R.color.red),
                        BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
            }
            LoginPresentationConstants.VALID_NUMBER_CHECK_FAIL -> {
                viewDataBinding.btnLogin.revertAnimation()
            }
            LoginPresentationConstants.VALID_NUMBER_REQUEST_SENT -> {
                viewDataBinding.btnLogin.startAnimation()
            }
            LoginPresentationConstants.ON_COUNTRY_CODE_CLICKED -> {
                val bottomSheetFragment = CountriesListDialogFragment.newInstance()
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCountryListInteraction(countriesViewEvent: CountriesViewEvent) {
        when (countriesViewEvent.id) {
            CountriesPresentationConstants.COUNTRY_ITEM_CLICKED -> {
                viewModel.onCountryCodeSelected(countriesViewEvent.countriesListItemViewModel)
            }
        }
    }
}