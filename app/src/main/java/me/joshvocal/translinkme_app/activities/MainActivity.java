package me.joshvocal.translinkme_app.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.fragments.FavouritesFragment;
import me.joshvocal.translinkme_app.fragments.LocationFragment;
import me.joshvocal.translinkme_app.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void switchToSearchFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new SearchFragment())
                .commit();
    }

    private void switchToFavouritesFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new FavouritesFragment())
                .commit();
    }

    private void switchToLocationFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new LocationFragment())
                .commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_location:
                return true;
            case R.id.navigation_search:
                return true;
            case R.id.navigation_favourite:
                return true;
        }
       
        return false;
    }
}
