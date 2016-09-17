package net.xichiheng.yulewa.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.socks.library.KLog;

import net.xichiheng.yulewa.R;

import org.xutils.image.ImageOptions;

import java.util.List;

public abstract class PhotoWallAdapter<T> extends BaseAdapter {

    private Context ct;
    private List<T> data;
    private int itemLayoutId;
    private ImageOptions imageOptions;
    private int itemMaxNum;
    private int firstItemDrawableId;
    private boolean isFull = false;//true 不显示加号,false 显示加号

    /**
     * @param ct
     * @param data
     * @param itemLayoutId
     * @param itemMaxNum
     * @param firstItemDrawableId
     */
    public PhotoWallAdapter(Context ct, List<T> data, int itemLayoutId, int itemMaxNum, int firstItemDrawableId) {
        this.ct = ct;
        this.data = data;
        this.itemLayoutId = itemLayoutId;
        this.itemMaxNum = itemMaxNum;
        this.firstItemDrawableId = firstItemDrawableId;
        this.imageOptions = new ImageOptions.Builder().setFailureDrawableId(R.mipmap.ic_launcher)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .build();
        isFull = data.size() >= itemMaxNum;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (isFull) {
            count = itemMaxNum;
        } else {
            count = data.size() + 1;
        }
        KLog.e("调用了PhotoWallAdapter的getCount，count为" + count);
        return count;
    }

    public void notifyPhotoWallDataSetChanged(List<T> mItemDatas, int mItemMaxNum) {
        data = mItemDatas;
        itemMaxNum = mItemMaxNum;
        isFull = data.size() >= itemMaxNum;
        notifyDataSetChanged();
    }

    public boolean getIsFull(){
        return isFull;
    }

    @Override
    public Object getItem(int position) {
        KLog.e("调用了getItem，Item为" + data.get(position));
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder = getViewHolder(position, convertView, parent);
        if (isFull) {
            SetChildViewData(mViewHolder, data.get(position), imageOptions);
        } else {
            if (position == 0) {
                SetFirstItemViewData(mViewHolder, firstItemDrawableId);
                //                KLog.e("调用了getView的SetFirstItemViewData，position为" + position);
            } else {
                SetChildViewData(mViewHolder, data.get(position - 1), imageOptions);
                //                KLog.e("调用了getView的SetChildViewData，position为" + position);
            }
        }
        return mViewHolder.getView();
    }

    /**
     * 设置最后一个item视图的数据
     *
     * @param mViewHolder
     * @param firstItemDrawableId 第一个item的视图id，R.mipmap...
     */
    public abstract void SetFirstItemViewData(ViewHolder mViewHolder, int firstItemDrawableId);

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.Create(ct, convertView, parent, itemLayoutId, position);
    }

    /**
     * 当position符合data的size时，将数据分发绑定给Item中的各个子View
     *
     * @param mViewHolder  ViewHolder帮助类
     * @param itemData     该Item的所有数据
     * @param imageOptions xUtils中的图片加载配置
     */
    public abstract void SetChildViewData(ViewHolder mViewHolder, T itemData, ImageOptions imageOptions);
}
