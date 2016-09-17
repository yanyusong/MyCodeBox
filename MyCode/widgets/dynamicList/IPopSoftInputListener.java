package net.xichiheng.yulewa.interfaces;

public interface IPopSoftInputListener {
    /**
     * 弹出底部编辑框
     *
     * @param hint
     * @param iSoftInputListener
     */
    void popUpSoftInput (String hint, IEditInputBtnListener iSoftInputListener);

    /**
     * 隐藏底部编辑框
     */
    void popDownSoftInput ();
}
