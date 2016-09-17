package cn.aibianli.sdot.common.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2015/12/10.
 */
public class InnerGridView extends GridView {

    public InnerGridView(Context context) {
        super (context);
    }

    public InnerGridView(Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public InnerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.
                makeMeasureSpec (Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure (widthMeasureSpec, expandSpec);
    }
}
