package me.joshvocal.translinkme_app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.fragment.FavouritesFragment;
import me.joshvocal.translinkme_app.fragment.LocationFragment;
import me.joshvocal.translinkme_app.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    // Bind Bottom Navigation View
    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomNavigationView.setSelectedItemId(R.id.navigation_search);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void switchToSearchFragment() {
        setTitle(R.string.fragment_search_title);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new SearchFragment())
                .commit();
    }

    private void switchToFavouritesFragment() {
        setTitle(R.string.fragment_favourites_title);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new FavouritesFragment())
                .commit();
    }

    private void switchToLocationFragment() {
        setTitle(R.string.fragment_location_title);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new LocationFragment())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_location:
                switchToLocationFragment();
                return true;
            case R.id.navigation_search:
                switchToSearchFragment();
                return true;
            case R.id.navigation_favourite:
                switchToFavouritesFragment();
                return true;
        }

        return false;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                switchToLocationFragment();
                break;
            case 1:
                switchToSearchFragment();
                break;
            case 2:
                switchToFavouritesFragment();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // Empty
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // Empty
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        return true;
    }
}
