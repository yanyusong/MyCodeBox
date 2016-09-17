package net.abianli.ablweb.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.abianli.ablweb.R;


/**
 * 使用原生toolbar的兼容
 * Created by Clock on 2016/2/3.
 */
public abstract class BaseToolBarActivity extends BaseActivity {

    private LinearLayout layBase;
    protected Toolbar mToolbar;
    protected TextView mToolbarTitle;
    private FrameLayout mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow ().getAttributes ();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_base_toolbar);
        layBase = (LinearLayout) findViewById(R.id.base_lay);
        mToolbar = (Toolbar) findViewById(R.id.base_toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mContentView = (FrameLayout) findViewById(R.id.base_content);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitleTextColor(0xffffffff);//白色
        mContentView.addView(View.inflate(this, getLayoutId(), null));
    }

    public void setToolBarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    protected abstract int getLayoutId();

}
