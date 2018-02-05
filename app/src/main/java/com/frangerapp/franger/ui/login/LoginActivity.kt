package com.frangerapp.franger.ui.login

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.frangerapp.franger.R
import com.frangerapp.franger.app.FrangerApp
import com.frangerapp.franger.app.util.di.module.login.SignUpModule
import com.frangerapp.franger.databinding.ActivityLoginBinding
import com.frangerapp.franger.ui.BaseActivity
import com.frangerapp.franger.ui.countries.CountriesListDialogFragment
import com.frangerapp.franger.ui.util.UiUtils
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent
import com.frangerapp.franger.viewmodel.countries.util.CountriesPresentationConstants
import com.frangerapp.franger.viewmodel.login.LoginViewModel
import com.frangerapp.franger.viewmodel.login.eventbus.LoginViewEvent
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


/**
 * Created by Pavan on 20/01/18.
 */
class LoginActivity : LoginBaseActivity() {

    companion object {
        val TAG = "LoginActivity"
        @JvmStatic
        fun newInstance(activity: Activity): Intent {
            val homeIntent = Intent(activity, LoginActivity::class.java)
            return homeIntent
        }
    }


    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewDataBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FrangerApp.get(this@LoginActivity)
                .loginComponent()
                .plus(SignUpModule(this@LoginActivity))
                .inject(this@LoginActivity)
        invokeDataBinding()
        setupViews()
        setupControllers()
        onPageLoaded()
    }

    private fun setupViews() {
        UiUtils.setEditTextAsClickable(viewDataBinding.countryCodeTxt)
    }

    private fun setupControllers() {

    }

    private fun invokeDataBinding() {
        viewModel = ViewModelProviders.of(this@LoginActivity, factory).get(LoginViewModel::class.java)
        viewDataBinding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        viewDataBinding.vm = viewModel
        viewDataBinding.executePendingBindings()
    }

    private fun onPageLoaded() {
        viewModel.onPageLoaded()
    }

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        btnLogin.dispose()
        viewModel.onViewDestroyed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onViewModelInteraction(loginViewEvent: LoginViewEvent) {
        when (loginViewEvent.id) {
            LoginPresentationConstants.VALID_NUMBER_CHECK_SUCCESS -> {
                viewDataBinding.btnLogin.doneLoadingAnimation(getColorRes(R.color.red),
                        BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp))
                Toast.makeText(this@LoginActivity, loginViewEvent.message, Toast.LENGTH_SHORT).show()
                moveToVerifyUserPage()
            }
            LoginPresentationConstants.VALID_NUMBER_CHECK_FAIL -> {
                viewDataBinding.btnLogin.revertAnimation()
                Toast.makeText(this@LoginActivity, loginViewEvent.message, Toast.LENGTH_SHORT).show()
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

    private fun moveToVerifyUserPage() {
        this@LoginActivity.startActivity(VerifyUserActivity.newInstance(this@LoginActivity))
    }
}