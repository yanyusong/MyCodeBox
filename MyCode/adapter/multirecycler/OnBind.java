package cn.aibianli.sdot.base.adapter.multirecycler;



import cn.aibianli.sdot.base.adapter.GeneralRecyclerViewHolder;

/**
 * Created by mac on 16/6/18.
 */
public interface OnBind<T> {

    /**
     * @param holder
     * @param position
     */
    void onBindChildViewData(GeneralRecyclerViewHolder holder, Object itemData, int position);
}
