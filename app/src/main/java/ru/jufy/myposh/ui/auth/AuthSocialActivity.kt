package ru.jufy.myposh.ui.auth

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jufy.mgtshr.extensions.visible
import kotlinx.android.synthetic.main.webview.*
import ru.jufy.myposh.BuildConfig
import ru.jufy.myposh.presentation.auth.social.AuthSocialMvpView
import ru.jufy.myposh.presentation.auth.social.AuthSocialPresenter
import ru.jufy.myposh.ui.global.WebViewActivity
import javax.inject.Inject

class AuthSocialActivity : WebViewActivity(), AuthSocialMvpView {

    @Inject
    lateinit var presenter: AuthSocialPresenter<AuthSocialMvpView>

    override fun setUp() {
        super.setUp()

        val simpleCurrentDomain = if (BuildConfig.DEBUG) "posh.jwma.ru" else "art.posh.space"
        webview?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar?.visible(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar?.visible(false)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url).toString()
                return if  (handleUri(uri)) true
                else super.shouldOverrideUrlLoading(view, url)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url.toString()
                return if  (handleUri(uri)) true
                else super.shouldOverrideUrlLoading(view, request)
            }

            private fun handleUri(url: String): Boolean {
                if (Uri.parse(url).host.contains(simpleCurrentDomain)) {
                    presenter.loginSocial(url)
                    return true
                }
                return false
            }
        }

        presenter.onAttach(this)
        presenter.authSocial(intent.getStringExtra(SOCIAL_TYPE))
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }

    override fun loadUrl(link: String) {
        setToolbarTitle(link)
        this.link = link
        loadUrl()
    }
    companion object {
        val SOCIAL_TYPE = "ru.jufy.myposh.ui.global.WebViewActivity.SOCIAL_TYPE"
    }
}