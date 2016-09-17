package net.xichiheng.yulewa.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.wq.photo.widget.PickConfig;
import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.adapter.PhotoWallAdapter;
import net.xichiheng.yulewa.adapter.ViewHolder;
import net.xichiheng.yulewa.http.API;
import net.xichiheng.yulewa.interfaces.IHttpSuccess;
import net.xichiheng.yulewa.model.PhotosModel;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhotoWallUtils {

    //public static int ASNY_delphoto = 7474;//删除照片
    /**
     * fromTag: "0":网络图片 "1":本地图片；
     */
    public static String KEY_fromTag = "fromTag";
    /**
     * path:网络图片的url/本地图片的绝对地址
     */
    public static String KEY_path = "path";
    /**
     * imageKey:七牛上网络图片的imageKey,本地图片则为－1；
     */
    public static String KEY_imageKey = "imageKey";

    public static int TAG_TAKE_PHOTO = 958;
    public static int TAG_PICK_PHOTO = 395;

    private GridView gridView;
    private int imgWidth;// 图片宽
    private int imgHeight;// 图片高
    private GridImgsAdapter adapter;
    private Integer PHOTO_MAX_NUM;
    private ImageUploadUtil imgUpdate;
    private Context mContext;
    /**
     * 维护一个List<HashMap<String, String>>来保存当前照片墙上所有照片的信息，包括：
     * path:网络图片的url/本地图片的绝对地址； fromTag: "0":网络图片 "1":本地图片；
     * imageKey:七牛上网络图片的imageKey,本地图片则为－1；
     */
    private List<HashMap<String, String>> imgPaths = new ArrayList<HashMap<String, String>> ();
    /**
     * 维护一个list<string> 用来保存本次选好的本地图片的路径，包括增加，删除等
     */
    private List<String> imgFilePaths = new ArrayList<String> ();
    /**
     * 保存的本地未上传图片的imageKeys
     */
    private String[] imageKeys;

    private int curDeletingPos = 0;// 当前正在删除的照片位置
    private boolean isCompleted = false;// 图片是否已全部上传到七牛服务器
    /**
     * 初始化界面时网络照片的数量
     */
    private int PHOTOS_NET_NUM = 0;

    private int height = 0;
    private int width = 0;
    private DelPhoto iDelPhoto;
    private GetQnTokenInterface iGetQnToken;
    private UpdateCompleteInterface iUpdateComplete;

    private ProgressDialog dialog;//图片上传进度的dialog

    /**
     * 初始化照片墙的初始数据和配置
     *
     * @param ct
     * @param mGridView
     * @param mImgPaths 维护一个List<HashMap<String, String>>来保存当前照片墙上所有照片的信息，包括：
     *                  path:网络图片的url/本地图片的绝对地址； fromTag: "0":网络图片 "1":本地图片；
     *                  imageKey:七牛上网络图片的imageKey,本地图片则为－1；
     *                  //     * @param itemHeight item的高，单位都是像素px
     *                  //     * @param itemWidth  item的宽，单位都是像素px
     */
    public PhotoWallUtils (Context ct, GridView mGridView, List<HashMap<String, String>> mImgPaths, int itemHeight, int itemWidth, int itemLayoutId, int firstItemResId,
                           Integer photo_max_num) {
        this.mContext = ct;
        this.gridView = mGridView;
        this.imgPaths = mImgPaths;
        this.height = itemHeight;
        this.width = itemWidth;
        this.PHOTO_MAX_NUM = photo_max_num;
        this.PHOTOS_NET_NUM = mImgPaths.size ();
        adapter = new GridImgsAdapter (mContext, imgPaths, itemLayoutId, PHOTO_MAX_NUM,
                firstItemResId);
        gridView.setAdapter (adapter);

    }

    public List<HashMap<String, String>> getAllItemData () {
        return imgPaths;
    }

    public void notifyPhotoWallDataSetChanged (List<HashMap<String, String>> allImgPaths, int photosMaxNum) {
        imgPaths = allImgPaths;
        this.PHOTO_MAX_NUM = photosMaxNum;
        this.PHOTOS_NET_NUM = allImgPaths.size ();
        adapter.notifyPhotoWallDataSetChanged (imgPaths, PHOTO_MAX_NUM);
    }

    /**
     * 使能图片的点击事件，点击看大图，点加号可添加，不是自己则不做反应
     *
     * @param canAdd 若为true则必须在此操作所在的activity／fragment的OnActivityResult中调用PhotoWallUtils.OnActivityResult.
     *               是否可编辑，点击加号是否可添加照片
     *               false:点击图片看大图，点加号不做反应
     */
    public void enableOnItemClick (final Boolean canAdd) {
        gridView.setOnItemClickListener (new OnItemClickListener () {

            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                // 点击的是加号
                if ((position == 0) && canAdd && !adapter.getIsFull ()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder (mContext);
                    dialog.setTitle ("选择照片");
                    dialog.setItems (new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            switch (which) {
                                case 0://拍照
                                    PhotoUtils.doTakePhoto ((Activity) mContext, TAG_TAKE_PHOTO);
                                    break;
                                case 1://相册
                                    //                                    PhotoUtils.doPickPhotoFromGallery ((Activity) mContext, TAG_PICK_PHOTO);
                                    new PickConfig.Builder ((Activity) mContext)
                                            .maxPickSize (PHOTO_MAX_NUM - imgPaths.size ())
                                            .isneedcamera (false)
                                            .spanCount (4)
                                            .isneedcrop (true)
                                            .isneedactionbar (true)
                                            .pickMode (PickConfig.MODE_MULTIP_PICK)
                                            .build ();
                                    break;
                            }
                        }
                    });
                    dialog.create ().show ();
                } else {
                    // 点击的是图片，看本地大图
                    // ToastShowMessage.showShortToast(mContext, "点击看大图");
                    List<HashMap<String, String>> paths = new ArrayList<HashMap<String, String>> ();
                    for (int i = 0; i < imgPaths.size (); i++) {
                        String path = "";
                        switch (Integer.valueOf (imgPaths.get (i).get (KEY_fromTag))) {
                            case 0:
                                path = ImageUtils.getPrimaryImageUrl (imgPaths.get (i).get (KEY_path));
                                break;

                            case 1:
                                path = imgPaths.get (i).get (KEY_path);
                                break;

                            default:
                                break;
                        }
                        HashMap<String, String> pathInfo = new HashMap<String, String> ();
                        pathInfo.put (ImageUtils.TAG_path, path);
                        pathInfo.put (ImageUtils.TAG_fromTag, imgPaths.get (i).get (KEY_fromTag));
                        paths.add (pathInfo);
                    }
                    int imgPos = ((canAdd && !adapter.getIsFull ()) ? position - 1 : position);
                    ImageUtils.imageBrower (mContext, imgPos, paths);
                }
            }
        });
    }

    /**
     * 在添加照片操作所在的activity／fragment的OnActivityResult中调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //TAG_TAKE_PHOTO
                case 958:
                    String imgPath = PhotoUtils.getPhotoPathBy (mContext, data);
                    HashMap<String, String> img = new HashMap<String, String> ();
                    img.put (KEY_path, imgPath);
                    img.put (KEY_fromTag, "1");
                    img.put (KEY_imageKey, "-1");
                    imgFilePaths.add (imgPath);
                    imgPaths.add (0, img);
                    break;
                //TAG_PICK_PHOTO
                case PickConfig.PICK_REQUEST_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra ("data");
                    imgFilePaths.addAll (list);
                    for (int i = 0; i < list.size (); i++) {
                        HashMap<String, String> img1 = new HashMap<String, String> ();
                        img1.put (KEY_path, list.get (i));
                        img1.put (KEY_fromTag, "1");
                        img1.put (KEY_imageKey, "-1");
                        imgPaths.add (0, img1);
                    }
                    break;

                default:
                    break;
            }
            if (imgPaths.size () < PHOTO_MAX_NUM) {
                adapter.notifyPhotoWallDataSetChanged (imgPaths, imgPaths.size () + 1);
            } else {
                adapter.notifyPhotoWallDataSetChanged (imgPaths, imgPaths.size ());
            }
        }
    }

    /**
     * 开启照片墙的长按删除和设为头像功能
     */
    public void enableOnItemLongClick (DelPhoto mDelPhoto) {

        this.iDelPhoto = mDelPhoto;
        gridView.setOnItemLongClickListener (new OnItemLongClickListener () {

            @Override
            public boolean onItemLongClick (AdapterView<?> parent, View view, final int position, long id) {
                // 长按加号不做反应
                if ((position == 0) && !adapter.getIsFull ()) {

                } else {
                    final int imgPos = ((!adapter.getIsFull ()) ? position - 1 : position);
                    new AlertDialog.Builder (mContext)
                            .setItems (new String[]{"设为自己头像", "删除该照片"}, new DialogInterface.OnClickListener () {
                                @Override
                                public void onClick (DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0://设为头像
                                            switch (Integer.valueOf (imgPaths.get (imgPos).get (KEY_fromTag))) {
                                                // 从服务器获取的网络图片
                                                case 0:
                                                    // 点击在了网络图片上
                                                    API.SetShowerHeard post = new API.SetShowerHeard ();
                                                    post.imgs = imgPaths.get (imgPos).get (KEY_imageKey);
                                                    post.showProgress ();
                                                    post.post (mContext, 492, false, new IHttpSuccess () {
                                                        @Override
                                                        public void handleResult (int tag, String resultData) {

                                                        }
                                                    });
                                                    break;
                                                // 还在编辑尚未上传的本地图片
                                                case 1:
                                                    Toast.makeText (mContext, "请将图片上传后再设为头像", Toast.LENGTH_SHORT).show ();
                                                    break;
                                                default:
                                                    break;
                                            }

                                            break;
                                        case 1://删除该照片
                                            switch (Integer.valueOf (imgPaths.get (imgPos).get (KEY_fromTag))) {
                                                // 从服务器获取的网络图片
                                                case 0:
                                                    // 点击在了网络图片上
                                                    curDeletingPos = imgPos;
                                                    iDelPhoto.postDelPhotoRequest (imgPos, imgPaths.get (imgPos).get (KEY_imageKey));

                                                    break;
                                                // 还在编辑尚未上传的本地图片
                                                case 1:
                                                    imgFilePaths.remove (imgFilePaths.size () - imgPos - 1);
                                                    imgPaths.remove (imgPos);
                                                    adapter.notifyPhotoWallDataSetChanged (imgPaths, PHOTO_MAX_NUM);
                                                    break;
                                                default:
                                                    break;
                                            }

                                            break;
                                    }
                                }
                            })
                            .show ();

                }

                return true;

            }
        });

    }
    //删除照片end***************************************************

    /**
     * 开启照片墙的长按删除本地照片的功能
     */
    public void enableOnItemLongClick () {

        this.iDelPhoto = null;
        gridView.setOnItemLongClickListener (new OnItemLongClickListener () {

            @Override
            public boolean onItemLongClick (AdapterView<?> parent, View view, final int position, long id) {
                //// TODO: 16/1/4 自己编辑相册时，满了以后0位置会出错
                // 长按加号不做反应
                if ((position == 0) && !adapter.getIsFull ()) {

                } else {
                    final int imgPos = ((!adapter.getIsFull ()) ? position - 1 : position);
                    new AlertDialog.Builder (mContext)
                            .setTitle ("提示")
                            .setMessage ("是否删除该张照片？")
                            .setNegativeButton ("取消", new DialogInterface.OnClickListener () {
                                @Override
                                public void onClick (DialogInterface dialog, int which) {
                                    dialog.dismiss ();
                                }
                            })
                            .setPositiveButton ("确定", new DialogInterface.OnClickListener () {
                                @Override
                                public void onClick (DialogInterface dialog, int which) {
                                    switch (Integer.valueOf (imgPaths.get (imgPos).get (KEY_fromTag))) {
                                        // 从服务器获取的网络图片
                                        case 0:
                                            // 点击在了网络图片上
                                            if (iDelPhoto != null) {
                                                curDeletingPos = imgPos;
                                                iDelPhoto.postDelPhotoRequest (imgPos, imgPaths.get (imgPos).get (KEY_imageKey));
                                            }

                                            break;
                                        // 还在编辑尚未上传的本地图片
                                        case 1:
                                            imgFilePaths.remove (imgFilePaths.size () - imgPos - 1);
                                            imgPaths.remove (imgPos);
                                            adapter.notifyPhotoWallDataSetChanged (imgPaths, PHOTO_MAX_NUM);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show ();
                }
                return true;

            }
        });

    }
    //删除照片end***************************************************

    /**
     * 删除照片的网络请求返回后调用
     * 与postDelPhotoRequest配对使用
     */
    public void doDelPhotoReturned () {
        imgPaths.remove (curDeletingPos);
        adapter.notifyPhotoWallDataSetChanged (imgPaths, PHOTO_MAX_NUM);
    }

    /**
     * 开始上传图片的操作
     *
     * @param mGetQnToken
     */
    public void updatePhotos (GetQnTokenInterface mGetQnToken) {
        this.iGetQnToken = mGetQnToken;
        if (imgFilePaths.size () > 0) {
            iGetQnToken.postGetQnTokenRequest ();
        }
    }

    /**
     * 尚未上传的照片
     *
     * @return
     */
    public int getimgFilePaths () {
        return imgFilePaths.size ();
    }

    /**
     * 获取到七牛token后调用,与postGetQnTokenRequest配对使用
     * 根据七牛token开始上传照片
     *
     * @param qnToken
     */
    public void doGetQnTokenReturned (String qnToken, UpdateCompleteInterface mUpdateComplete) {
        if (qnToken == null || qnToken.isEmpty ()) {
            KLog.e ("获取到的七牛Token为空");
        } else {
            this.iUpdateComplete = mUpdateComplete;
            if (imgFilePaths.size () > 0) {
                imageKeys = new String[imgFilePaths.size ()];
                Log.e ("imgkey", "初始化imageKeys的size为" + imgFilePaths.size ());
                if (dialog == null) {
                    dialog = initProgressDialog ();
                }
                dialog.setMax (imgFilePaths.size ());
                dialog.setProgress (0);
                dialog.show ();
                for (int i = 0; i < imgFilePaths.size (); i++) {
                    String path = imgFilePaths.get (i);
                    if (new File (path).exists ()) {
                        byte[] data = ImageUtils.getimage (path, 480, 800);//480,800
                        update (data, qnToken, iUpdateComplete, dialog);
                    }
                }
            }
        }
    }

    private ProgressDialog initProgressDialog () {
        ProgressDialog dialog = new ProgressDialog (mContext);
        dialog.setProgressStyle (ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable (false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside (false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle ("上传进度");
        return dialog;
    }

    /**
     * 上传图片
     *
     * @param data  byte[]
     * @param token QnToken
     */
    private void update (byte[] data, String token, final UpdateCompleteInterface mUpdateComplete, final ProgressDialog dialog) {
        KLog.e ("byte[] data " + data);
        if (imgUpdate == null) {
            imgUpdate = new ImageUploadUtil ();
        }
        imgUpdate.upLoadImage (data, token, new UpCompletionHandler () {

            @Override
            public void complete (String key, ResponseInfo info, JSONObject response) {
                if (info.isOK ()) {
                    Log.e ("rise", "上传完以后key=" + key.toString () + ",info=" + info.toString () + ",response="
                            + response.toString ());
                    List<String> keys = imgUpdate.getUploadKeys ();
                    if (keys.contains (key)) {
                        imageKeys[keys.indexOf (key)] = response.optString ("key", "");
                    }
                    /**
                     * 已上传完的图片数量
                     */
                    int compeletedNum = 0;
                    // imageKeys[order] = response.optString("key", "");
                    for (String imgkey : imageKeys) {
                        if (!(imgkey == null || imgkey.isEmpty ())) {
                            // 遍历是否key集合中还存在null或者""值，如果存在则还未上传完
                            compeletedNum = compeletedNum + 1;
                            isCompleted = compeletedNum == imgFilePaths.size ();
                        }
                    }
                    if (isCompleted) {
                        // 当最后一张图片上传完后
                        KLog.e ("qiniu", "所有图片上传七牛成功");
                        mUpdateComplete.UpdateCompleteHandler (imgFilePaths, imageKeys);

                    }
                } else {
                    KLog.e ("qiniu", "一张图片上传失败，取消上传");
                    imgUpdate.cancelUpload ();
                    imgUpdate.clearUploadKeys ();
                    if (dialog != null && dialog.isShowing ()) {
                        dialog.dismiss ();
                    }
                    Toast.makeText (mContext, "图片上传失败，请检查网络后重试", Toast.LENGTH_SHORT).show ();
                }

            }
        }, dialog);
    }


    /**
     * 将imageKeys告诉服务器后的回调，与UpdateCompleteHandler配对使用
     * 清除并重置imgFilePaths的数据，根据返回的数据将刚才上传过的假图片数据伪造成真，主要以下三点：
     * 更改fromTag 为0，即网络来源
     * 更改path为返回的imageUrl
     * 更改imageKey为返回的imagekey
     */
    public void doUpdateImgkeysReturned (PhotosModel imgs) {
        imgFilePaths.clear ();
        imgUpdate.clearUploadKeys ();
        for (int i = 0; i < imgs.getList ().size (); i++) {
            imgPaths.get (imgs.getList ().size () - 1 - i).put (KEY_fromTag, "0");
            imgPaths.get (imgs.getList ().size () - 1 - i).put (KEY_path, imgs.getList ().get (i).getImgUrl ());
            imgPaths.get (imgs.getList ().size () - 1 - i).put (KEY_imageKey, imgs.getList ().get (i).getImgKey ());
        }
        PHOTOS_NET_NUM = PHOTOS_NET_NUM + imgs.getList ().size ();
        if (dialog != null && dialog.isShowing ()) {
            dialog.dismiss ();
        }
    }

    /**
     * imgkey上传服务器失败调用
     */
    public void doUpdateImgkeysFailReturned () {
        imgUpdate.clearUploadKeys ();
        if (dialog != null && dialog.isShowing ()) {
            dialog.dismiss ();
        }
    }

    //删除照片start***************************************************
    public interface DelPhoto {
        /**
         * 发送删除照片的网络请求，在请求返回后需调用DoDelPhotoReturned处理删除后的界面刷新
         *
         * @param position 要删除的照片在imgPaths中的位置(int)
         * @param imgKey   要删除的照片在七牛中的imgKey(String)
         */
        void postDelPhotoRequest (int position, String imgKey);
    }

    //上传照片start***************************************************
    public interface GetQnTokenInterface {
        /**
         * 发送从服务器获取七牛token的请求，获取到后调用doGetQnTokenReturned
         */
        void postGetQnTokenRequest ();

    }

    public interface UpdateCompleteInterface {
        /**
         * 当最后一张照片上传完成后执行，在此方法里将获取到的imageKeys处理后集中上传给服务器，
         * 上传完成后调用doUpdateImgkeysReturned
         *
         * @param imgFilePaths 按add顺序排列的将要上传的本地图片的路径集合
         * @param imageKeys    按imgFilePaths顺序对应的imgKey的字符串数组
         */
        void UpdateCompleteHandler (List<String> imgFilePaths, String[] imageKeys);

    }

    //上传照片end***************************************************

    class GridImgsAdapter extends PhotoWallAdapter<HashMap<String, String>> {

        private Context context;

        public GridImgsAdapter (Context ct, List<HashMap<String, String>> data, int itemLayoutId, int itemTotalNum, int firstItemDrawableId) {
            super (ct, data, itemLayoutId, itemTotalNum, firstItemDrawableId);
            context = ct;
        }

        @Override
        public void SetFirstItemViewData (ViewHolder mViewHolder, int firstItemDrawableId) {
            ImageView img = mViewHolder.getChildView (R.id.view_single_img_square);
            Picasso.with (context).load (firstItemDrawableId).resize (width, height).centerCrop ().placeholder (R.mipmap.img_userheard_nodata)
                    .error (R.mipmap.img_userheard_nodata).into (img);
        }

        @Override
        public void SetChildViewData (ViewHolder mViewHolder, HashMap<String, String> itemData, ImageOptions imageOptions) {
            ImageView img = mViewHolder.getChildView (R.id.view_single_img_square);
            switch (Integer.valueOf (itemData.get (KEY_fromTag))) {
                case 0:
                    Picasso.with (mContext).load (itemData.get (KEY_path)).resize (width, height).centerCrop ().placeholder (R.mipmap.img_userheard_nodata)
                            .error (R.mipmap.img_userheard_nodata).into (img);
                    break;

                case 1:
                    Picasso.with (mContext).load (new File (itemData.get (KEY_path))).resize (width, height).centerCrop ().placeholder (R.mipmap.img_userheard_nodata)
                            .error (R.mipmap.img_userheard_nodata).into (img);
                    break;

                default:
                    break;
            }
        }
    }


}
