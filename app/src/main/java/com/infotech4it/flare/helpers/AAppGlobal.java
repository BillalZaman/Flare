package com.infotech4it.flare.helpers;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.Timestamp;
import com.infotech4it.flare.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AAppGlobal {

    public static final class Companion {
        public static final int TEXT_TYPE = 0;
        public static final int IMAGE_TYPE = 1;
        public static final int AUDIO_TYPE = 2;
        public static final int VIDEO_TYPE = 3;
        public static final int MEDIA_MIXED_TYPE = 5;
        public static final int TEXTFILE_TYPE = 4;
        public static final int LOCATION_TYPE = 5;
        public static final int TYPE_SEND = 0;
        public static final int TYPE_RECIEVE = 1;

        public static String getUniTime() {
            //Timestamp.now();
            //return String.valueOf(System.currentTimeMillis() / 1000L);
            // Map<String,String> gsg= ServerValue.TIMESTAMP;
            return String.valueOf(Timestamp.now().getSeconds());
        }

        public static void loadImage(ImageView imageView,int drawable){

            Glide.with(imageView.getContext())
                    .load(drawable)
                    .placeholder(R.drawable.ic_profile)
                    .apply(new RequestOptions().centerCrop())
                    .into(imageView);

        }

        public static String getExtension(String fileName){
            if (fileName.contains(".")) {
                return fileName.substring(fileName.lastIndexOf(".")+1);
            }
            else return null;
        }

        public static void loadImageFromNetwork(ImageView imageView, String path){

            if(!(imageView instanceof CircleImageView)) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

            if(path!=null) {

                RequestOptions options = new RequestOptions()
                        .priority(Priority.NORMAL)
                        .transform(new CenterInside())
                        .transform(new RoundedCorners(22))
                        .placeholder(R.drawable.ic_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(imageView.getWidth(), imageView.getHeight())
                        .error(R.drawable.ic_profile);


                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(options)

                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                return false;
                            }
                        })
                        .into(imageView);
            } else {

                Glide.with(imageView.getContext())
                        .load(R.drawable.ic_profile)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                return false;
                            }
                        })
                        .into(imageView);
            }


        }

    }


    public static String getMessageSentTime(long time) {

        DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");

        String diffTime="";
        // d1, d2 are dates
        Date d=new Date(Timestamp.now().getSeconds()*1000L);

        Date d1=new Date(time*1000L);
        long diff = d.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");

        if (diffDays>0) {
            diffTime= String.valueOf(diffDays)+" day";
        }else if (diffHours>0) {
            diffTime= String.valueOf(diffHours)+" hr";
        }else if (diffMinutes>0) {
            diffTime= String.valueOf(diffMinutes)+" min";
        }else if (diffSeconds>0) {
            diffTime= String.valueOf(diffSeconds)+" sec";
        }else {
            //diffTime= String.valueOf(diffSeconds)+" now";
            diffTime= "now";
        }

        return diffTime;

    }

    public static String dateToTimeStamp(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date.getTime());
    }

}
