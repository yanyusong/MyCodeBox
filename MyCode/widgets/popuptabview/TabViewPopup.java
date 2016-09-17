package cn.aibianli.sdot.common.widgets.popuptabview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.aibianli.sdot.R;
import cn.aibianli.sdot.base.adapter.GeneralListAdapter;
import cn.aibianli.sdot.base.adapter.ViewHolder;

/**
 * Created by mac on 16/8/22.
 */
public class TabViewPopup {

    private PopupWindow popupWindow;

    private Context mContext;

    private List<String> titles = new ArrayList<>();

    private List<String> rightSubheadings = new ArrayList<>();

    private List<List<String>> subheadsings = new ArrayList<>();

    private int defaultCheckedPosition;

    private OnLeftLvItemClickedListener onLeftLvItemClickedListener;

    private OnRightLvItemClickedListener onRightLvItemClickedListener;


    public interface OnLeftLvItemClickedListener {
        void OnLeftLvItemClicked(AdapterView<?> parent, View view, int position, long id);
    }

    public interface OnRightLvItemClickedListener {
        void OnRightLvItemClicked(AdapterView<?> parent, View view, int position, long id);
    }

    public TabViewPopup(Context mContext, List<String> titles, List<List<String>> subheadsings, int defaultCheckedPosition) {
        this.mContext = mContext;
        this.titles = titles;
        this.subheadsings = subheadsings;
        this.defaultCheckedPosition = defaultCheckedPosition;
        initPopupWindow();
    }

    private void initPopupWindow() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_merchant_select_menu_view, null, false);
        ListView lvLeft = (ListView) view.findViewById(R.id.lv_left);
        final ListView lvRight = (ListView) view.findViewById(R.id.lv_right);
        FrameLayout bgDim = (FrameLayout) view.findViewById(R.id.bg_dim);
        lvLeft.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        lvLeft.setAdapter(new GeneralListAdapter<String>(mContext, titles, R.layout.item_poptab_left) {
            @Override
            public void SetChildViewData(ViewHolder mViewHolder, String itemData, int position) {
                CheckBox title = mViewHolder.getChildView(R.id.tv_title);
                title.setText(itemData);
            }
        });

        lvRight.setAdapter(new GeneralListAdapter<String>(mContext, rightSubheadings, R.layout.item_poptab_right) {
            @Override
            public void SetChildViewData(ViewHolder mViewHolder, String itemData, int position) {
                TextView subheading = mViewHolder.getChildView(R.id.tv_subheading);
                subheading.setText(itemData);
            }
        });

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rightSubheadings.clear();
                rightSubheadings.addAll(subheadsings.get(position));
                ((GeneralListAdapter) lvRight.getAdapter()).notifyDataSetChanged();
                if (onLeftLvItemClickedListener != null) {
                    onLeftLvItemClickedListener.OnLeftLvItemClicked(parent, view, position, id);
                }
            }
        });
        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onRightLvItemClickedListener != null) {
                    onRightLvItemClickedListener.OnRightLvItemClicked(parent, view, position, id);
                }
            }
        });
        bgDim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //        popupWindow.setOutsideTouchable(true);

    }

    public ListView getLeftLv() {
        ListView leftLv = (ListView) popupWindow.getContentView().findViewById(R.id.lv_left);
        return leftLv;
    }

    public ListView getRightLv() {
        ListView rightLv = (ListView) popupWindow.getContentView().findViewById(R.id.lv_right);
        return rightLv;
    }

    public void setLeftLvOnItemClickListener(OnLeftLvItemClickedListener listener) {
        this.onLeftLvItemClickedListener = listener;
    }

    public void setRightLvOnItemClickListener(OnRightLvItemClickedListener listener) {
        this.onRightLvItemClickedListener = listener;
    }

    public void showAsDropDown(View anchor) {
        popupWindow.showAsDropDown(anchor);
        switchCheckedToPos(defaultCheckedPosition);
    }

    public void showAsDropDown(View anchor, int curCheckedPosition) {
        popupWindow.showAsDropDown(anchor);
        switchCheckedToPos(curCheckedPosition);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    private void switchCheckedToPos(int position) {
        ListView lvleft = (ListView) popupWindow.getContentView().findViewById(R.id.lv_left);
        ListView lvRight = (ListView) popupWindow.getContentView().findViewById(R.id.lv_right);
        lvleft.setItemChecked(position, true);
        rightSubheadings.clear();
        rightSubheadings.addAll(subheadsings.get(position));
        ((GeneralListAdapter) lvRight.getAdapter()).notifyDataSetChanged();
    }
}
