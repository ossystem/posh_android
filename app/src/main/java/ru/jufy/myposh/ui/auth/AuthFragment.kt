package ru.jufy.myposh.ui.auth

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import com.jufy.mgtshr.extensions.showKeyboardAndFocus
import com.jufy.mgtshr.extensions.visible
import com.jufy.mgtshr.ui.base.BaseFragment
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.fragment_login.*
import ru.jufy.myposh.R
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.presentation.auth.phone.AuthPresenter
import ru.jufy.myposh.presentation.auth.phone.AuthMvpView
import ru.jufy.myposh.ui.utils.ValidationUtils
import javax.inject.Inject

/**
 * Created by BorisDev on 04.09.2017.
 */

class AuthFragment : BaseFragment(), AuthMvpView {
    @Inject
    lateinit var presenter: AuthPresenter<AuthMvpView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        presenter.onAttach(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }

    override fun setUp(view: View?) {
        applyBlurOnBackground()
        buttonSend.setOnClickListener { validatePhone() }
        phoneInput.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE)
                validatePhone()
            return@setOnEditorActionListener false
        }

        buttonForward.setOnClickListener { validateCode() }
        codeInput.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE)
                validateCode()
            return@setOnEditorActionListener false
        }

        buttonBack.setOnClickListener { presenter.onBackPressed() }
        imageViewFbRec.setOnClickListener { presenter.authSocialClicked(SocialTypes.FB) }
        imageViewVkRec.setOnClickListener { presenter.authSocialClicked(SocialTypes.INSTAGRAM) }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        phoneInput.showKeyboardAndFocus(context)
    }

    private fun validateCode() {
        if (isValidCode())
            presenter.sendCodeClicked(codeInput.text.trim().toString())
    }

    private fun validatePhone() {
        if (isValidPhone()) {
            val phone = phoneInput.text.toString()
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "")
                    .replace(" ", "")
            presenter.sendNumberClicked(phone)
        }
    }

    override fun onDetach() {
        presenter.onDetach()
        super.onDetach()
    }

    private fun applyBlurOnBackground() {
        val radius = 5f
        val decorView = activity!!.window.decorView
        //set background, if your root layout doesn't have one
        val windowBackground = decorView.background

        blurView.setupWith(blurContainer)
                .windowBackground(windowBackground)
                .blurAlgorithm(RenderScriptBlur(context))
                .blurRadius(radius)
                .setHasFixedTransformationMatrix(true)
    }

    override fun clearCode() {
        codeInput.setText("")
    }

    override fun toggleCodeView(isCodeView: Boolean) {
        buttonSend?.visible(!isCodeView)
        if (isCodeView) codeInput.visible(true)
        else codeInput.visibility = View.INVISIBLE
        buttonsNavigation?.visible(isCodeView)

        if (isCodeView) codeInput.showKeyboardAndFocus(context)
        else phoneInput.showKeyboardAndFocus(context)
    }

    override fun togglePhoneProgressVisibility(isLoading: Boolean) {
        buttonSend?.visible(!isLoading)
        phoneProgressBar?.visible(isLoading)
    }

    private fun isValidCode(): Boolean {
        val valid = !codeInput.text.toString().trim({ it <= ' ' }).isEmpty() && codeInput.text.toString().length == 4
        if (!valid) {
            showMessage(getString(R.string.error), getString(R.string.code_format_incorrect))
            codeInput.showKeyboardAndFocus(context)
        }
        return valid
    }

    private fun isValidPhone(): Boolean {
        val valid = !phoneInput.rawText.trim({ it <= ' ' }).isEmpty() && ValidationUtils.isPhoneNumberValid(phoneInput.text.toString())
        if (!valid) {
            showMessage(getString(R.string.error), getString(R.string.phone_format_incorrect))
            phoneInput.showKeyboardAndFocus(context)
        }
        return valid
    }

    override fun toggleCodeProgressVisibility(isLoading: Boolean) {
        buttonsNavigation.visible(!isLoading)
        codeProgressBar?.visible(isLoading)
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): AuthFragment {
            val args = Bundle()

            val fragment = AuthFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
