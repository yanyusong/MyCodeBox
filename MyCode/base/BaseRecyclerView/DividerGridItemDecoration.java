package net.boyazhidao.cgb.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    //    private Drawable mDrawable;

    private Paint mPaint;
    private int lineWidth;//px 分割线宽
    /**
     * A single color value in the form 0xAARRGGBB.
     **/
    private int colorRGB;
    private int headerCount = 0;
    private int footerCount = 0;


    public DividerGridItemDecoration(Context context, int headerViewCount, int footerViewCount, int lineWidthDp, int mColorRGB) {
        this.colorRGB = mColorRGB;
        this.lineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidthDp, context.getResources().getDisplayMetrics());
        this.headerCount = headerViewCount;
        this.footerCount = footerViewCount;
        //        int color = 0;
        //        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        //            color = context.getResources().getColor(colorResId, null);
        //        }else{
        //            color = Color.BLACK;
        //        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(colorRGB);
        mPaint.setStyle(Paint.Style.FILL);
        //        mDrawable = context.getResources().getDrawable(R.drawable.divider_item_black);
    }

    public DividerGridItemDecoration(Context context, int lineWidthDp, int mColorRGB) {
        this(context, 0, 0, lineWidthDp, mColorRGB);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int left = child.getLeft() - params.leftMargin;
            int right = child.getRight() + params.rightMargin
                    + lineWidth;
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + lineWidth;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;
            int left = child.getRight() + params.rightMargin;
            int right = left + lineWidth;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int left = 0;
        int top = 0;
        int right = lineWidth;
        int bottom = lineWidth;
        if (isLastColum(parent, itemPosition, spanCount, childCount)) {//最后一列不用绘制right
            right = 0;
        }
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {//最后一行不用绘制bottom
            bottom = 0;
        }
        outRect.set(left,top,right,bottom);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int num = (childCount - headerCount) % spanCount;
            num = num == 0 ? spanCount : num;
            childCount = childCount - num;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        }
        return false;
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos < headerCount) {
                return true;
            }
            if ((pos + 1 - headerCount) % spanCount == 0) {
                return true;
            }
            if (pos >= childCount - footerCount) {
                return true;
            }
        }
        return false;
    }

}

















