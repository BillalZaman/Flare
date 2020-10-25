package com.infotech4it.flare.views.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityHomeBinding;
import com.infotech4it.flare.fragments.ChatFragment;
import com.infotech4it.flare.fragments.FeedFragment;
import com.infotech4it.flare.fragments.ProfileFragment;
import com.infotech4it.flare.fragments.SettingFragment;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.ChatInterface;
import com.infotech4it.flare.interfaces.MoreInterface;

public class HomeActivity extends AppCompatActivity implements MoreInterface, ChatInterface {
    private ActivityHomeBinding binding;
    private FeedFragment feedFragment = new FeedFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private SettingFragment settingFragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        init();
    }

    private void init() {
        settingBottomNav();
        UIHelper.replaceFragment(this, R.id.framelayout, feedFragment);

    }

    private void settingBottomNav() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_News: {
                        UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, feedFragment);
                        break;
                    }
                    case R.id.action_Profile: {
                        UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, profileFragment);
                        break;
                    }
                    case R.id.action_Chat: {
                        UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, chatFragment);
                        break;
                    }
                    case R.id.action_Setting: {
                        UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, settingFragment);
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onMoreClick(int position) {
        switch (position) {
            case 0: {
                // change password
                UIHelper.openActivity(this, ChangePasswordActivity.class);
                break;
            }
            case 1: {
                // feedback
                UIHelper.openActivity(this, FeedbackActivity.class);
                break;
            }
            case 2: {
                // logout
                
                break;
            }
            case 3: {
                // more app

                break;
            }
            case 4: {
                // share our app

                break;
            }
            case 5: {
                //privacy policy

                break;
            }
        }
    }

    @Override
    public void chatClick(int position) {

    }
}