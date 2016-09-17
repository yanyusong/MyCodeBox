package cn.aibianli.sdot.base.module.network_refresh;


import cn.aibianli.sdot.base.module.base.BaseContract;
import cn.aibianli.sdot.base.module.network.BaseNetContract;
import cn.aibianli.sdot.common.beans.ComRespInfo;

/**
 * Created by mac on 16/6/11.
 */
public class BaseRefreshContract {

    public interface IBaseRefreshView<T extends IBaseRefreshPresenter, DATA> extends BaseNetContract.IBaseNetView<T> {

        void onBindViewData(ComRespInfo<DATA> dataComRespInfo);

        void showRefreshIndication();

        void hideRefreshInfication();

    }

    public interface IBaseRefreshPresenter extends BaseContract.IBasePresenter {

        void onRefreshData();

    }

}
