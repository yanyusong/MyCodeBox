package net.boyazhidao.cgb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.boyazhidao.cgb.R;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.List;

/***********************************************
 * @类名 ：抽象方法GeneralListAdapter
 * @描述 ：一个简化操作的ListView适配器
 * @主要参数 ：
 * @主要接口 :
 * @作者 ：yanys
 * @日期 ：2015年7月10日上午10:01:11
 * @版本 ：1.0
 * @备注 ：使用时定义一个自己的adapter类继承GeneralListAdapter
 ***********************************************/
public abstract class GeneralListAdapter<T> extends BaseAdapter {

    public Context ct;
    public List<T> data;
    public ImageOptions imageOptions;
    private int itemLayoutId;

    /**
     * ListView的通用适配器
     *
     * @param ct           上下文
     * @param data         传入的列表数据封装，eg:List<HashMap<K, V>>;
     * @param itemLayoutId 传入的条目Item的布局ID
     */
    public GeneralListAdapter (Context ct, List<T> data, int itemLayoutId) {
        this.ct = ct;
        this.data = data;
        this.itemLayoutId = itemLayoutId;
        this.imageOptions = initImageOptions ();
    }

    public GeneralListAdapter (Context ct, int itemLayoutId) {
        this (ct, new ArrayList<T> (), itemLayoutId);
    }

    public ImageOptions initImageOptions () {
        return new ImageOptions.Builder ()
                //				.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                //				.setRadius(DensityUtil.dip2px(5))
                .setCrop (true)
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType (ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId (R.mipmap.ic_launcher)
                .setFailureDrawableId (R.mipmap.ic_launcher)
                .build ();
    }

    @Override
    public int getCount () {
        if (data == null) {
            return 0;
        }
        return data.size ();
    }

    @Override
    public T getItem (int position) {
        if (data.isEmpty ()) {
            return null;
        }
        return data.get (position);
    }

    @Override
    public long getItemId (int position) {
        if (data.isEmpty ()) {
            return 0;
        }
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder = getViewHolder (position, convertView, parent);
        SetChildViewData (mViewHolder, data.get (position), position, imageOptions);
        return mViewHolder.getView ();
    }

    protected ViewHolder getViewHolder (int position, View convertView, ViewGroup parent) {
        return ViewHolder.Create (ct, convertView, parent, itemLayoutId, position);
    }

    public void update (List<T> data) {
        this.data = data;
        notifyDataSetChanged ();
    }

    /**
     * 将数据分发绑定给Item中的各个子View
     *
     * @param mViewHolder  ViewHolder帮助类
     * @param itemData     该Item的所有数据
     * @param position     该Item的position
     * @param imageOptions xUtils中的图片加载工具的配置
     */
    public abstract void SetChildViewData (ViewHolder mViewHolder, T itemData, int position, ImageOptions imageOptions);

}
