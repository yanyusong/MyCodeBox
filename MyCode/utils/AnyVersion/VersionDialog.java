package cn.aibianli.stockmanager.common.util.AnyVersion;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.text.Html;
import android.view.WindowManager;


/**
 * Created by Yoojia.Chen
 * yoojia.chen@gmail.com
 * 2015-01-04
 */
class VersionDialog {

    private final AlertDialog dialog;

    public VersionDialog(final Application context, final Version version, final Downloads downloads) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(version.name)
                .setMessage(Html.fromHtml(version.note))
                .setCancelable(false)
                .setNegativeButton("稍后", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloads.submit(context, version);
                        dialog.dismiss();
                    }
                });
        this.dialog = builder.create();

    }

    public void show() {
        if (dialog != null) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }
    }
}