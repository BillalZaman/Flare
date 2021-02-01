package com.infotech4it.flare.interfaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public interface ChatScreenMediaListener {

    public void itemClick(View view, int position,String value,int viewType);


    public void startPlay(View view, int position,String value);

    public void stopPlay(View view, int position);

    public void startAudioPlay(ImageView imageView, SeekBar seekBar, int position, String filePath);

    public void stopAudioPlay(View imageView, SeekBar seekBar, int position);



}

