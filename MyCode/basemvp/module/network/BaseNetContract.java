package cn.aibianli.sdot.base.module.network;


import cn.aibianli.sdot.base.module.base.BaseContract;

/**
 * Created by mac on 16/6/11.
 */
public class BaseNetContract {

    public interface IBaseNetView<T extends BaseContract.IBasePresenter> extends BaseContract.IBaseView<T>, INetView {


    }

    public interface INetView {

        interface ILoadingCancelListener {
            void onLoadCancelListener();
        }

        void showLoading(boolean cancelable, ILoadingCancelListener listener);

        void hideLoading();

        void showLoadingError();

        void showEmptyPage();

        void showNoNetWork();

        void showToLoginDialog();

    }

}
