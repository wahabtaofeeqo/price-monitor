package com.taocoder.pricemonitor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.fragments.LoginFragment;
import com.taocoder.pricemonitor.fragments.RegisterFragment;
import com.taocoder.pricemonitor.interfaces.OnFragmentChangeListener;

public class CreateAccountActivity extends AppCompatActivity implements OnFragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        String login = getIntent().getStringExtra("page");
        if (login != null) {
            changeFragment(new LoginFragment(), false);
        }
        else
            changeFragment(new RegisterFragment(), false);
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
}