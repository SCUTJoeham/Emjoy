package com.example.emjoy.crop.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.emjoy.crop.edge.Edge;
import com.example.emjoy.crop.handle.CropWindowEdgeSelector;
import com.example.emjoy.crop.util.CatchEdgeUtil;
import com.example.emjoy.crop.util.UIUtil;


@SuppressLint("AppCompatCustomView")
public class CropImageView extends ImageView {

    //裁剪框边框画笔
    private Paint mBorderPaint;

    //裁剪框九宫格画笔
    private Paint mGuidelinePaint;

    //绘制裁剪边框四个角的画笔
    private Paint mCornerPaint;


    //判断手指位置是否处于缩放裁剪框位置的范围：如果是当手指移动的时候裁剪框会相应的变化大小
    //否则手指移动的时候就是拖动裁剪框使之随着手指移动
    private float mScaleRadius;

    private float mCornerThickness;

    private float mBorderThickness;

    //四个角小短边的长度
    private float mCornerLength;

    //用来表示图片边界的矩形
    private RectF mBitmapRect = new RectF();

    //手指位置距离裁剪框的偏移量
    private PointF mTouchOffset = new PointF();


    private CropWindowEdgeSelector mPressedCropWindowEdgeSelector;

    public CropImageView(Context context) {
        super(context);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    /**
     * 里面的值暂时写死，也可以从AttributeSet里面来配置
     *
     * @param context
     */
    private void init(@NonNull Context context) {

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(UIUtil.dip2px(context, 3));
        mBorderPaint.setColor(Color.parseColor("#AAFFFFFF"));

        mGuidelinePaint = new Paint();
        mGuidelinePaint.setStyle(Paint.Style.STROKE);
        mGuidelinePaint.setStrokeWidth(UIUtil.dip2px(context, 1));
        mGuidelinePaint.setColor(Color.parseColor("#AAFFFFFF"));


        mCornerPaint = new Paint();
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(UIUtil.dip2px(context, 5));
        mCornerPaint.setColor(Color.WHITE);


        mScaleRadius = UIUtil.dip2px(context, 24);
        mBorderThickness = UIUtil.dip2px(context, 3);
        mCornerThickness = UIUtil.dip2px(context, 5);
        mCornerLength = UIUtil.dip2px(context, 20);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);

        mBitmapRect = getBitmapRect();
        initCropWindow(mBitmapRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        //绘制九宫格引导线
        drawGuidelines(canvas);
        //绘制裁剪边框
        drawBorder(canvas);
        //绘制裁剪边框的四个角
        drawCorners(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }


    /**
     * 获取裁剪好的BitMap
     */
    public Bitmap getCroppedImage() {


        final Drawable drawable = getDrawable();
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            return null;
        }

        final float[] matrixValues = new float[9];
        getImageMatrix().getValues(matrixValues);

        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        float bitmapLeft = (transX < 0) ? Math.abs(transX) : 0;
        float bitmapTop = (transY < 0) ? Math.abs(transY) : 0;

        final Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();

        final float cropX = (bitmapLeft + Edge.LEFT.getCoordinate()) / scaleX;
        final float cropY = (bitmapTop + Edge.TOP.getCoordinate()) / scaleY;

        final float cropWidth = Math.min(Edge.getWidth() / scaleX, originalBitmap.getWidth() - cropX);
        final float cropHeight = Math.min(Edge.getHeight() / scaleY, originalBitmap.getHeight() - cropY);

        return Bitmap.createBitmap(originalBitmap,
                (int) cropX,
                (int) cropY,
                (int) cropWidth,
                (int) cropHeight);

    }


    /**
     * 获取图片ImageView周围的边界组成的RectF对象
     */
    private RectF getBitmapRect() {

        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return new RectF();
        }

        final float[] matrixValues = new float[9];
        getImageMatrix().getValues(matrixValues);

        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final int drawableIntrinsicWidth = drawable.getIntrinsicWidth();
        final int drawableIntrinsicHeight = drawable.getIntrinsicHeight();

        final int drawableDisplayWidth = Math.round(drawableIntrinsicWidth * scaleX);
        final int drawableDisplayHeight = Math.round(drawableIntrinsicHeight * scaleY);

        final float left = Math.max(transX, 0);
        final float top = Math.max(transY, 0);
        final float right = Math.min(left + drawableDisplayWidth, getWidth());
        final float bottom = Math.min(top + drawableDisplayHeight, getHeight());

        return new RectF(left, top, right, bottom);
    }

