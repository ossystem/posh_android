package ru.jufy.myposh.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by BorisDev on 23.08.2017.
 */

public class ClippingRelativeLayout extends RelativeLayout {
    Path clipPath = new Path();
    Rect canvasRect = new Rect();

    public ClippingRelativeLayout(Context context) {
        super(context);
    }

    public ClippingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClippingRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(canvasRect);
        clipPath.addCircle(canvasRect.exactCenterX(), canvasRect.exactCenterY(), canvasRect.height()/2, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
