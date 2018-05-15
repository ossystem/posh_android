package ru.jufy.myposh.ui.activities

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.jufy.mgtshr.extensions.disableShiftMode
import kotlinx.android.synthetic.main.activity_splash.*

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

import ru.jufy.myposh.R
import ru.jufy.myposh.ui.fragments.FavoritesFragment
import ru.jufy.myposh.ui.fragments.LibraryFragment
import ru.jufy.myposh.ui.fragments.MarketFragment
import ru.jufy.myposh.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity(), IntentDispatcherActivity {

    private lateinit var marketFragment: MarketFragment
    private lateinit var libraryFragment: LibraryFragment
    private lateinit var favoritesFragment: FavoritesFragment
    private lateinit var settingsFragment: SettingsFragment
    private var currentPhotoPath: String? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var currentFragment = MainFragments.MARKET

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
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initFragments()
        setupBottomNav()
        showMarket()

        if (!isBLEEnabled) {
            showBLEDialog()
        }
    }

    fun showBLEDialog() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, ENABLE_BT_REQ)
    }

    fun showCurrentFragment() {
        when (currentFragment) {
            MainActivity.MainFragments.MARKET -> showMarket()
            MainActivity.MainFragments.LIBRARY -> showLibrary()
            MainActivity.MainFragments.FAVORITES -> showFavorites()
            MainActivity.MainFragments.SETTINGS -> showSettings()
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

    private fun initFragments() {
        marketFragment = MarketFragment.newInstance()
        libraryFragment = LibraryFragment.newInstance()
        favoritesFragment = FavoritesFragment.newInstance()
        settingsFragment = SettingsFragment.newInstance()
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
                R.id.bn_market -> showMarket()
                R.id.bn_my_library -> showLibrary()
                R.id.bn_favorites -> showFavorites()
                R.id.bn_settings -> showSettings()
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
