package net.boyazhidao.cgb.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.yoojia.anyversion.NotifyStyle;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import net.boyazhidao.cgb.CGBapplication;
import net.boyazhidao.cgb.R;
import net.boyazhidao.cgb.base.BaseToolBarActivity;
import net.boyazhidao.cgb.cache.SpCacheKey;
import net.boyazhidao.cgb.fragment.DemandProductListFrag;
import net.boyazhidao.cgb.fragment.MyMessageListFrag;
import net.boyazhidao.cgb.fragment.SettingFragment;
import net.boyazhidao.cgb.fragment.SupplyProductListFrag;
import net.boyazhidao.cgb.http.URLS;
import net.boyazhidao.cgb.http_volley.RequestInfo;
import net.boyazhidao.cgb.utils.FragUtils;
import net.boyazhidao.cgb.utils.SPUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseToolBarActivity {

    private static String Tag_DemandProductListFrag = "DemandProductListFrag";
    private static String Tag_SupplyProductListFrag = "SupplyProductListFrag";
    private static String Tag_MyMessageListFrag = "MyMessageListFrag";
    private static String Tag_SettingFrag = "SettingFrag";

    private Drawer mainDrawer = null;
    private FragUtils fragUtils;

    enum fragTagEnums {
        DemandProductListFrag, SupplyProductListFrag, MyMessageListFrag, SettingFrag
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
        fragUtils = initFragUtils();
        fragUtils.showFragment(Tag_DemandProductListFrag);
        initSlidingDrawer(savedInstanceState);
        initListeners();
        //检查版本更新
        CGBapplication.getAnyVersionInstance().check(NotifyStyle.Dialog);
    }

    private FragUtils initFragUtils() {

        String[] fragTags = new String[]{Tag_DemandProductListFrag, Tag_SupplyProductListFrag, Tag_MyMessageListFrag, Tag_SettingFrag};

        FragUtils utils = new FragUtils(getSupportFragmentManager(), R.id.frameContent, fragTags, new FragUtils.IFragmentInitMethods() {
            @Override
            public Fragment initFrag(String fragTag) {
                Fragment tempFrag = null;
                switch (fragTagEnums.valueOf(fragTag)) {
                    case DemandProductListFrag:
                        DemandProductListFrag mDemandProductListFrag = new DemandProductListFrag();
                        RequestInfo reqInfo = new RequestInfo(URLS.GET_PUBLISH_GOODS_LIST);
                        mDemandProductListFrag.init(Tag_DemandProductListFrag, reqInfo, false, false, R.layout.item_product_need);
                        tempFrag = mDemandProductListFrag;
                        break;
                    case SupplyProductListFrag:
                        SupplyProductListFrag mSupplyProductListFrag = new SupplyProductListFrag();
                        RequestInfo reqInfo1 = new RequestInfo(URLS.GET_SUPPLY_GOODS_LIST);
                        StringBuilder stringBuilder = new StringBuilder(SPUtils.get(mContext, SpCacheKey.tokenType, "").toString());
                        stringBuilder.append(" ");
                        stringBuilder.append(SPUtils.get(mContext, SpCacheKey.accessToken, "").toString());
                        reqInfo1.addHeaders("Authorization", stringBuilder.toString());
                        Log.e("SupplyProductListFrag", "Authorization的值是" + stringBuilder.toString());
                        mSupplyProductListFrag.init(Tag_SupplyProductListFrag, reqInfo1, true, false, R.layout.item_product_supply);
                        tempFrag = mSupplyProductListFrag;
                        break;
                    case MyMessageListFrag:
                        MyMessageListFrag myMessageListFrag = new MyMessageListFrag();
                        RequestInfo reqInfo2 = new RequestInfo(URLS.GET_APPLY_PRODUCTS_LIST);
                        myMessageListFrag.init(Tag_MyMessageListFrag, reqInfo2, false, false, R.layout.item_apply_product_list);
                        tempFrag = myMessageListFrag;
                        break;
                    case SettingFrag:
                        SettingFragment mSettingFragment = new SettingFragment();
                        tempFrag = mSettingFragment;
                        break;
                    default:
                        break;
                }

                return tempFrag;
            }
        });
        return utils;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initAppBar() {
        setToolBarTitle("需求信息");
    }

    private void initListeners() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = mainDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mainDrawer != null && mainDrawer.isDrawerOpen()) {
            mainDrawer.closeDrawer();
            return;
        }
        if (!fragUtils.getCurrentFragTag().equals(Tag_DemandProductListFrag)) {
            fragUtils.showFragment(Tag_DemandProductListFrag);
        } else {
            exitBy2Click();
        }
    }

    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            cgbApplication.exit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CGBapplication.getInstance().getRequestQueue().cancelAll(Tag_DemandProductListFrag);
    }


    private void initSlidingDrawer(Bundle savedInstanceState) {
        // Handle Toolbar
        mainDrawer = new DrawerBuilder()
                .withActivity(this)
                .withRootView(R.id.drawer_container)
                .withHeader(R.layout.view_drawer_header)
                .withHeaderDivider(false)
                .withSavedInstance(savedInstanceState)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withDrawerLayout(R.layout.material_drawer)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("需求信息").withIcon(R.mipmap.ic_assignment_turned_in_black_48dp).withIdentifier(1),
                        new PrimaryDrawerItem().withName("我的商品").withIcon(R.mipmap.ic_local_grocery_store_black_48dp).withIdentifier(2),
                        new PrimaryDrawerItem().withName("我的消息").withIcon(R.mipmap.ic_chat_bubble_outline_black_48dp).withIdentifier(3),
                        new PrimaryDrawerItem().withName("设置").withIcon(R.mipmap.ic_build_black_48dp).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            switch (drawerItem.getIdentifier()) {
                                case 1:
                                    setToolBarTitle("需求信息");
                                    fragUtils.showFragment(Tag_DemandProductListFrag);
                                    break;
                                case 2:
                                    setToolBarTitle("我的商品");
                                    fragUtils.showFragment(Tag_SupplyProductListFrag);
                                    break;
                                case 3:
                                    setToolBarTitle("我的消息");
                                    fragUtils.showFragment(Tag_MyMessageListFrag);
                                    break;
                                case 4:
                                    setToolBarTitle("设置");
                                    fragUtils.showFragment(Tag_SettingFrag);
                                    break;
                                default:
                                    break;
                            }
                        }
                        return false;
                    }
                }).build();
    }
}
