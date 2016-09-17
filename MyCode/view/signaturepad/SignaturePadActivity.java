package net.aibianli.ps.modules.signaturepad;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhy.http.okhttp.callback.StringCallback;

import net.aibianli.ps.R;
import net.aibianli.ps.base.activity.BaseToolBarActivity;
import net.aibianli.ps.bean.ComRespInfo;
import net.aibianli.ps.bean.Order;
import net.aibianli.ps.common.IntentTags;
import net.aibianli.ps.common.URLs;
import net.aibianli.ps.common.helpers.httploadhelper.RequestHeader;
import net.aibianli.ps.common.utils.BitmapUtil;
import net.aibianli.ps.common.utils.okhttputils.OkHttpUtils;
import net.aibianli.ps.common.utils.sputils.SPUtils;
import net.aibianli.ps.common.utils.sputils.SpCacheKey;
import net.aibianli.ps.common.widgets.signaturepad.views.SignaturePad;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

/**
 * Created by mac on 16/7/8.
 */
public class SignaturePadActivity extends BaseToolBarActivity {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private Order shopOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarTitle("确认收货");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shopOrder = (Order) getIntent().getSerializableExtra(IntentTags.order);

        View view = getLayoutInflater().inflate(R.layout.activity_signature_pad, null, false);
        mContentView.addView(view);

        mSignaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
//                Toast.makeText(SignaturePadActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                byte[] bytes = compressBitmap(signatureBitmap);
                upLoadImage(bytes);
            }
        });
    }


    private byte[] compressBitmap(Bitmap signature) {
        //宽高 480 800,大小120kb,格式:jpg
        Bitmap img = BitmapUtil.compressBitmapWidthAndHeight(signature, 480, 800);
        return BitmapUtil.compressBitmapQualityReduce(img, 120);
    }

    private void upLoadImage(byte[] bytes) {
        OkHttpUtils.postByteArray()
                .MediaType(MediaType.parse("multipart/form-data"))
                .addByteArray("UploadPic",bytes)
                .addHeader(RequestHeader.Cookie, SPUtils.get(mContext, SpCacheKey.cookie, "-1").toString())
                .url(URLs.Post_Upload_Image)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        showProgressDialog(false, null);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast("上传失败!");
                        hideProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ComRespInfo<String> result = JSON.parseObject(response, new TypeReference<ComRespInfo<String>>() {
                        });
                        if (result.getResult() == 1) {
                            Log.e("Signature", "照片上传成功!");
                            postSignSuccess(shopOrder.getId() + "", result.getData());//// TODO: 16/7/11
                        } else {
                            showToast(result.getMsg());
                            hideProgressDialog();
                        }
                    }
                });
    }

    private void postSignSuccess(String distributeListId, String imgpath) {
        OkHttpUtils.post()
                .url(URLs.Post_Sign_Success)
                .addHeader(RequestHeader.Cookie, SPUtils.get(mContext, SpCacheKey.cookie, "-1").toString())
                .addParams("distributeListId", distributeListId)
                .addParams("imgpath", imgpath)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast("上传失败!");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ComRespInfo<String> result = JSON.parseObject(response, new TypeReference<ComRespInfo<String>>() {
                        });
                        if (result.getResult() == 1) {
                            showToast("签收成功!");
                            Log.e("Signature", "签收成功!");
                            finish();
                        } else {
                            showToast(result.getMsg());
                        }
                    }
                });


    }

    private static String getCharacterAndNumber() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
        return str;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
