package com.madinabektayeva.treasurehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by madina on 02.05.18.
 */


public class CustomView extends android.support.v7.widget.AppCompatImageView {

    Paint paint;
    Path path;
    Rect imageBounds;
    Drawable mCustomImage;
    int pathMarkSize;



    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrst) {
        super(context, attrst);
        mCustomImage = context.getResources().getDrawable(R.drawable.pathmark);
        paint = new Paint();
        path = new Path();

        paint.setAntiAlias(true);
        paint.setARGB(255, 126, 192, 238);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        imageBounds = new Rect(0,0,0,0);
        pathMarkSize = 16;
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //canvas.drawPath(path, paint);
       // Rect imageBounds = canvas.getClipBounds();  // Adjust this for where you want it

        mCustomImage.setBounds(imageBounds);
        mCustomImage.draw(canvas);

    }

    public void drawPathMark(int x, int y){
        imageBounds = new Rect(x-pathMarkSize/2, y+pathMarkSize/2, x+pathMarkSize/2, y-pathMarkSize/2);
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event){

        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;

        }

        invalidate();
        return true;
    }

}