package ru.jufy.myposh.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.jufy.myposh.fragments.FavoritesFragment;
import ru.jufy.myposh.fragments.LibraryFragment;
import ru.jufy.myposh.fragments.MarketFragment;
import ru.jufy.myposh.fragments.SettingsFragment;
import ru.jufy.myposh.R;

public class MainActivity extends AppCompatActivity implements IntentDispatcherActivity {

    private MarketFragment marketFragment;
    private LibraryFragment libraryFragment;
    private FavoritesFragment favoritesFragment;
    private SettingsFragment settingsFragment;
    private FragmentTransaction transaction;
    private String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CROP_AND_CONFIRM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFragments();
        setupBottomNav();
        showFragment(marketFragment);
    }


    private void initFragments() {
        marketFragment = MarketFragment.newInstance();
        libraryFragment = LibraryFragment.newInstance();
        favoritesFragment = FavoritesFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Could not create file", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ru.jufy.myposh.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                dispatchConfirmPictureIntent();
        }
    }

    private void dispatchConfirmPictureIntent() {
        Intent confirmPictureIntent = new Intent(this, ConfirmPictureActivity.class);
        confirmPictureIntent.putExtra(ConfirmPictureActivity.EXTRA_FILE_PATH, currentPhotoPath);
        startActivityForResult(confirmPictureIntent, REQUEST_CROP_AND_CONFIRM);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bn_market:
                                showFragment(marketFragment);
                                break;
                            case R.id.bn_my_library:
                                showFragment(libraryFragment);
                                break;
                            case R.id.bn_favorites:
                                showFragment(favoritesFragment);
                                break;
                            case R.id.bn_settings:
                                showFragment(settingsFragment);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void showFragment(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_frame, fragment);
        transaction.commit();
    }

}
