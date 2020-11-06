package com.infotech4it.flare.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.infotech4it.flare.R;


public class LoaderDialog {
    Activity activity;
    AlertDialog alertDialog;

    public LoaderDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loader,null));

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void dismiss(){
        alertDialog.dismiss();
    }
}
