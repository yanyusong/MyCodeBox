#启动页
###显示至少2秒钟，同时进行自动登录，若登录成功了就跳转，没有则开始每隔1秒的轮询，直到登录返回成功了或者登录失败了跳转到登录页面
```
private Handler handler = new Handler();
private volatile boolean isLogined = false;

 Runnable loadingRun = new Runnable() {
        @Override
        public void run() {
            if (isLogined) {
                handler.removeCallbacks(loadingRun);
                Intent toMain = new Intent(mContext, MainActivity.class);
                startActivity(toMain);
                finish();
            } else {
                handler.postDelayed(loadingRun, 1000);
            }
        }
    };
   
       @Override
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        autoLogin();
        handler.postDelayed(loadingRun, 2000);
    } 
    
  private void autoLogin() {
        accountCache = SPUtils.get(mContext, SpCacheKey.accountNum, "").toString();
        passwordCache = SPUtils.get(mContext, SpCacheKey.passWord, "").toString();
        if (checkLoginFormat(accountCache, passwordCache)) {

            RequestInfo requestInfo = new RequestInfo(URLS.LOGIN);
            loadData("WelcomeActivity", requestInfo, false, false, new VolleyResponse.strReqCallback() {
                @Override
                public void success(String response) {
                   
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


```