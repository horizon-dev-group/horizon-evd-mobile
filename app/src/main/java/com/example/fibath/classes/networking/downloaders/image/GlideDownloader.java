package com.example.fibath.classes.networking.downloaders.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideDownloader {
    private static String base_movie_link = "http://10.0.2.2/CITYLOX_FINAL_(NOV_18_2017)/ethiorox/ethiorox_db/images/movie_theatre/";
    private static String base_event_link = "http://10.0.2.2/CITYLOX_FINAL_(NOV_18_2017)/ethiorox/ethiorox_db/images/events/";
    private static String base_organization_link = "http://10.0.2.2/CITYLOX_FINAL_(NOV_18_2017)/ethiorox/ethiorox_db/images/organization/";

    public static void downloadLocalImage(Context context, int drawable, ImageView img) {
        Glide.with(context).load(drawable).into(img);
    }
}
