package net.boyazhidao.cgb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;

import net.boyazhidao.cgb.CGBapplication;
import net.boyazhidao.cgb.R;
import net.boyazhidao.cgb.base.BaseActivity;
import net.boyazhidao.cgb.cache.SpCacheKey;
import net.boyazhidao.cgb.http.URLS;
import net.boyazhidao.cgb.http_volley.RequestInfo;
import net.boyazhidao.cgb.http_volley.VolleyResponse;
import net.boyazhidao.cgb.model.LoginModel;
import net.boyazhidao.cgb.utils.SPUtils;

/**
 * Created by mac on 16/3/12.
 */
public class WelcomeActivity extends BaseActivity {
    private Handler handler = new Handler();
    private volatile boolean isLogined = false;
    private String accountCache;
    private String passwordCache;
    Runnable loadingRun = new Runnable() {
        @Override
        public void run() {
            if (isLogined) {
                //登录成功了就跳转
                handler.removeCallbacks(loadingRun);
                Intent toMain = new Intent(mContext, MainActivity.class);
                startActivity(toMain);
                finish();
            } else {
                //没有则接着轮询
                handler.postDelayed(loadingRun, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //开启自动登录，两秒后检查
        autoLogin();
        handler.postDelayed(loadingRun, 2000);
    }

    private void autoLogin() {
        accountCache = SPUtils.get(mContext, SpCacheKey.accountNum, "").toString();
        passwordCache = SPUtils.get(mContext, SpCacheKey.passWord, "").toString();
        //查看缓存是否登录过，有则开始登录，没有则跳转到登录页面
        if (checkLoginFormat(accountCache, passwordCache)) {

            RequestInfo requestInfo = new RequestInfo(URLS.LOGIN);

            requestInfo.addBodyParams("username", accountCache);
            requestInfo.addBodyParams("password", passwordCache);
            requestInfo.addBodyParams("grant_type", "password");
            requestInfo.addBodyParams("client_secret", "123@abc");
            requestInfo.addBodyParams("client_id", "consoleApp");
            loadData("WelcomeActivity", requestInfo, false, false, new VolleyResponse.strReqCallback() {
                @Override
                public void success(String response) {
                    LoginModel login = JSON.parseObject(response, LoginModel.class);
                    SPUtils.put(mContext, SpCacheKey.userId, login.getUserId());
                    SPUtils.put(mContext, SpCacheKey.accountNum, accountCache);
                    SPUtils.put(mContext, SpCacheKey.passWord, passwordCache);
                    SPUtils.put(mContext, SpCacheKey.isRememberPassword, true);
                    SPUtils.put(mContext, SpCacheKey.tokenType, login.getToken_type());
                    SPUtils.put(mContext, SpCacheKey.accessToken, login.getAccess_token());
                    isLogined = true;
                }

                @Override
                public void error(VolleyError error) {
                    showToast("登录失败，请检查网络后重试");
                    Intent toLogin = new Intent(CGBapplication.getInstance().getApplicationContext(), LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                }
            });
        }else{
            Intent toLogin = new Intent(CGBapplication.getInstance().getApplicationContext(), LoginActivity.class);
            startActivity(toLogin);
            finish();
        }
    }


    private boolean checkLoginFormat(String name, String password) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }
}
