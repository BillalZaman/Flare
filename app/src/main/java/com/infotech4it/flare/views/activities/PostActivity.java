package com.infotech4it.flare.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityPostBinding;

import org.jetbrains.annotations.NotNull;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

import static android.Manifest.permission.CAMERA;

public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private EasyImage easyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        init();
    }

    private void init() {
        binding.setOnClick(this);
        easyImage = new EasyImage.Builder(this)
                .setChooserTitle("Pick media")
                .setCopyImagesToPublicGalleryFolder(false)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName("EasyImage sample")
                .allowMultiple(false)
                .build();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack: {

                break;
            }
            case R.id.imgAddPic: {
                verifyCameraStoragePermission();
                break;
            }
            case R.id.btnPost: {

                break;
            }
        }
    }

    public void verifyCameraStoragePermission() {
        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission[2]) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(activity, "permission granted", Toast.LENGTH_SHORT).show();
            selectImage();
        } else {
            ActivityCompat.requestPermissions(this, permission, 123);
        }

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
                    easyImage.openCameraForImage(PostActivity.this);
                } else if (options[item].equals(getResources().getString(R.string.chhose_from_gallery))) {
                    dialog.dismiss();
                    easyImage.openGallery(PostActivity.this);
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
        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                if (mediaFiles[0].getFile().exists()) {
                    Glide.with(PostActivity.this).load(mediaFiles[0].getFile()).into(binding.imgStatus);
//                    viewModel.uploadImage(mediaFiles[0].getFile()
//                            , PreferenceHelper.getInstance().getString(ConstUtils.LANGUAGE, ConstUtils.ENGLISH));
                }
            }
        });
    }
}