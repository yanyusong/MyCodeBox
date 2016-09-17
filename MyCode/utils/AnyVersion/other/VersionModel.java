package net.boyazhidao.cgb.model;

/**
 * Created by mac on 16/3/17.
 */
public class VersionModel {


    /**
     * versionName : 1.0.0
     * versionNote : [{"note":"1.第一处修改"},{"note":"2.第二处修改"},{"note":"3.第三处修改"},{"note":"4.第四处修改"}]
     * downloadURL : 1.0.0
     * versionCode : 1
     */

    public String versionName;
    public String downloadURL;
    public String versionCode;
    /**
     * note : 1.第一处修改
     */
    public String versionNote;





    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionNote() {
        return versionNote;
    }

    public void setVersionNote(String versionNote) {
        this.versionNote = versionNote;
    }

}
