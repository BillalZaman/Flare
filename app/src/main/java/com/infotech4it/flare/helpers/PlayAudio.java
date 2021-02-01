package com.infotech4it.flare.helpers;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class PlayAudio {

    private MediaPlayer mPlayer;
    private int lastProgress = 0;
    private String filePath;
    private SeekBar seekBar;
    private ImageView imageViewPlay;
    private Handler mHandler = new Handler();

    public PlayAudio(String filePath, SeekBar seekBar, ImageView imageViewPlay){

        this.filePath=filePath;
        this.seekBar=seekBar;
        this.imageViewPlay=imageViewPlay;

    }

    public void startPlaying() {

        if (mPlayer==null) {
            mPlayer = new MediaPlayer();
        }

        if (mPlayer.isPlaying()) {
            mPlayer.reset();
           // mPlayer.release();
        }

        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }

        //seekBar.setProgress(lastProgress);
        //mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageViewPlay.setSelected(false);
                lastProgress=0;
                mPlayer.seekTo(lastProgress);
                mHandler.removeCallbacks(runnable);
                mPlayer.reset();
                mPlayer.release();
                seekBar.setProgress(lastProgress);}
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mPlayer!=null && fromUser ){
                    mPlayer.seekTo(progress);
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    Runnable runnable = () -> seekUpdation();

    public void seekUpdation() {

        if(mPlayer != null){
            int mCurrentPosition = mPlayer.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
            Log.d("seekUpdation__",lastProgress+"");
        }
        mHandler.postDelayed(runnable, 100);
    }

    public void stopPlaying() {

        try{
            imageViewPlay.setSelected(false);
            lastProgress=0;
            mHandler.removeCallbacks(runnable);
            mPlayer.reset();
            //mPlayer.release();
            seekBar.setProgress(lastProgress);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean isPlaying() {
        boolean status=false;

        try {
            if (mPlayer!=null) {
                status=mPlayer.isPlaying();
            }
        }catch (Exception e) {
            status=false;
        }


        return status;
    }


}