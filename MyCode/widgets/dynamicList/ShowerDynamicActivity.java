package net.xichiheng.yulewa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.base.BaseTitleActivity;
import net.xichiheng.yulewa.fragment.show.DynamicListFragment;
import net.xichiheng.yulewa.http.API;
import net.xichiheng.yulewa.interfaces.IEditInputBtnListener;
import net.xichiheng.yulewa.interfaces.IPopSoftInputListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;

/**
 * Created by mac on 16/1/5.
 */
@ContentView(R.layout.view_framelayout)
public class ShowerDynamicActivity extends BaseTitleActivity implements IPopSoftInputListener {

    public final static int Tag_RiseDynamicReturn = 9;

    @ViewInject(R.id.softinput_window)
    private RelativeLayout softinput_window;
    @ViewInject(R.id.softinput_edit)
    private EditText softinput_edit;
    @ViewInject(R.id.softinput_send)
    private TextView softinput_send;

    private DynamicListFragment fragSwDynamic;
    private String showerId;
    private boolean isMe;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showerId = getIntent().getExtras().getString("showerId", "0");
        isMe = getIntent().getExtras().getBoolean("isMe", false);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        initTop();
        HashMap<String,Object> extra = new HashMap<>();
        extra.put("isMe",isMe);
        fragSwDynamic = new DynamicListFragment();
        API.ShowSingleDynamic singleDynamicPost = new API.ShowSingleDynamic();
        singleDynamicPost.userId = Integer.valueOf(showerId);
        fragSwDynamic.init(263, singleDynamicPost, R.layout.item_dynamic_list,extra);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, fragSwDynamic).commit();
    }

    private void initTop() {

        mTitleBar.setLeftTxt("返回");
        mTitleBar.setTitle("动态");
        mTitleBar.setLeftTxtClickListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (isMe) {
            mTitleBar.setRightTxt("发布");
            mTitleBar.setRightTxtListner(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toRaise = new Intent(ShowerDynamicActivity.this,RaiseDynamicActivity.class);
                    startActivityForResult(toRaise,Tag_RiseDynamicReturn);
                }
            });
        }
    }

    @Override
    public void popUpSoftInput(String hint, final IEditInputBtnListener iSoftInputListener) {
        softinput_window.setVisibility(View.VISIBLE);
        softinput_edit.setFocusable(true);
        softinput_edit.setFocusableInTouchMode(true);
        softinput_edit.requestFocus();
        softinput_edit.setMovementMethod(LinkMovementMethod.getInstance());
        softinput_edit.setHint(hint);
        softinput_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = softinput_edit.getText().toString();
                if (text.isEmpty()) {
                    showToast("输入不能为空");
                } else {
                    popDownSoftInput();
                    iSoftInputListener.BtnSendOnClick(text);
                    softinput_edit.setText("");
                }
            }
        });
        imm.showSoftInput(softinput_edit, 0);
    }

    @Override
    public void popDownSoftInput() {
        imm.hideSoftInputFromWindow(softinput_edit.getWindowToken(), 0);
        softinput_window.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (softinput_window.getVisibility() == View.VISIBLE) {
            softinput_window.setVisibility(View.GONE);
            softinput_edit.setText("");
        } else {
            super.onBackPressed();
        }
    }

}
