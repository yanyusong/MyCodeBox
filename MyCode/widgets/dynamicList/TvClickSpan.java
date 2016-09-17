package net.xichiheng.yulewa.widget;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class TvClickSpan extends ClickableSpan {

    private Context ct;
    private int color;
    private iTvOnClickSpanListener mOnClickListener;

    public TvClickSpan (Context ct, int color, iTvOnClickSpanListener mOnClickListener) {
        super ();
        this.ct = ct;
        this.color = color;
        this.mOnClickListener = mOnClickListener;
    }

    public TvClickSpan (Context ct, iTvOnClickSpanListener mOnClickListener) {
        super ();
        this.ct = ct;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public void updateDrawState (TextPaint ds) {
        //中绿色0x1ea707
        ds.setColor (color);
    }

    @Override
    public void onClick (View widget) {

        mOnClickListener.OnClick ();
    }

    public interface iTvOnClickSpanListener {
        void OnClick ();
    }

}
