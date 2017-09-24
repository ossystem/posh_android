package ru.jufy.myposh.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.graphics.PorterDuff.Mode;

import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.ScreenUtils;

/**
 * Created by Anna on 4/11/2017.
 */

public class ArcBarView extends ImageView {

    int defaultBigButtonSizeDp = 56;
    int defaultBigIconSizeDp = 24;
    int defaultBigButtonBorderDp = 8;
    int defaultSmallButtonSizeDp = 40;
    int defaultSmallButtonBorderDp = 4;
    int defaultShadowSizeDp = 8;
    int defaultTopSpaceDp = 8;
    int defaultButtonMarginDp = 16;

    //BG
    int topSpace;
    int bgColor;
    int shadowColor;
    int shadowSize;

    //buttons
    int buttonMargin;
    int smallButtonSize;
    int smallButtonBorder;
    int bigButtonSize;
    int bigButtonBorder;
    int buttonBgColor;

    //icon
    int iconTint;
    int centerIconResId = -1;
    int bigIconSize;
    int leftIconResId = -1;
    int rightIconResId = -1;
    int smallIconSize;

    //bounds
    Rect bounds;
    RectF boundsF;

    //paint
    Paint mBgPaint;

    Paint mButtonPaint;

    //paths
    Path bgPath;

    //click listeners
    OnClickListener rightButtonClickListener;
    OnClickListener leftButtonClickListener;
    OnClickListener centerButtonClickListener;

    public ArcBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //get attrs
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = bigButtonSize + bigButtonBorder * 2 +
                smallButtonSize * 2 + smallButtonBorder * 4 +
                buttonMargin * 4 ;
        int desiredHeight = bigButtonSize + bigButtonBorder * 2 +
                shadowSize + topSpace;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    private void init() {
        //placeholders
        bounds = new Rect();
        boundsF = new RectF();
        bgPath = new Path();

        //bar paint
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(bgColor);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setShadowLayer(shadowSize/2, 0, shadowSize/2, shadowColor);

        //button paint
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(buttonBgColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //clear previous drawings
        bgPath.reset();

        //calc width and height to work with
        canvas.getClipBounds(bounds);
        boundsF.set(bounds);

        //calc center button size and location
        float bigButtonRadius = bigButtonSize /2;
        float bigButtonCenterX = boundsF.width()/2;
        float bigButtonCenterY = boundsF.height() - shadowSize -
                bigButtonBorder - bigButtonRadius;

        //add arc
        float chordHeight = bigButtonSize * 0.5f;
        float arcRadius = calcRadiusFromChord(boundsF.width(), chordHeight);
        float arcCenterX = bigButtonCenterX;
        float arcCenterY = bigButtonCenterY + bigButtonRadius * 0.3f - arcRadius;
        bgPath.addCircle(arcCenterX, arcCenterY, arcRadius, Path.Direction.CW);

        if (isLeftButtonSet()) {
            //add left button border
            //TODO
        }

        if (isCenterButtonSet()) {
            //add big button border
            bgPath.addCircle(bigButtonCenterX,
                    bigButtonCenterY,
                    bigButtonRadius + bigButtonBorder,
                    Path.Direction.CW);
        }
        if (isRightButtonSet()) {
            //add left button border
           //TODO
        }

        //draw bg
        canvas.drawPath(bgPath, mBgPaint);

        if (isLeftButtonSet()) {
            //TODO
        }

        if (isCenterButtonSet()) {
            //draw big button bg
            canvas.drawCircle(bigButtonCenterX, bigButtonCenterY, bigButtonRadius, mButtonPaint);
            //draw big button icon
            Drawable centerIcon = getTintedDrawable(centerIconResId);
            centerIcon.setBounds((int)(bigButtonCenterX - bigIconSize /2),
                    (int)(bigButtonCenterY - bigIconSize /2),
                    (int)(bigButtonCenterX + bigIconSize /2),
                    (int)(bigButtonCenterY + bigIconSize /2));
            centerIcon.draw(canvas);
        }

        if (isRightButtonSet()) {
            //TODO
        }

    }

    public void setCenterButton(@DrawableRes int iconId, OnClickListener clickListener) {
        centerIconResId = iconId;
        centerButtonClickListener = clickListener;
        invalidate();
    }

    public void setLeftButton(@DrawableRes int iconId, OnClickListener clickListener) {
        leftIconResId = iconId;
        leftButtonClickListener = clickListener;
        invalidate();
    }

    public void setRightButton(@DrawableRes int iconId, OnClickListener clickListener) {
        rightIconResId = iconId;
        rightButtonClickListener = clickListener;
        invalidate();
    }

    private boolean isCenterButtonSet(){
        return centerIconResId > 0;
    }

    private boolean isLeftButtonSet(){
        return leftIconResId > 0;
    }

    private boolean isRightButtonSet(){
        return rightIconResId > 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCenterButtonSet()  && centerButtonClickListener != null) {
            centerButtonClickListener.onClick(this);
        }
        return super.onTouchEvent(event);
    }

    private Drawable getTintedDrawable(@DrawableRes  int iconId) {
        Drawable drawable = getResources().getDrawable(iconId);
        if (drawable != null) {
            Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, iconTint);
        }
        return drawable;
    }

    private float calcRadiusFromChord(float w, float h) {
        return h / 2 + w * w / (8 * h);
    }

}
