package com.bee.planbus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.planbus.R;
import com.bee.planbus.fragments.CalendarFragment;
import com.bee.planbus.fragments.FastNoteFragment;
import com.bee.planbus.fragments.HomeFragment;
import com.bee.planbus.fragments.TaskFragment;
import com.bee.planbus.fragments.TimerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navView)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bttmNavigationHome)
    BottomNavigationView bttmNavigationHome;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    public void callCalendar(){
        replaceFragment(new CalendarFragment());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);



        replaceFragment(new HomeFragment());
        bttmNavigationHome.setSelectedItemId(R.id.home);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ANASAYFA");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        bttmNavigationHome.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.timer:
                    replaceFragment(new TimerFragment());
                    break;
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.notes:
                    replaceFragment(new FastNoteFragment());
                    break;
            }
            return true;
        });


    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        TextView txtName = findViewById(R.id.txtName);
        txtName.setText(firebaseUser.getEmail());
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new HomeFragment());
                bttmNavigationHome.setVisibility(View.VISIBLE);
                bttmNavigationHome.setSelectedItemId(R.id.home);
                drawerLayout.close();
                break;

            case R.id.nav_target:
                replaceFragment(new TaskFragment());
                getSupportActionBar().setTitle("HEDEFLERİM");
                drawerLayout.close();
                bttmNavigationHome.setVisibility(View.GONE);
                break;
            case R.id.nav_map:
                Intent intent2 = new Intent(HomeActivity.this, MapListActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_setting:
                Intent intent4 = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent4);
                break;

            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("UYARI!");
                builder.setMessage("Çıkış yapmak istediğinize emin misiniz?");
                builder.setIcon(R.drawable.warning);
                builder.setNegativeButton("Hayır", null);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                builder.show();

        }
        return true;
    }

   /* @Override
    protected void onStart() {
        super.onStart();
 FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user== null) {
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }*/

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    public void goActivity(Object activity) {
        Intent intent = new Intent(HomeActivity.this, activity.getClass());
    }
}