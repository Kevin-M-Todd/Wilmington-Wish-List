package com.kevin.wilmingtonwishlist.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class BoxImage extends androidx.appcompat.widget.AppCompatImageView {

    public BoxImage(Context context) {
        super(context);
    }

    public BoxImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoxImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}