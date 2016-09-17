package cn.aibianli.sdot.common.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2015/12/10.
 */
public class InnerListView extends ListView {

    public InnerListView(Context context) {
        super (context);
    }

    public InnerListView(Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public InnerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.
                makeMeasureSpec (Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure (widthMeasureSpec, expandSpec);
    }
}
