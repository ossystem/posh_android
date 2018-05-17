package ru.jufy.myposh.ui.global

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import com.jufy.mgtshr.extensions.visible
import com.jufy.mgtshr.ui.base.BaseActivity
import kotlinx.android.synthetic.main.appbarlayout_shadow.*
import kotlinx.android.synthetic.main.webview.*
import ru.jufy.myposh.R
import ru.jufy.myposh.Screens
import ru.jufy.myposh.ui.main.MainActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.*
import java.util.*
import javax.inject.Inject


open class WebViewActivity : BaseActivity() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    protected var link: String? = null
    private var action = -1
    private var httpHeaders: HashMap<String, String>? = null


    private val navigator = object : Navigator {
        override fun applyCommands(commands: Array<out Command>) {
            for (command in commands) applyCommand(command)
        }

        private fun applyCommand(command: Command) {
            when (command) {
                is Forward -> forward(command)
                is Replace -> replace(command)
                is Back -> back()
                is SystemMessage -> showMessage(command.message)
                else -> Log.e("Cicerone", "Illegal command for this screen: " + command.javaClass.simpleName)
            }
        }

        private fun forward(command: Forward) {
            when (command.screenKey) {
                Screens.MAIN_ACTIVITY_SCREEN -> {
                    val intent = Intent(this@WebViewActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                else -> Log.e("Cicerone", "Unknown screen: " + command.screenKey)
            }
        }

        private fun replace(command: Replace) {
            forward(Forward(command.screenKey, command.transitionData))
            finish()
        }

        private fun back() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)
        setUp()
    }

    override fun setUp() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(URL)) {
            link = intent.getStringExtra(URL)
            setToolbarTitle(link!!)
        }

        action = intent.getIntExtra(ACTION, -1)
        if (ACTION_SHOW_WITH_HEADERS == action) {
            httpHeaders = intent.getSerializableExtra(HEADERS) as HashMap<String, String>
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        router.exit()
    }

    protected fun loadUrl() {
        link?.let {
            if (ACTION_SHOW_WITH_HEADERS == action) {
                webview?.loadUrl(it, httpHeaders)
            } else {
                webview?.loadUrl(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadUrl()
    }

    override protected fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun showProgress() {
        error_text?.visible(false)
        webview?.visible(false)
        progressBar?.visible(true)
    }

    override fun hideProgress() {
        progressBar?.visible(false)
        error_text?.visible(false)
        webview?.visible(true)
    }

    companion object {
        val URL = "ru.jufy.myposh.ui.global.WebViewActivity.URL"
        val ACTION = "ru.jufy.myposh.ui.global.WebViewActivity.ACTION"
        val HEADERS = "ru.jufy.myposh.ui.global.WebViewActivity.HEADERS"

        val ACTION_AUTHORIZE = 0
        val ACTION_SHOW_WITH_HEADERS = 1
    }
}
