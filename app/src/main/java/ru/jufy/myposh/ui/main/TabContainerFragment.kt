package ru.jufy.myposh.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jufy.mgtshr.ui.base.BaseFragment
import com.jufy.mgtshr.ui.subnavigation.LocalCiceroneHolder
import ru.jufy.myposh.R
import ru.jufy.myposh.Screens
import ru.jufy.myposh.presentation.global.RouterProvider
import ru.jufy.myposh.ui.fragments.FavoritesFragment
import ru.jufy.myposh.ui.fragments.LibraryFragment
import ru.jufy.myposh.ui.fragments.MarketFragment
import ru.jufy.myposh.ui.fragments.SettingsFragment
import ru.jufy.myposh.ui.global.BackButtonListener
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import javax.inject.Inject

class TabContainerFragment : BaseFragment(), RouterProvider, BackButtonListener {
    @Inject
    lateinit var ciceroneHolder: LocalCiceroneHolder

    private val navigator: Navigator by lazy {
        return@lazy object : SupportAppNavigator(activity, childFragmentManager, R.id.ftc_container) {
            override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? {
                return null;
            }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? {
                return when (screenKey) {
                    Screens.STORE_SCREEN -> MarketFragment.newInstance()
                    Screens.PURCHASES_SCREEN -> LibraryFragment.newInstance()
                    Screens.FAVOURITES -> FavoritesFragment.newInstance()
                    Screens.SETTINGS_SCREEN ->SettingsFragment.newInstance()
                    else -> null
                }
            }

            override fun exit() {
                (activity as RouterProvider).router.exit()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        getCicerone()?.navigatorHolder?.setNavigator(navigator)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if (childFragmentManager.findFragmentById(R.id.ftc_container) == null) {
            getCicerone()?.router?.replaceScreen(getContainerName())
        }
    }

    override fun onPause() {
        getCicerone()?.navigatorHolder?.removeNavigator()
        super.onPause()
    }

    companion object {
        private val EXTRA_NAME = "tcf_extra_name"
        private val EXTRA_DATA: String = "EXTRA_DATA"

        fun getNewInstance(name: String, extraData: String = ""): TabContainerFragment {
            val fragment = TabContainerFragment()

            val arguments = Bundle()
            arguments.putString(EXTRA_NAME, name)
            arguments.putString(EXTRA_DATA, extraData)
            fragment.arguments = arguments

            return fragment
        }
    }

    private fun getExtraData(): String? {
        return arguments!!.getString(EXTRA_DATA)
    }

    private fun getContainerName(): String? {
        return arguments!!.getString(EXTRA_NAME)
    }

    private fun getCicerone(): Cicerone<Router>? {
        return ciceroneHolder.getCicerone(getContainerName()!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab_container, container, false)
    }


    override fun setUp(view: View?) {

    }

    override val router: Router
        get() = getCicerone()!!.router!!;

    override fun onBackPressed(): Boolean {
        val fragment = childFragmentManager.findFragmentById(R.id.ftc_container)
        return if (fragment != null
                && fragment is BackButtonListener
                && (fragment as BackButtonListener).onBackPressed()) {
            true
        } else {
            (activity as RouterProvider).router.exit()
            true
        }
    }
}