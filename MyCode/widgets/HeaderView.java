package net.xichiheng.yulewa.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import net.xichiheng.yulewa.R;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 导航栏
 * 在这个类中定义导航栏的所有操作
 */
public class HeaderView extends RelativeLayout {
    @ViewInject (R.id.header_bg)
    private RelativeLayout bg_lay;
    @ViewInject (R.id.header_lefttxt)
    private TextView leftTxt;
    @ViewInject (R.id.header_left)
    private ImageView leftImg;
    @ViewInject (R.id.header_rightimg)
    private ImageView rightImg;
    @ViewInject (R.id.header_title)
    private TextView title;
    @ViewInject (R.id.header_citylay)
    private RelativeLayout cityLay;

    @ViewInject (R.id.header_cityimg)
    private ImageView cityImg;
    @ViewInject (R.id.header_city)
    private TextView city;
    @ViewInject (R.id.header_city_jiantou)
    private ImageView city_jiantou;
    @ViewInject (R.id.header_righttxt)
    private TextView rightTxt;
    @ViewInject (R.id.header_tabslay)
    private RadioGroup tabsGroup;
    @ViewInject (R.id.header_tableft)
    private RadioButton leftTab;
    @ViewInject (R.id.header_tabright)
    private RadioButton rightTab;

    @ViewInject (R.id.header_radios_lay)
    private RadioGroup radioGroup;

    @ViewInject (R.id.header_radios_left)
    private RadioButton radioLeft;
    @ViewInject (R.id.header_radios_middle)
    private RadioButton radioMiddle;
    @ViewInject (R.id.header_radios_right)
    private RadioButton radioRight;

    private String mTitle;

    /**
     * 扩展组件时分为 步
     * 1.完成XML
     * 2.findview
     * 3.setView的方法
     * 4.setListener的方法（需实现接口
     * 1>定义接口
     * 2>实例化接口
     * 3>set(接口)方法留下初始化接口的方法
     * 4>组件监听事件中调用接口方法）
     */
    public HeaderView (Context context) {
        super (context);
        initView (context);
    }

    public HeaderView (Context context, AttributeSet attrs) {
        super (context, attrs);
        setXMLAttrs (context, attrs);
        initView (context);
    }

    /**
     * 在这里解析自定义属性的值及相应的操作
     *
     * @param context
     * @param attrs
     */
    private void setXMLAttrs (Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes (attrs, R.styleable.TitleBar);
        for (int i = 0; i < ta.getIndexCount (); i++) {
            int attr = ta.getIndex (i);
            switch (attr) {
                case R.styleable.TitleBar_title_text:
                    this.mTitle = ta.getString (attr);
                    break;
                /*case R.styleable.NavigationBar_right_button_text:
                    rightBtText = ta.getString(attr);
                    break;
                case R.styleable.NavigationBar_right_button_img:
                    rightBtResId = ta.getResourceId(attr, rightBtResId);
                    break;
                case R.styleable.NavigationBar_show_left_button:
                    isShowleftBt = ta.getBoolean(attr, isShowleftBt);
                    break;*/
            }
        }
        ta.recycle ();
    }

