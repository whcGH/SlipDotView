package com.test.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by whc on 2018/7/16.
 */

public class SlipPointView extends View implements ViewPager.OnPageChangeListener {

    public SlipPointView(Context context) {
        this(context, null);
    }

    public SlipPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlipPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        mpPaint = new Paint();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.slip_point_view);
        setCount(array.getInt(R.styleable.slip_point_view_count, 0));
        setPointColor(array.getColor(R.styleable.slip_point_view_p_color, Color.TRANSPARENT));
        setMovePointColor(array.getColor(R.styleable.slip_point_view_mp_color, Color.TRANSPARENT));
        this.offsetSize = array.getDimensionPixelOffset(R.styleable.slip_point_view_offset_size, 0);
        this.expendSize = array.getDimensionPixelOffset(R.styleable.slip_point_view_expend_size, 0);
        this.radius = array.getDimensionPixelSize(R.styleable.slip_point_view_radius, 0);
        mp = new MP();

        offset = 0;
        currentPosition = 0;

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();

    }

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;


    private Paint paint;//画普通点
    private Paint mpPaint;//画动点
    private float offsetSize;//间距
    private float radius;//点半径
    private float expendSize;//展开长度
    //移动点
    private MP mp;
    //普通点
    private ArrayList<P> pList;
    //当前位置
    private int currentPosition;
    //偏移量
    private float offset;

    //间距
    public SlipPointView setOffsetSize(int offsetSize) {
        this.offsetSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offsetSize, getContext().getResources().getDisplayMetrics());
        return this;
    }

    //展开长度
    public SlipPointView setExpendSize(int expendSize) {
        this.expendSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, expendSize, getContext().getResources().getDisplayMetrics());
        return this;
    }

    //普通点颜色
    public SlipPointView setPointColor(int color) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setColor(color);
        return this;
    }

    //移动点颜色
    public SlipPointView setMovePointColor(int color) {
        mpPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mpPaint.setAntiAlias(true);
        mpPaint.setColor(color);
        return this;
    }

    //点个数
    public int getCount() {
        if (pList != null && pList.size() > 0) {
            return pList.size();
        } else
            return 0;
    }

    //设置点个数
    public SlipPointView setCount(int count) {
        if (pList == null) {
            pList = new ArrayList<>();
        }
        pList.clear();
        for (int i = 0; i < count; i++) {
            P p = new P(i);
            pList.add(p);
        }
        return this;
    }

    public SlipPointView setRadius(int radius) {
        this.radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getContext().getResources().getDisplayMetrics());
        return this;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        //设置宽高
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
            int width = paddingLeft + paddingRight;
            if (getCount() > 0) {
                width += (int) ((radius * 2 + offsetSize) * getCount() - offsetSize + expendSize);
            } else {
                width += 0;
            }
            specSize = width;
        }
        return specSize;
    }

    //根据xml的设定获取高度
    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
            specSize = (int) (radius * 2) + paddingTop + paddingBottom;
        }
        return specSize;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position % getCount();
        offset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        offset = 0;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //普通圆点
    private class P {

        P(int position) {
            this.position = position;
        }

        private int position;

        void onDraw(Canvas canvas, Paint paint, int currentPosition, float offset) {
            float drawX = radius * (position * 2 + 1) + offsetSize * (position);
            if (currentPosition == getCount() - 1 && position == 0) {
                drawX += (offset * expendSize / 2);
            } else if (currentPosition == getCount() - 1) {
                drawX += (offset * expendSize);
            } else if (position == currentPosition) {
                drawX += (expendSize / 2 - offset * expendSize / 2);
            } else if (position == currentPosition + 1) {
                drawX += (expendSize - offset * expendSize / 2);
            } else if (position > currentPosition + 1) {
                drawX += expendSize;
            }
            canvas.drawCircle(drawX + paddingLeft, radius + paddingTop, radius, paint);
        }
    }

    //移动圆点
    private class MP {
        void onDraw(Canvas canvas, Paint paint, int x) {
            canvas.drawCircle(x, radius + paddingTop, radius, paint);
            canvas.drawCircle(x + expendSize, radius + paddingTop, radius, paint);
            canvas.drawRect(x, paddingTop, x + expendSize, radius * 2 + paddingTop, paint);
        }

        void onDraw(Canvas canvas, Paint paint, int frontX, int backX) {
            //前
            if (offset != 0) {
                canvas.drawCircle(frontX, radius + paddingTop, radius, paint);
                canvas.drawCircle(radius, radius + paddingTop, radius, paint);
                canvas.drawRect(radius, paddingTop, frontX, radius * 2 + paddingTop, paint);
            }
            //后
            if (backX <= radius * (getCount() * 2 - 1) + expendSize + offsetSize * (getCount() - 1) + paddingLeft) {
                canvas.drawCircle(backX, radius + paddingTop, radius, paint);
                canvas.drawCircle(radius * (getCount() * 2 - 1) + expendSize + offsetSize * (getCount() - 1) + paddingLeft, radius + paddingTop, radius, paint);
                canvas.drawRect(backX, paddingTop, getWidth() - radius, radius * 2 + paddingTop, paint);
            }
        }

        void onDraw(Canvas canvas, Paint paint, int currentPosition, float offset) {
            if (currentPosition == getCount() - 1) {
                int frontX = (int) (radius + offset * expendSize);
                int backX = (int) (radius * (currentPosition * 2 + 1) + offsetSize * currentPosition + offset * expendSize);
                onDraw(canvas, paint, frontX + paddingLeft, backX + paddingLeft);
            } else {
                int drawX = (int) (radius * (currentPosition * 2 + 1) + offset * (offsetSize + radius * 2) + offsetSize * currentPosition);
                onDraw(canvas, paint, drawX + paddingLeft);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pList != null && pList.size() > 0) {
            for (P p : pList) {
                p.onDraw(canvas, paint, currentPosition, offset);
            }
        }
        mp.onDraw(canvas, mpPaint, currentPosition, offset);
    }

    public void attachToViewpager(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(this);
        this.setCount(viewPager.getAdapter().getCount());
    }
}
