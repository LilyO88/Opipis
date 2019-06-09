package com.lidorttol.opipis.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.lidorttol.opipis.R;

public class MainActivity extends AppCompatActivity {

    NavigationView navView;
    DrawerLayout drawerLayout;
    private NavController navController;
    private AppBarConfiguration appbarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    private void setupViews() {
        navController = Navigation.findNavController(this, R.id.navHostFragment);

        navView = ActivityCompat.requireViewById(this, R.id.navigationView);
        drawerLayout = ActivityCompat.requireViewById(this, R.id.drawerLayout);

        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = ActivityCompat.requireViewById(this, R.id.main_toolbar);
        setSupportActionBar(toolbar);

        appbarConfiguration = new AppBarConfiguration
                .Builder(R.id.mapFragment, R.id.profileFragment)
                .setDrawerLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appbarConfiguration);
        //Si no se pone la siguiente línea no aparece el título la primera vez
        setTitle(R.string.title_first_time);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> toolbar.setTitle(navController.getCurrentDestination().getLabel()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() != R.id.destSettingsFragment) {
            drawerLayout.closeDrawer(drawerLayout);
//        }
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }*/

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