    private void initView (final Context context) {
        View view = LayoutInflater.from (context).inflate (R.layout.view_header, this);
        x.view ().inject (this, view);
//////////////////////////这里可以配置默认的点击事件///////////////////////////////
        leftTxt.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                ((Activity) context).finish ();
            }
        });


    }


    public void setTitle (String value) {
        title.setVisibility (View.VISIBLE);
        title.setText (Html.fromHtml (value));
    }

    public void setLeftImg (int resId) {
        leftImg.setVisibility (View.VISIBLE);
        leftImg.setImageResource (resId);
    }


    public void setRightImg (int resId) {
        rightImg.setVisibility (View.VISIBLE);
        rightImg.setImageResource (resId);
    }

    public void hide () {
        this.setVisibility (GONE);
    }

    public void setRightImgEnable (Boolean enable) {
        if (enable) {
            rightImg.setVisibility (View.VISIBLE);
        } else {
            rightImg.setVisibility (View.GONE);
        }
    }

    public void setRightTxtEnable (Boolean enable) {
        if (enable == true) {
            rightTxt.setVisibility (View.VISIBLE);
        } else {
            rightTxt.setVisibility (View.GONE);
        }
    }

    public void setLeftTxtPadding (int left, int top, int right, int bottom) {
        leftTxt.setPadding (left, top, right, bottom);
    }

    public void setLeftTxt (String Value) {
        leftTxt.setVisibility (View.VISIBLE);
        leftTxt.setText (Html.fromHtml (Value));
    }

    public void showUp (int resId) {
        city_jiantou.setVisibility (View.VISIBLE);
        city_jiantou.setImageResource (resId);
    }

    public void setCityImg (int resId) {
        cityImg.setVisibility (View.VISIBLE);
        cityImg.setImageResource (resId);
    }

    public void setMargins (int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams ();
        lp.setMargins (left, top, right, bottom);
        this.setLayoutParams (lp);
    }

    public void setCity (String Value) {
        cityLay.setVisibility (View.VISIBLE);
        city.setVisibility (View.VISIBLE);
        city.setText (Html.fromHtml (Value));
    }

    public void setLeftTab (String Value) {
        if (tabsGroup.getVisibility () != VISIBLE) {
            tabsGroup.setVisibility (View.VISIBLE);
        }
        leftTab.setVisibility (View.VISIBLE);
        leftTab.setText (Html.fromHtml (Value));
        if (rightTab.isChecked () == false) {
            leftTab.setChecked (true);
        }
    }

    public void setLeftTab (String Value, int size) {
        if (tabsGroup.getVisibility () != VISIBLE) {
            tabsGroup.setVisibility (View.VISIBLE);
        }
        leftTab.setVisibility (View.VISIBLE);
        leftTab.setTextSize (TypedValue.COMPLEX_UNIT_DIP, size);
        leftTab.setText (Html.fromHtml (Value));
        if (rightTab.isChecked () == false) {
            leftTab.setChecked (true);
        }
    }

    public void setLeftTabChecked () {
        if (leftTab.isChecked () == false) {
            leftTab.setChecked (true);
        }
    }

    public void setRightTab (String Value, int size) {
        if (tabsGroup.getVisibility () != VISIBLE) {
            tabsGroup.setVisibility (View.VISIBLE);
        }
        rightTab.setVisibility (View.VISIBLE);
        rightTab.setTextSize (TypedValue.COMPLEX_UNIT_DIP, size);
        rightTab.setText (Html.fromHtml (Value));
        if (leftTab.isChecked () == false) {
            rightTab.setChecked (true);
        }
    }

    public void setRightTab (String Value) {
        if (tabsGroup.getVisibility () != VISIBLE) {
            tabsGroup.setVisibility (View.VISIBLE);
        }
        rightTab.setVisibility (View.VISIBLE);
        rightTab.setText (Html.fromHtml (Value));
        if (leftTab.isChecked () == false) {
            rightTab.setChecked (true);
        }
    }

    public void setRightTabChecked () {
        if (rightTab.isChecked () == false) {
            rightTab.setChecked (true);
        }
    }

    /**
     * 设置左边radio的text和字号大小
     * radioLeft,radioMiddle,radioRight中的默认选中是三个中最先调用set方法的组件
     *
     * @param Value sectionName
     * @param size  字号，单位dp,默认则传－1
     */
    public void setLeftRadio (String Value, int size) {
        if (radioGroup.getVisibility () != VISIBLE) {
            radioGroup.setVisibility (View.VISIBLE);
        }
        radioLeft.setVisibility (View.VISIBLE);
        if (size != -1) {
            radioLeft.setTextSize (TypedValue.COMPLEX_UNIT_DIP, size);
        }
        radioLeft.setText (Html.fromHtml (Value));
        if ((radioMiddle.isChecked () == false)
                && (radioRight.isChecked () == false)) {
            radioLeft.setChecked (true);
        }
    }

    /**
     * 设置中间radio的text和字号大小
     * radioLeft,radioMiddle,radioRight中的默认选中是三个中最先调用set方法的组件
     *
     * @param Value sectionName
     * @param size  字号，单位dp,默认则传－1
     */
    public void setMiddleRadio (String Value, int size) {
        if (radioGroup.getVisibility () != VISIBLE) {
            radioGroup.setVisibility (View.VISIBLE);
        }
        radioMiddle.setVisibility (View.VISIBLE);
        if (size != -1) {
            radioMiddle.setTextSize (TypedValue.COMPLEX_UNIT_DIP, size);
        }
        radioMiddle.setText (Html.fromHtml (Value));
        if ((!radioLeft.isChecked ())
                && (!radioRight.isChecked ())) {
            radioMiddle.setChecked (true);
        }
    }

    /**
     * 设置右边radio的text和字号大小
     * radioLeft,radioMiddle,radioRight中的默认选中是三个中最先调用set方法的组件
     *
     * @param Value sectionName
     * @param size  字号，单位dp,默认则传－1
     */
    public void setRightRadio (String Value, int size) {
        if (radioGroup.getVisibility () != VISIBLE) {
            radioGroup.setVisibility (View.VISIBLE);
        }
        radioRight.setVisibility (View.VISIBLE);
        if (size != -1) {
            radioRight.setTextSize (TypedValue.COMPLEX_UNIT_DIP, size);
        }
        radioRight.setText (Html.fromHtml (Value));
        if ((radioMiddle.isChecked () == false)
                && (radioLeft.isChecked () == false)) {
            radioRight.setChecked (true);
        }
    }

    public void setBackgroundRes (int resId) {
        bg_lay.setBackgroundResource (resId);
    }

    public void setLeftClickListner (OnClickListener listener) {
        leftImg.setOnClickListener (listener);
    }

    public void setLeftTxtClickListner (OnClickListener listener) {
        leftTxt.setOnClickListener (listener);
    }

    public void setRightImgListner (OnClickListener listener) {
        rightImg.setOnClickListener (listener);
    }

    public void setRightTxtListner (OnClickListener listener) {
        rightTxt.setOnClickListener (listener);
    }

    public void setJantouClickListner (OnClickListener listener) {
        city_jiantou.setOnClickListener (listener);
    }

    public void setOnTabsCheckedListener (final OnTabsCheckedListener listener) {
        tabsGroup.setOnCheckedChangeListener (new OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged (RadioGroup arg0, int checkedId) {
                switch (checkedId) {
                    case R.id.header_tableft:
                        if (listener != null) {
                            listener.onLeftTabChecked ();
                        }
                        break;
                    case R.id.header_tabright:
                        if (listener != null) {
                            listener.onRightTabChecked ();
                        }
                        break;
                }
            }
        });
    }

    public void setOnRadiosCheckedListener (final OnRadiosCheckedListener listener) {

        radioGroup.setOnCheckedChangeListener (new OnCheckedChangeListener () {

            @Override
            public void onCheckedChanged (RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.header_radios_left:
                        if (listener != null) {
                            listener.onLeftRadioChecked ();
                        }
                        break;
                    case R.id.header_radios_middle:
                        if (listener != null) {
                            listener.onMiddleRadioChecked ();
                        }
                        break;
                    case R.id.header_radios_right:
                        if (listener != null) {
                            listener.onRightRadioChecked ();
                        }
                        break;
                }
            }
        });
    }


    public String getRightTxt () {
        return String.valueOf (rightTxt.getText ());
    }

    public void setRightTxt (String Value) {
        rightTxt.setVisibility (View.VISIBLE);
        rightTxt.setText (Html.fromHtml (Value));
    }

    /**
     * 标题栏右侧文本是否可见
     * View.VISIBLE 可见
     * View.GONE    不可见
     *
     * @param v
     */
    public void setRightTxtVisible (int v) {
        rightTxt.setVisibility (v);
    }


    public interface OnTabsCheckedListener {
        /**
         * 左边tab的checked事件
         */
        void onLeftTabChecked ();

        /**
         * 右边tab的checked的事件
         */
        void onRightTabChecked ();
    }

    public interface OnRadiosCheckedListener {
        /**
         * 左边radio的checked事件
         */
        void onLeftRadioChecked ();

        /**
         * 中间radio的checked事件
         */
        void onMiddleRadioChecked ();

        /**
         * 右边radio的checked的事件
         */
        void onRightRadioChecked ();
    }
}
