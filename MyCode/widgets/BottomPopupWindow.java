package cn.aibianli.sdot.common.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.aibianli.sdot.R;
import cn.aibianli.sdot.base.adapter.GeneralListAdapter;
import cn.aibianli.sdot.base.adapter.ViewHolder;

/**
 * Created by mac on 16/7/15.
 */
public class BottomPopupWindow {

    protected PopupWindow mPopupWindow;
    //popup视图
    protected View mPopupView;

    private Activity context;

    private TextView cancel;
    private InnerListView popList;
    private FrameLayout background;
    private List<String> items = new ArrayList<>();


    public BottomPopupWindow(Activity context, List<String> items) {
        this.context = context;
        this.items = items;
        //默认占满全屏
        initPopupView();
        mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //指定透明背景，back键相关
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }


    private void initPopupView() {
        mPopupView = LayoutInflater.from(context).inflate(R.layout.view_pop_bottom, null);
        cancel = (TextView) mPopupView.findViewById(R.id.bottom_btn);
        popList = (InnerListView) mPopupView.findViewById(R.id.popup_list);
        background = (FrameLayout) mPopupView.findViewById(R.id.view_background);
        popList.setAdapter(new PopListAdapter(context, items, R.layout.item_single_text));
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        popList.setOnItemClickListener(listener);
    }

    public void setOnBottomItemClickListener(View.OnClickListener listener) {
        cancel.setOnClickListener(listener);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void show() {
        mPopupWindow.showAtLocation(context.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    class PopListAdapter extends GeneralListAdapter<String> {

        private List<String> items = new ArrayList<>();

        public PopListAdapter(Context ct, List<String> data, int itemLayoutId) {
            super(ct, data, itemLayoutId);
            this.items = data;
        }

        @Override
        public void SetChildViewData(ViewHolder mViewHolder, String itemData, int position) {
            TextView textView = mViewHolder.getChildView(R.id.single_textview);
            textView.setText(itemData);
        }
    }
}
