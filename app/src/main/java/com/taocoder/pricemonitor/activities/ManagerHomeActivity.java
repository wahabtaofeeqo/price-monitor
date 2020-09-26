package com.taocoder.pricemonitor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.fragments.ApprovedPriceFragment;
import com.taocoder.pricemonitor.fragments.CompetitorPricesFragment;
import com.taocoder.pricemonitor.fragments.ManagerHomeFragment;
import com.taocoder.pricemonitor.fragments.RequestApprovalFragment;
import com.taocoder.pricemonitor.helpers.SessionManager;
import com.taocoder.pricemonitor.interfaces.OnFragmentChangeListener;

public class ManagerHomeActivity extends AppCompatActivity implements OnFragmentChangeListener {

    private DrawerLayout drawerLayout;

    // Fist fragment
    private String pageName = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ninthcore");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);

        final SessionManager sessionManager = SessionManager.getInstance(this);
        NavigationView navigationView = findViewById(R.id.nav);
        View navHeader = navigationView.getHeaderView(0);
        TextView email = navHeader.findViewById(R.id.email);
        email.setText(sessionManager.getName());

        navigationView.setNavigationItemSelectedListener(navListener);

        changeFragment(new ManagerHomeFragment(), false);
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        changeFragment(fragment, true);
    }

    private void changeFragment(Fragment fragment, boolean backTrack) {

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);

        if (backTrack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    //Navigation items click event
    private NavigationView.OnNavigationItemSelectedListener navListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.home:
                    if (!pageName.equalsIgnoreCase("home")) {
                        pageName = "home";
                        changeFragment(new ManagerHomeFragment(), true);
                    }
                    break;

                case R.id.request:
                    if (!pageName.equalsIgnoreCase("request")) {
                        pageName = "request";
                        changeFragment(new RequestApprovalFragment(), true);
                    }
                    break;

                case R.id.prices:
                    if (!pageName.equalsIgnoreCase("prices")) {
                        pageName = "prices";
                        changeFragment(new CompetitorPricesFragment(), true);
                    }
                    break;

                case R.id.approved:
                    if (!pageName.equalsIgnoreCase("approved")) {
                        pageName = "approved";
                        changeFragment(new ApprovedPriceFragment(), true);
                    }
                    break;

                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
                    sessionManager.setEmail("");

                    Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    intent.putExtra("page", "login");
                    startActivity(intent);
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        }
    };
}