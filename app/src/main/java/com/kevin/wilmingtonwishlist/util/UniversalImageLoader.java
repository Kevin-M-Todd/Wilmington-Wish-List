package com.kevin.wilmingtonwishlist.util;

import android.content.Context;
import android.widget.ImageView;

import com.kevin.wilmingtonwishlist.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class UniversalImageLoader {

    private static final int defaultImage = R.drawable.camera_icon;
    private Context mContext;

    public UniversalImageLoader(Context context) {
        mContext = context;
    }


    public static void setImage(String imgURL, ImageView image){

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imgURL, image);
    }
}