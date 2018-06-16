package ru.jufy.myposh.ui.main

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.FileProvider
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.jufy.mgtshr.extensions.disableShiftMode
import com.jufy.mgtshr.ui.base.BaseActivity
import ru.jufy.myposh.R
import ru.jufy.myposh.Screens
import ru.jufy.myposh.presentation.global.RouterProvider
import ru.jufy.myposh.ui.activities.ConfirmPictureActivity
import ru.jufy.myposh.ui.activities.IntentDispatcherActivity
import ru.jufy.myposh.ui.fragments.FavoritesFragment
import ru.jufy.myposh.ui.fragments.LibraryFragment
import ru.jufy.myposh.ui.fragments.SettingsFragment
import ru.jufy.myposh.ui.global.BackButtonListener
import ru.jufy.myposh.ui.launch.LaunchActivity
import ru.jufy.myposh.ui.store.MarketFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity(), IntentDispatcherActivity, RouterProvider {
    private lateinit var marketFragment: MarketFragment
    private lateinit var libraryFragment: LibraryFragment
    private lateinit var favoritesFragment: FavoritesFragment
    private lateinit var settingsFragment: SettingsFragment
    private var currentPhotoPath: String? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var currentFragment = MainFragments.MARKET

    @Inject
    lateinit var navigationHolder: NavigatorHolder
    @Inject lateinit override var router: Router


    private var currentTabIndex: Int = 0

    private lateinit var tabs: HashMap<String, TabContainerFragment>

    private val tabKeys = listOf(
            tabIdToFragmentTag(R.id.bn_market),
            tabIdToFragmentTag(R.id.bn_my_library),
            tabIdToFragmentTag(R.id.bn_favorites),
            tabIdToFragmentTag(R.id.bn_settings)
    )

    private fun tabIdToFragmentTag(id: Int) = "tab_$id"

    private val navigator = object : Navigator {
        override fun applyCommands(commands: Array<out Command>?) {
            commands?.let {
                for (command in commands) applyCommand(command)
            }
        }

        fun applyCommand(command: Command) {
            if (command is Back || command is BackTo) {
                finish()
            } else if (command is SystemMessage) {
                Toast.makeText(this@MainActivity,
                        command.message, Toast.LENGTH_SHORT).show()
            } else if (command is Replace) {
                when (command.screenKey) {
                    Screens.STORE_SCREEN -> showTab(0, currentTabIndex)
                    Screens.PURCHASES_SCREEN -> showTab(1, currentTabIndex)
                    Screens.FAVOURITES ->showTab(2, currentTabIndex)
                    Screens.LOGIN_ACTIVITY_SCREEN -> startActivity(Intent(this@MainActivity, LaunchActivity::class.java))
                    Screens.SETTINGS_SCREEN ->showTab(3, currentTabIndex)
                }
            }
        }
    }

    private fun createNewFragments(): HashMap<String, TabContainerFragment> = hashMapOf(
            tabKeys[0] to TabContainerFragment.getNewInstance(Screens.STORE_SCREEN),
            tabKeys[1] to TabContainerFragment.getNewInstance(Screens.PURCHASES_SCREEN),
            tabKeys[2] to TabContainerFragment.getNewInstance(Screens.FAVOURITES),
            tabKeys[3] to  TabContainerFragment.getNewInstance(Screens.SETTINGS_SCREEN)
    )

    private fun findFragments(): HashMap<String, TabContainerFragment> = hashMapOf(
            tabKeys[0] to supportFragmentManager.findFragmentByTag(tabKeys[0]) as TabContainerFragment,
            tabKeys[1] to supportFragmentManager.findFragmentByTag(tabKeys[1]) as TabContainerFragment,
            tabKeys[2] to supportFragmentManager.findFragmentByTag(tabKeys[2]) as TabContainerFragment,
            tabKeys[3] to supportFragmentManager.findFragmentByTag(tabKeys[3]) as TabContainerFragment
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        hideKeyboard()
        val fragment = tabs[tabKeys[currentTabIndex]]
        if (fragment != null
                && fragment is BackButtonListener
                && (fragment as BackButtonListener).onBackPressed()) {
            return
        } else {
            router.exit()
        }
    }

    private fun showTab(newItem: Int, oldItem: Int) {
        supportFragmentManager.beginTransaction()
                .hide(tabs[tabKeys[oldItem]])
                .show(tabs[tabKeys[newItem]])
                .commit()
        currentTabIndex = newItem
    }

    override fun onPause() {
        super.onPause()
        navigationHolder.removeNavigator()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationHolder.setNavigator(navigator)
    }


    val isBLEEnabled: Boolean
        get() {
            val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = manager.adapter
            return adapter != null && adapter.isEnabled
        }

    private enum class MainFragments {
        MARKET, LIBRARY, FAVORITES, SETTINGS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUp()

        if (savedInstanceState == null) {
            currentTabIndex = 0
            tabs = createNewFragments()
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_frame, tabs[tabKeys[0]], tabKeys[0])
                    .add(R.id.fragment_frame, tabs[tabKeys[1]], tabKeys[1])
                    .add(R.id.fragment_frame, tabs[tabKeys[2]], tabKeys[2])
                    .add(R.id.fragment_frame, tabs[tabKeys[3]], tabKeys[3])
                    .hide(tabs[tabKeys[1]])
                    .hide(tabs[tabKeys[2]])
                    .hide(tabs[tabKeys[3]])
                    .commitNow()
            bottomNavigationView?.selectedItemId = R.id.bn_market
        } else {
            tabs = findFragments()
        }

        if (!isBLEEnabled) {
            showBLEDialog()
        }
    }

    override fun setUp() {
        setupBottomNav()

    }

    fun showBLEDialog() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, ENABLE_BT_REQ)
    }

    fun showCurrentFragment() {
        when (currentFragment) {
            MainFragments.MARKET -> showMarket()
            MainFragments.LIBRARY -> showLibrary()
            MainFragments.FAVORITES -> showFavorites()
            MainFragments.SETTINGS -> showSettings()
        }
    }

    fun showMarket() {
        currentFragment = MainFragments.MARKET
        showFragment(marketFragment)
    }

    fun showLibrary() {
        currentFragment = MainFragments.LIBRARY
        showFragment(libraryFragment)
    }

    fun showFavorites() {
        currentFragment = MainFragments.FAVORITES
        showFragment(favoritesFragment)
    }

    fun showSettings() {
        currentFragment = MainFragments.SETTINGS
        showFragment(settingsFragment)
    }

    override fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Toast.makeText(this, "Could not create file", Toast.LENGTH_SHORT).show()
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        "ru.jufy.myposh.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                dispatchConfirmPictureIntent()
            }
        }
    }

    private fun dispatchConfirmPictureIntent() {
        val confirmPictureIntent = Intent(this, ConfirmPictureActivity::class.java)
        confirmPictureIntent.putExtra(ConfirmPictureActivity.EXTRA_FILE_PATH, currentPhotoPath)
        startActivityForResult(confirmPictureIntent, REQUEST_CROP_AND_CONFIRM)
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun setupBottomNav() {
        bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView?.disableShiftMode()
        bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bn_market -> router.replaceScreen(Screens.STORE_SCREEN)
                R.id.bn_my_library -> router.replaceScreen(Screens.PURCHASES_SCREEN)
                R.id.bn_favorites -> router.replaceScreen(Screens.FAVOURITES)
                R.id.bn_settings -> router.replaceScreen(Screens.SETTINGS_SCREEN)
            }
            true
        }
    }

    fun hideBottomNav() {
        bottomNavigationView?.visibility = View.GONE
    }

    fun showBottomNav() {
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun showFragment(fragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.fragment_frame, fragment)
        transaction.commit()
        showBottomNav()
    }

    companion object {
        internal val REQUEST_IMAGE_CAPTURE = 1
        internal val REQUEST_CROP_AND_CONFIRM = 2

        private val ENABLE_BT_REQ = 0
    }
}
