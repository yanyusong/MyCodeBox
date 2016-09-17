package net.xichiheng.yulewa.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.YLWapplication;
import net.xichiheng.yulewa.base.BaseTitleActivity;
import net.xichiheng.yulewa.utils.ImageUtils;
import net.xichiheng.yulewa.widget.HackyViewPager;
import net.xichiheng.yulewa.widget.picbrowser.PhotoView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ContentView(R.layout.activity_image_page)
public class ImagePageActivity extends BaseTitleActivity {

    public final static String EXTRA_IMAGE_PATHS = "imagePaths";
    public final static String EXTRA_IMAGE_INDEX = "index";
    public final static String STATE_POSITION = "state_position";

    @ViewInject(R.id.viewPager)
    private HackyViewPager viewPage;

    private List<HashMap<String, String>> imgPaths = new ArrayList<HashMap<String, String>>();
    private int pagerPosition;
    private ImagePagerAdapter adapter;
    private YLWapplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        application = (YLWapplication) getApplication();

        initView();

        getData();

        initTitleBar();
        adapter = new ImagePagerAdapter(imgPaths, this);
        viewPage.setAdapter(adapter);
        viewPage.setCurrentItem(pagerPosition);
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        pagerPosition = bundle.getInt(EXTRA_IMAGE_INDEX, 0);
        imgPaths = (List<HashMap<String, String>>) bundle.getSerializable(EXTRA_IMAGE_PATHS);
    }

    private void initTitleBar() {
        mTitleBar.setLeftTxt("返回");
        mTitleBar.setLeftTxtClickListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, viewPage.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<HashMap<String, String>> images;
        private LayoutInflater inflater;
        @SuppressWarnings("unused")
        private Context mContext;
        private ImageOptions imageOptions;

        public ImagePagerAdapter(List<HashMap<String, String>> images, Context context) {
            this.images = images;
            this.mContext = context;
            inflater = getLayoutInflater();
            imageOptions = new ImageOptions.Builder()
                    //	.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                    //	.setRadius(DensityUtil.dip2px(5))
                    .setCrop(true)
                            // 加载中或错误图片的ScaleType
                            // .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                    .setFailureDrawableId(R.mipmap.ic_launcher)
                    .build();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image,
                    view, false);
            KLog.e("img_path = " + images.get(position).get(ImageUtils.TAG_path));
            PhotoView imageView = (PhotoView) imageLayout
                    .findViewById(R.id.image);
            switch (Integer.valueOf(images.get(position).get(ImageUtils.TAG_fromTag))) {
                //加载网络图片
                case 0:
                    Picasso.with(mContext).load(images.get(position).get(ImageUtils.TAG_path))
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(imageView);
                    break;
                //加载本地图片
                case 1:
                    Picasso.with(mContext).load(new File(images.get(position).get(ImageUtils.TAG_path)))
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(imageView);
                    break;
                default:
                    break;
            }

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }
}
