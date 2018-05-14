package com.jufy.mgtshr.ui.base


import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.not_found_view.*
import ru.jufy.myposh.R
import ru.jufy.myposh.ui.activities.LoginActivity
import ru.jufy.myposh.ui.global.EmptyView
import ru.jufy.myposh.ui.global.MvpView
import javax.inject.Inject

/**
 * Created by rolea on 4/22/2017.
 */

abstract class BaseActivity : AppCompatActivity(), MvpView, EmptyView, BaseFragment.Callback, HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        /*
        TODO:enable on next refactore step
        val crashlytics = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlytics)*/

    }


    override fun showProgress() {
        if (mainRootContainer != null) mainRootContainer!!.visibility = View.GONE
        if (progressBar != null) progressBar!!.visibility = View.VISIBLE
        if (notFountTitle != null && notFountTitle!!.visibility == View.VISIBLE)
            notFountTitle!!.visibility = View.GONE
        if (retryButton != null && retryButton!!.visibility == View.VISIBLE)
            retryButton!!.visibility = View.GONE

    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun hideProgress() {
        if (progressBar != null) progressBar!!.visibility = View.GONE
        if (mainRootContainer != null) mainRootContainer!!.visibility = View.VISIBLE
        /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            animatedView = null;
        }*/
    }


    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        val textView = sbView
                .findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        snackbar.show()
    }

    override fun onError(@StringRes resId: Int) {
        onError(getString(resId))
    }


    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }

    override fun hideKeyboard() {
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onLoadingSuccess() {
        /*if (animatedView!=null){
            animatedView.stopOk();

        }*/
    }


    override fun showMessage(message: String) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLoadingError() {
        changeNotFoundState(true)
        changeNotFoundTitle("Нет соединения.\nПовторите попытку")
        changeNotFoundButton(true)
    }

    override fun onError(message: String) {
        if (message != null) {
            showSnackBar(message)
        }
    }

    override fun changeNotFoundState(visibility: Boolean) {

        mainRootContainer?.visibility = if (visibility) View.GONE else View.VISIBLE
        notFountTitle?.visibility = if (visibility) View.VISIBLE else View.GONE

    }

    override fun changeNotFoundButton(visibility: Boolean) {

        retryButton?.visibility = if (visibility) View.VISIBLE else View.GONE

    }

    override fun changeNotFoundTitle(title: String) {

        notFountTitle?.text = title
    }

    override fun onAuthError() {
        val newIntent = Intent(this, LoginActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // newIntent.putExtra(LoginActivity.Companion.IS_LOGOUT, true)
        startActivity(newIntent)
        finish()
    }

    override fun showMessage(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)

        alertDialogBuilder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    protected abstract fun setUp()

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentDispatchingAndroidInjector
    }
}
