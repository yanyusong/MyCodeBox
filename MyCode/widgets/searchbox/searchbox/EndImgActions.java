package cn.aibianli.sdot.common.widgets.searchbox.searchbox;

/**
 * Created by mac on 16/8/18.
 */
public interface EndImgActions {

    enum ImgState {
        Clear,
        Search,
        None
    }

    interface OnEndImgClickListener {
        /**
         * Called when the EndImg button is pressed
         */
        void onEndImgClick(ImgState state);
    }

    void switchImgState(ImgState state);

}
