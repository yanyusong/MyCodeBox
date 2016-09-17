package cn.aibianli.sdot.common.widgets.searchbox.searchbox;

/**
 * Created by mac on 16/8/18.
 */
public interface SelectMenuActions {

    interface OnSelectMenuClickListener {
        /**
         * Called when the menu button is pressed
         */
        void onSelectMenuClick();
    }

    interface ISelectMenuView{

        void showSelectView();

        void hideSelectView();

        boolean isShowing();

    }

    void setSelectMenuText(String text);

    String getSelectMenuText();

}
