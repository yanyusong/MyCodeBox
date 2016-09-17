package net.xichiheng.yulewa.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.wq.photo.widget.PickConfig;

import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.base.BaseTitleActivity;
import net.xichiheng.yulewa.fragment.show.ShowerPhotosFragment;
import net.xichiheng.yulewa.utils.PhotoWallUtils;

import org.xutils.view.annotation.ContentView;

/**
 * Created by mac on 16/1/5.
 */
@ContentView(R.layout.view_framelayout)
public class ShowerPhotowallActivity extends BaseTitleActivity {

    private ShowerPhotosFragment fragSwPhoto;
    private String showerId;
    private boolean isMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showerId = getIntent().getExtras().getString("showerId", "0");
        isMe = getIntent().getExtras().getBoolean("isMe", false);

        initTop();
        fragSwPhoto = ShowerPhotosFragment.newInstance(showerId, isMe);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, fragSwPhoto).commit();
    }

    private void initTop() {

        mTitleBar.setLeftTxt("返回");
        mTitleBar.setTitle("照片墙");
        mTitleBar.setLeftTxtClickListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fragSwPhoto.getIsUploadComplete()) {
                    new AlertDialog.Builder(ct)
                            .setTitle("提示")
                            .setMessage("是否保存您刚才添加的照片？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fragSwPhoto.doCompleted();
                                }
                            })
                            .show();
                } else {
                    finish();
                }

            }
        });

    }

    public void setRightText(boolean isEnable) {
        if (isEnable) {
            mTitleBar.setRightTxt("完成");
            mTitleBar.setRightTxtListner(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragSwPhoto.doCompleted();
                }
            });
        } else {
            mTitleBar.setRightTxtEnable(false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if ((requestCode == PhotoWallUtils.TAG_TAKE_PHOTO) || (requestCode == PickConfig.PICK_REQUEST_CODE)) {
                fragSwPhoto.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


}




























