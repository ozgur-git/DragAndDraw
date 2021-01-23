package com.example.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoxDrawingView extends View {

    private static final String TAG="BoxDrawingView";

    private Box mCurrentBox;
    private List<Box> mBoxen=new ArrayList<>();
    private Paint mBoxPaint,mBackgroundPaint;


    public BoxDrawingView(Context context) {
        super(context);
    }

    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint=new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint=new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current=new PointF(event.getX(),event.getY());

        String action="";

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                action="ACTION DOWN";
                mCurrentBox=new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action="ACTION MOVE";
                if (mCurrentBox!=null){
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action="ACTION POINTER UP";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action="ACTION POINTER DOWN";
                break;
            case MotionEvent.ACTION_UP:
                action="ACTION UP";
                mCurrentBox=null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action="ACTION CANCEL";
                mCurrentBox=null;
                break;
        }

        Log.i(TAG,action+" at x= "+current.x+" at y "+current.y);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for(Box box:mBoxen){

            float left=Math.min(box.getOrigin().x,box.getCurrent().x);
            float right=Math.max(box.getOrigin().x,box.getCurrent().x);
            float top=Math.min(box.getOrigin().y,box.getCurrent().y);
            float bottom=Math.max(box.getOrigin().y,box.getCurrent().y);

            canvas.drawRect(left,top,right,bottom,mBoxPaint);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState=super.onSaveInstanceState();
        SavedState myState=new SavedState(superState);
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", (Serializable) mBoxen);
//        bundle.putSerializable("key", (Serializable) mBoxen);
        myState.setBundle(bundle);
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (mBoxen.size()==0) {
            mBoxen = (List<Box>) savedState.getBundle().getSerializable("key");
        }

        Log.i("restore","box size is "+mBoxen.size());
        invalidate();
    }

    static class SavedState extends BaseSavedState{

        Bundle mBundle;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel source) {
            super(source);
        }

        public Bundle getBundle() {
            return mBundle;
        }

        public void setBundle(Bundle bundle) {
            mBundle = bundle;
        }
    }
}
