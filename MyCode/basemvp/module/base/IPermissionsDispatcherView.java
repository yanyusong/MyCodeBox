package cn.aibianli.sdot.base.module.base;

import permissions.dispatcher.PermissionRequest;

/**
 * Created by mac on 16/9/5.
 */
public interface IPermissionsDispatcherView {

    void showRationaleDialog(String message, PermissionRequest request);

}
