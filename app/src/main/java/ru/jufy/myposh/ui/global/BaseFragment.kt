package com.jufy.mgtshr.ui.base

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.not_found_view.*
import ru.jufy.myposh.R
import ru.jufy.myposh.ui.global.EmptyView
import ru.jufy.myposh.ui.global.MvpView
import javax.inject.Inject

/**
 * Created by rolea on 4/22/2017.
 */

abstract class BaseFragment : Fragment(), MvpView, EmptyView, HasSupportFragmentInjector {
    protected val SHARE_PERMISSION_KEY = 1

    protected var SHARE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    var baseActivity: BaseActivity? = null
        private set

    val isTelephoneAvailable: Boolean
        get() {
            val pm = context!!.packageManager

            return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        }

    override fun onAuthError() {
        (activity as BaseActivity).onAuthError()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        if (context is BaseActivity) {
            val activity = context as BaseActivity?
            this.baseActivity = activity
            activity!!.onFragmentAttached()
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    override fun showProgress() {
        mainRootContainer?.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE
        if (notFountTitle != null && notFountTitle!!.visibility == View.VISIBLE)
            notFountTitle!!.visibility = View.GONE
        if (retryButton != null && retryButton!!.visibility == View.VISIBLE)
            retryButton!!.visibility = View.GONE

    }

    override fun hideProgress() {
        progressBar?.visibility = View.GONE
        mainRootContainer?.visibility = View.VISIBLE
    }


    override fun onError(message: String) {
        baseActivity?.onError(message)
    }

    override fun onError(@StringRes resId: Int) {
        baseActivity?.onError(resId)
    }


    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity?.hideKeyboard()
        }
    }

    override fun onLoadingSuccess() {
        if (baseActivity != null) {
            baseActivity?.onLoadingSuccess()
        }
    }

    override fun onLoadingError() {
        changeNotFoundState(true)
        if (notFountTitle != null)
            changeNotFoundTitle("Нет соединения.\n Повторите попытку")
        else
            onError("Нет соединения.\n Повторите попытку")
        changeNotFoundButton(true)
    }


    protected abstract fun setUp(view: View?)

    override fun showMessage(title: String, message: String) {
        if (baseActivity != null) {
            baseActivity!!.showMessage(title, message)
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return childFragmentInjector
    }

    protected fun buildDialogWithItems(title: String, items: Array<String>, listener: DialogInterface.OnClickListener,
                                       vararg message: String): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(context!!, R.style.AlertDialogCustom)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setItems(items, listener)

        if (message.isNotEmpty()) {
            alertDialogBuilder.setMessage(message[0])
        }
        alertDialogBuilder.setNegativeButton("Нет", { dialog, _ -> dialog.dismiss() })
        alertDialogBuilder.setPositiveButton("Да", listener)

        return alertDialogBuilder.create()
    }

    override fun changeNotFoundState(visibility: Boolean) {
        progressBar?.visibility = View.GONE
        mainRootContainer?.visibility = if (visibility) View.GONE else View.VISIBLE
        notFountTitle?.visibility = if (visibility) View.VISIBLE else View.GONE

    }

    override fun changeNotFoundButton(visibility: Boolean) {
        if (retryButton != null) {
            retryButton!!.visibility = if (visibility) View.VISIBLE else View.GONE
        }
    }

    protected fun setTitleToolbar(title: String) {
        baseActivity?.setToolbarTitle(title)
    }

    override fun changeNotFoundTitle(title: String) {
        notFountTitle?.text = title
    }


    protected fun getRealPathFromURIPath(contentURI: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context!!.contentResolver.query(contentURI, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    companion object {
        protected val EXTRA_NAME = "extra_name"
    }
}
