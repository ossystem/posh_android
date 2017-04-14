package ru.jufy.myposh.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.jufy.myposh.fragments.FavoritesFragment;
import ru.jufy.myposh.fragments.LibraryFragment;
import ru.jufy.myposh.fragments.MarketFragment;
import ru.jufy.myposh.fragments.SettingsFragment;
import ru.jufy.myposh.R;

public class MainActivity extends AppCompatActivity {

    private MarketFragment marketFragment;
    private LibraryFragment libraryFragment;
    private FavoritesFragment favoritesFragment;
    private SettingsFragment settingsFragment;
    private FragmentTransaction transaction;

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
