##FragUtils
```
    private static String Tag_DemandProductListFrag = "DemandProductListFrag";
    private static String Tag_SupplyProductListFrag = "SupplyProductListFrag";
    private static String Tag_MyMessageListFrag = "MyMessageListFrag";
    private static String Tag_SettingFrag = "SettingFrag";
    
    private FragUtils fragUtils;
    
    fragUtils = initFragUtils();
    fragUtils.showFragment(Tag_DemandProductListFrag);

    enum fragTagEnums {
        DemandProductListFrag, SupplyProductListFrag, MyMessageListFrag, SettingFrag
    }
    
    private FragUtils initFragUtils() {

        String[] fragTags = new String[]{Tag_DemandProductListFrag, Tag_SupplyProductListFrag, Tag_MyMessageListFrag, Tag_SettingFrag};

        FragUtils utils = new FragUtils(getSupportFragmentManager(), R.id.frameContent, fragTags, new FragUtils.IFragmentInitMethods() {
            @Override
            public Fragment initFrag(String fragTag) {
                Fragment tempFrag = null;
                switch (fragTagEnums.valueOf(fragTag)) {
                    case DemandProductListFrag:
                        
                        tempFrag = mDemandProductListFrag;
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
        
```
#AMapLocationUtils 高德定位工具使用
```
   private void startLocation () {
        AMapLocationUtils aMapLocationUtils = new AMapLocationUtils (this);
        aMapLocationUtils.startLocation (new AMapLocationUtils.IOnLocationListener () {
            @Override
            public void OnLocationSuccess (double longitude, double latitude) {
                KLog.e("定位成功,lng="+longitude+",lat="+latitude);
                UserModel user = new UserModel ();
                user.setLatitude ("" + latitude);
                user.setLongitude ("" + longitude);
                setUser (user);
            }

            @Override
            public void OnLocationFail (int errorCode) {

            }

            @Override
            public void OnFinish () {

            }
        });
    }
```


