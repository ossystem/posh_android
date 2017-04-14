package ru.jufy.myposh.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.ScreenUtils;

/**
 * Created by Anna on 4/13/2017.
 *
 * This class is only a draft
 * No exception checks, no generalisations
 * Works properly only in certain circumstances!
 *
 */

public class ArcLayout extends ViewGroup {



    //bg
    int topSpace;
    int bgColor;
    int shadowColor;
    int shadowSize;
    int buttonBorder;
    int chordHeight;
    int buttonSpacing;

    Rect holder;

    int middleX;
    int middleY;
    int middleRadius;
    int leftX;
    int leftY;
    int leftRadius = -1;
    int rightX;
    int rightY;
    int rightRadius = -1;
    int arcRadius;
    int middleToSideX;
    int middleToSideY;

    //bounds
    Rect bounds;
    RectF boundsF;

    //paint
    Paint mBgPaint;

    //paths
    Path bgPath;

    public ArcLayout(Context context) {
        super(context);
    }

    public ArcLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        holder = new Rect();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ArcLayout,
                0, 0);



        bgColor = a.getColor(R.styleable.ArcLayout_bg_color,
                getResources().getColor(R.color.primary));
        shadowColor = a.getColor(R.styleable.ArcLayout_shadow_color,
                getResources().getColor(R.color.shadow));
        topSpace = a.getDimensionPixelSize(R.styleable.ArcLayout_top_space,
                getResources().getDimensionPixelSize(R.dimen.arc_layout_default_top_space));
        shadowSize =  a.getDimensionPixelSize(R.styleable.ArcLayout_shadow_size,
                getResources().getDimensionPixelSize(R.dimen.arc_layout_default_shadow_size));
        buttonBorder =  a.getDimensionPixelSize(R.styleable.ArcLayout_button_border,
                getResources().getDimensionPixelSize(R.dimen.arc_layout_default_button_border));
        chordHeight =  a.getDimensionPixelSize(R.styleable.ArcLayout_chord_height,
                getResources().getDimensionPixelSize(R.dimen.arc_layout_default_chord_height));
        buttonSpacing =  a.getDimensionPixelSize(R.styleable.ArcLayout_button_spacing,
                getResources().getDimensionPixelSize(R.dimen.arc_layout_default_button_spacing));

        init();
    }

    public ArcLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                // Update our size information based on the layout params.  Children
                // that asked to be positioned on the left or right go in those gutters.
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.position == LayoutParams.POSITION_MIDDLE) {
                    maxWidth = MeasureSpec.getSize(widthMeasureSpec);
                    maxHeight = child.getMeasuredHeight() + buttonBorder * 2 + shadowSize + topSpace;
                }
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight());
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        FloatingActionButton middleButton = null;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.position == LayoutParams.POSITION_MIDDLE) {
                middleButton = (FloatingActionButton) child;
                break;
            }
        }

        //Костыль - вылетает NPE при детаче фрагмента
        if (middleButton == null) {
            return;
        }

        middleY = bottom - shadowSize - buttonBorder - middleButton.getMeasuredHeight() / 2;
        middleX = (right + left) / 2;

        arcRadius = (int)calcRadiusFromChord(right - left, chordHeight);
        middleToSideX = buttonSpacing;
        middleToSideY = arcRadius - (int)Math.sqrt(arcRadius * arcRadius -
                buttonSpacing * buttonSpacing);

        leftX = middleX - middleToSideX;
        leftY = middleY - middleToSideY;
        rightX = middleX + middleToSideX;
        rightY = middleY - middleToSideY;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                // Compute the frame in which we are placing this child.
                if (lp.position == LayoutParams.POSITION_LEFT) {
                    leftRadius = child.getMeasuredHeight()/2;
                    calcRect(child, leftX, leftY, holder);
                } else if (lp.position == LayoutParams.POSITION_RIGHT) {
                    rightRadius = child.getMeasuredHeight()/2;
                    calcRect(child, rightX, rightY, holder);
                } else {
                    middleRadius = child.getMeasuredHeight()/2;
                    calcRect(child, middleX, middleY, holder);
                }

                child.layout(holder.left, holder.top,
                        holder.right, holder.bottom);
            }
        }

        middleX = middleX - left;
        middleY = middleY - top;
        leftX = leftX - left;
        leftY = leftY - top;
        rightX = rightX - left;
        rightY = rightY - top;
    }


    public  int  getPreferredHeight() {
        return (int)(topSpace + buttonBorder * 2 +
                getResources().getDimension(R.dimen.fab_size_normal));
    }
    private void calcRect(View v, int centerX, int centerY, Rect out) {
        out.right = centerX + v.getMeasuredWidth()/2;
        out.top = centerY -  v.getMeasuredHeight()/2;
        out.left = centerX -  v.getMeasuredWidth()/2;
        out.bottom = centerY +  v.getMeasuredHeight()/2;
    }


    private float calcRadiusFromChord(float w, float h) {
        return h / 2 + w * w / (8 * h);
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
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
    }



    private boolean hasLeftChild() {
        return leftRadius >=0;
    }

    private boolean hasRightChild() {
        return rightRadius >=0;
    }

    protected void dispatchDraw(Canvas canvas) {

        //clear previous drawings
        bgPath.reset();

        //calc width and height to work with
        canvas.getClipBounds(bounds);
        boundsF.set(bounds);


        //add arc
        float arcCenterX = middleX;
        float arcCenterY = middleY + middleRadius * 0.25f - arcRadius;
        bgPath.addCircle(arcCenterX, arcCenterY, arcRadius, Path.Direction.CW);
        bgPath.addCircle(middleX, middleY, middleRadius + buttonBorder, Path.Direction.CW);
        if(hasLeftChild()) {
            bgPath.addCircle(leftX, leftY, leftRadius + buttonBorder, Path.Direction.CW);
        }
        if (hasRightChild()) {
            bgPath.addCircle(rightX, rightY, rightRadius + buttonBorder, Path.Direction.CW);
        }
        //draw bg
        canvas.drawPath(bgPath, mBgPaint);
        super.dispatchDraw(canvas);

    }


    /**
     * Custom per-child layout information.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */

        public static int POSITION_MIDDLE = 0;
        public static int POSITION_LEFT = 1;
        public static int POSITION_RIGHT = 2;

        public int position = POSITION_MIDDLE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Pull the layout param values from the layout XML during
            // inflation.  This is not needed if you don't care about
            // changing the layout behavior in XML.
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ArcLayoutLP);
            position = a.getInt(R.styleable.ArcLayoutLP_layout_position, position);
            a.recycle();
        }

        public LayoutParams() {
            super(null);
        }
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}