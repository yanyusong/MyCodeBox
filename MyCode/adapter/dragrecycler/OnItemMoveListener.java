package net.boyazhidao.cgb.adapter.dragrecycler;

/**
 * Item移动后 触发
 * Created by YoKeyword on 15/12/28.
 */
public interface OnItemMoveListener {
    void onItemMove(int viewType, int fromPosition, int toPosition);
}
