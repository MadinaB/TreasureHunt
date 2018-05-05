package com.madinabektayeva.treasurehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by madina on 02.05.18.
 */


public class CustomView extends android.support.v7.widget.AppCompatImageView {

    Bitmap bitmap;

    int x;
    int y;


    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrst) {
        super(context, attrst);

    }


    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void drawPathMark(int x, int y, Bitmap bitmap){
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        invalidate();

    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        this.setImageBitmap(bitmap);

    }

}