    /**
     * 初始化裁剪框
     *
     * @param bitmapRect
     */
    private void initCropWindow(@NonNull RectF bitmapRect) {

        //裁剪框距离图片左右的padding值
        final float horizontalPadding = 0.01f * bitmapRect.width();
        final float verticalPadding = 0.01f * bitmapRect.height();

        //初始化裁剪框上下左右四条边
        Edge.LEFT.initCoordinate(bitmapRect.left + horizontalPadding);
        Edge.TOP.initCoordinate(bitmapRect.top + verticalPadding);
        Edge.RIGHT.initCoordinate(bitmapRect.right - horizontalPadding);
        Edge.BOTTOM.initCoordinate(bitmapRect.bottom - verticalPadding);
    }

    private void drawGuidelines(@NonNull Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        //引导线竖直方向第一条线
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        //引导线竖直方向第二条线
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        //引导线水平方向第一条线
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        //引导线水平方向第二条线
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

    private void drawBorder(@NonNull Canvas canvas) {

        canvas.drawRect(Edge.LEFT.getCoordinate(),
                Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(),
                Edge.BOTTOM.getCoordinate(),
                mBorderPaint);
    }


    private void drawCorners(@NonNull Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        //简单的数学计算

        final float lateralOffset = (mCornerThickness - mBorderThickness) / 2f;
        final float startOffset = mCornerThickness - (mBorderThickness / 2f);

        //左上角左面的短线
        canvas.drawLine(left - lateralOffset, top - startOffset, left - lateralOffset, top + mCornerLength, mCornerPaint);
        //左上角上面的短线
        canvas.drawLine(left - startOffset, top - lateralOffset, left + mCornerLength, top - lateralOffset, mCornerPaint);

        //右上角右面的短线
        canvas.drawLine(right + lateralOffset, top - startOffset, right + lateralOffset, top + mCornerLength, mCornerPaint);
        //右上角上面的短线
        canvas.drawLine(right + startOffset, top - lateralOffset, right - mCornerLength, top - lateralOffset, mCornerPaint);

        //左下角左面的短线
        canvas.drawLine(left - lateralOffset, bottom + startOffset, left - lateralOffset, bottom - mCornerLength, mCornerPaint);
        //左下角底部的短线
        canvas.drawLine(left - startOffset, bottom + lateralOffset, left + mCornerLength, bottom + lateralOffset, mCornerPaint);

        //右下角左面的短线
        canvas.drawLine(right + lateralOffset, bottom + startOffset, right + lateralOffset, bottom - mCornerLength, mCornerPaint);
        //右下角底部的短线
        canvas.drawLine(right + startOffset, bottom + lateralOffset, right - mCornerLength, bottom + lateralOffset, mCornerPaint);
    }

    /**
     * 处理手指按下事件
     * @param x 手指按下时水平方向的坐标
     * @param y 手指按下时竖直方向的坐标
     */
    private void onActionDown(float x, float y) {

        //获取边框的上下左右四个坐标点的坐标
        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        //获取手指所在位置位于图二种的A，B，C，D位置种哪一种
        mPressedCropWindowEdgeSelector = CatchEdgeUtil.getPressedHandle(x, y, left, top, right, bottom, mScaleRadius);

        if (mPressedCropWindowEdgeSelector != null) {
            //计算手指按下的位置与裁剪框的偏移量
            CatchEdgeUtil.getOffset(mPressedCropWindowEdgeSelector, x, y, left, top, right, bottom, mTouchOffset);
            invalidate();
        }
    }


    private void onActionUp() {
        if (mPressedCropWindowEdgeSelector != null) {
            mPressedCropWindowEdgeSelector = null;
            invalidate();
        }
    }


    private void onActionMove(float x, float y) {

        if (mPressedCropWindowEdgeSelector == null) {
            return;
        }

        x += mTouchOffset.x;
        y += mTouchOffset.y;


        mPressedCropWindowEdgeSelector.updateCropWindow(x, y, mBitmapRect);
        invalidate();
    }

}