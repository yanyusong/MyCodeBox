package net.xichiheng.yulewa.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private List<View> checkableViews = new ArrayList<View> ();
    private Boolean mChecked = false;

    public CheckableRelativeLayout (Context context) {
        super (context);
    }

    public CheckableRelativeLayout (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    @Override
    protected void onAttachedToWindow () {
        super.onAttachedToWindow ();
        int count = getChildCount ();
        for (int i = 0; i < count; i++) {
            View child = getChildAt (i);
            if (child instanceof Checkable) {
                checkableViews.add (child);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow () {
        super.onDetachedFromWindow ();
        checkableViews.clear ();

    }

    @Override
    public void setChecked (boolean checked) {
        if (!checkableViews.isEmpty ()) {
            for (View childView : checkableViews) {
                ((Checkable) childView).setChecked (checked);
            }
        }
        mChecked = checked;
    }

    @Override
    public boolean isChecked () {
        return mChecked;
    }

    @Override
    public void toggle () {
        if (!checkableViews.isEmpty ()) {
            for (View childView : checkableViews) {
                ((Checkable) childView).setChecked (!mChecked);
            }
        }
        mChecked = !mChecked;
    }

}
