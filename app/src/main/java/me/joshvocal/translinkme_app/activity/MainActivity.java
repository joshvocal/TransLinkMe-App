package me.joshvocal.translinkme_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.fragment.FavouritesFragment;
import me.joshvocal.translinkme_app.fragment.LocationFragment;
import me.joshvocal.translinkme_app.fragment.SearchFragment;
import me.joshvocal.translinkme_app.utils.InternetConnectivity;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    // Bind Bottom Navigation View
    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    private Snackbar mSnackBar;

    private InternetConnectivity mInternetConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ButterKnife bind
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            switchToSearchFragment();
        }

        // Bind Bottom Navigation
        mBottomNavigationView.setSelectedItemId(R.id.navigation_search);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        mInternetConnectivity = new InternetConnectivity(this);

        mSnackBar = Snackbar.make(findViewById(R.id.main_activity_coordinator_layout),
                getString(R.string.main_activity_snack_bar_no_connection), Snackbar.LENGTH_INDEFINITE);

        checkForNetworkConnection();
    }

    private void checkForNetworkConnection() {
        if (!mInternetConnectivity.isConnected()) {

            mSnackBar.setAction("Settings", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                }
            });

            mSnackBar.show();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        switch (item.getItemId()) {
            case R.id.action_about:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(getString(R.string.main_activity_alert_dialog_title,
                        getString(R.string.app_name), getString(R.string.app_version)))
                        .setMessage(getString(R.string.main_activity_alert_dialog_body))
                        .setNegativeButton(R.string.main_activity_alert_dialog_github_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                                        webIntent.setData(Uri.parse(
                                                getString(R.string.main_activity_alert_dialog_github_url)));
                                        startActivity(webIntent);
                                    }
                                })
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Do nothing.
                                    }
                                }).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForNetworkConnection();
    }
}
