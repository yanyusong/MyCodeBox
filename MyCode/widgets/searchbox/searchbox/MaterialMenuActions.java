package cn.aibianli.sdot.common.widgets.searchbox.searchbox;

/**
 * Created by mac on 16/8/18.
 */
public interface MaterialMenuActions {

    interface OnMaterialMenuClickedListener {

        void materialMenuClicked();
    }

    void openSearcher();

    void closeSearcher();

    void toggleSearcher();

}
