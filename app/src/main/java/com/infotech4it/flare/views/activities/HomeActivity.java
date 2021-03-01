package com.infotech4it.flare.views.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.infotech4it.flare.R;
import com.infotech4it.flare.constant.Constant;
import com.infotech4it.flare.databinding.ActivityHomeBinding;
import com.infotech4it.flare.fragments.ChatFragment;
import com.infotech4it.flare.fragments.FeedFragment;
import com.infotech4it.flare.fragments.FindFriendFragment;
import com.infotech4it.flare.fragments.ProfileFragment;
import com.infotech4it.flare.fragments.SettingFragment;
import com.infotech4it.flare.helpers.PreferenceHelper;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.ChatInterface;
import com.infotech4it.flare.interfaces.ImgClickInterface;
import com.infotech4it.flare.interfaces.MoreInterface;

import org.jetbrains.annotations.NotNull;

import java.util.logging.ConsoleHandler;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

import static android.Manifest.permission.CAMERA;

public class HomeActivity extends AppCompatActivity implements MoreInterface, ChatInterface, ImgClickInterface {
    private ActivityHomeBinding binding;
    private FeedFragment feedFragment = new FeedFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private FindFriendFragment findFriend = new FindFriendFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private SettingFragment settingFragment = new SettingFragment();
    private EasyImage easyImage;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        init();
    }

    private void init() {
        easyImage = new EasyImage.Builder(this)
                .setChooserTitle("Pick media")
                .setCopyImagesToPublicGalleryFolder(false)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName("EasyImage sample")
                .allowMultiple(false)
                .build();
        settingBottomNav();
        UIHelper.replaceFragment(this, R.id.framelayout, feedFragment);

    }

    private void settingBottomNav() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_News: {
                    UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, feedFragment);
                    break;
                }
                case R.id.action_Profile: {
                    UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, profileFragment);
                    break;
                }
                case R.id.action_Find_Friend:{
                    UIHelper.replaceFragment(HomeActivity.this, R.id.framelayout, findFriend);
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
                try {
                    mAuth.signOut();
                }finally {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

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
        UIHelper.openActivity(this, ChatDetailActivity.class);
    }


    public void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(CAMERA, getPackageName());
            final CharSequence[] options = {getResources().getString(R.string.take_photo),
                    getResources().getString(R.string.chhose_from_gallery),
                    getResources().getString(R.string.cancel_btn)};
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.select_option));
            builder.setItems(options, (dialog, item) -> {
                if (options[item].equals(getResources().getString(R.string.take_photo))) {
                    dialog.dismiss();
                    easyImage.openCameraForImage(this);
                } else if (options[item].equals(getResources().getString(R.string.chhose_from_gallery))) {
                    dialog.dismiss();
                    easyImage.openGallery(this);
                } else if (options[item].equals(getResources().getString(R.string.cancel_btn))) {
                    dialog.dismiss();
                }
            });
            builder.show();

        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyImage.handleActivityResult(requestCode, resultCode, data, this,
                new DefaultCallback() {
                    @Override
                    public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                        if (mediaFiles[0].getFile().exists()) {
                            PreferenceHelper.getInstance().setString(Constant.IMAGE_URL, String.valueOf(mediaFiles[0].getFile()));
//                            Glide.with(this).load(mediaFiles[0].getFile()).into(binding.imgcover);
                            UIHelper.showLongToastInCenter(HomeActivity.this, ""+mediaFiles[0].getFile());
                        }
                    }
                });
    }

    @Override
    public void ImgClick(String place) {
        if (place.equalsIgnoreCase("user")){
            selectImage();
        }
    }
}