package com.stockita.newpointofsales.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * This class is a custom view to draw a circle frames for the login screen
 */
public class FrameLoginScreen extends View {

    private Paint mPaint;
    private Point mCenter;
    private Random random;
    private float mReadius;

    private int mColorOne;
    private int mColorTwo;
    private int mColotThree;
    private int mColorFour;
    private int mColotFive;


    /**
     * Constructor for Java
     *
     * @param context
     */
    public FrameLoginScreen(Context context) {
        this(context, null);

    }

    /**
     * Constructor for XML
     *
     * @param context
     * @param attrs
     */
    public FrameLoginScreen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor XML with style
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public FrameLoginScreen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);

    }


    /**
     * Initialize attribute and define style attributes
     *
     * @param attributeSet
     * @param defStyleAttr
     */
    private void init(AttributeSet attributeSet, int defStyleAttr) {

        /* Random numbers */
        random = new Random();

        /* Create a paintbrush to draw with */
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /* filled in color */
        mPaint.setStyle(Paint.Style.FILL);

        /* Circle center point */
        mCenter = new Point();

        /* Initial colors */
        mColorOne = Color.RED;
        mColorTwo = Color.WHITE;
        mColotThree = Color.BLUE;
        mColorFour = Color.WHITE;
        mColotFive = Color.RED;


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width, height;

        /* Unconstrained sizes */
        int contentWidth = 200;
        int contentHeight = 200;

        width = MeasureUtils.getMeasurement(widthMeasureSpec, contentWidth);
        height = MeasureUtils.getMeasurement(heightMeasureSpec, contentHeight);

        /* Must call this method */
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (w != oldw || h != oldh) {
            /* If changed then reset */
            mCenter.x = w / 2;
            mCenter.y = h / 2;
            mReadius = Math.min(mCenter.x, mCenter.y);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        /* Draw s series of concentric circles, smallest to largest */
        mPaint.setColor(mColorOne);
        canvas.drawCircle(mCenter.x, mCenter.y, mReadius, mPaint);

        /* the white after the blue */
        mPaint.setColor(mColorTwo);
        canvas.drawCircle(mCenter.x, mCenter.y, mReadius * 0.8f, mPaint);

        /* the blue after the white */
        mPaint.setColor(mColotThree);
        canvas.drawCircle(mCenter.x, mCenter.y, mReadius * 0.6f, mPaint);

        /* the smallest white bigger than the red */
        mPaint.setColor(mColorFour);
        canvas.drawCircle(mCenter.x, mCenter.y, mReadius * 0.4f, mPaint);

        /* the smallest red */
        mPaint.setColor(mColotFive);
        canvas.drawCircle(mCenter.x, mCenter.y, mReadius * 0.2f, mPaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /* x position */
        int x = (int) event.getX();

        /* y position */
        int y = (int) event.getY();

        /* y is just zero */
        int z = 0;


        int[] colors = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN};

        /* Get random index */
        int index = random.nextInt(4);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (x != z) {
                    mColorOne = colors[index];
                    mColotThree = colors[index + 1];
                    mColotFive = colors[index + 2];
                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("move", String.valueOf(x) + " " + String.valueOf(y));
                break;
            case MotionEvent.ACTION_UP:
                Log.e("up", String.valueOf(x) + " " + String.valueOf(y));
                break;

        }

        /* Appearance changes */
        invalidate();

        /* Size or shape changes */
        //requestLayout();

        return false;
    }


    public static class MeasureUtils {

        public static int getMeasurement(int measureSpec, int contentSize) {

            int specSize = View.MeasureSpec.getSize(measureSpec);
            switch (MeasureSpec.getMode(measureSpec)) {
                case MeasureSpec.UNSPECIFIED:
                    /* Wrap content */
                    return contentSize;
                case MeasureSpec.AT_MOST:
                    /* Match parent */
                    return Math.min(contentSize, specSize);
                case MeasureSpec.EXACTLY:
                    /* Specific size */
                    return specSize;
                default:
                    return 0;
            }

        }

    }
}
