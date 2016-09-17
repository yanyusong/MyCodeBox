package net.xichiheng.yulewa.fragment.show;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.activity.ShowerPhotowallActivity;
import net.xichiheng.yulewa.base.BaseFragment;
import net.xichiheng.yulewa.http.API;
import net.xichiheng.yulewa.http.CommonRespInfo;
import net.xichiheng.yulewa.interfaces.IHttpSuccess;
import net.xichiheng.yulewa.model.PageModel;
import net.xichiheng.yulewa.model.PhotosModel;
import net.xichiheng.yulewa.model.SinglePhotoModel;
import net.xichiheng.yulewa.utils.PhotoWallUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ContentView(R.layout.view_photos_wall)
public class ShowerPhotosFragment extends BaseFragment implements IHttpSuccess {

    @ViewInject(R.id.shower_info_photos)
    private GridView photosGrid;
    private List<SinglePhotoModel> allPhotos = new ArrayList<>();//从服务器获取的当前照片墙上的所有图片数据
    private List<HashMap<String, String>> allImgPaths = new ArrayList<HashMap<String, String>>();//整理过后的当前照片墙上的所有图片数据

    private String showerId;
    private PhotoWallUtils photoWallUtils;
    private static Integer PHOTO_MAX_NUM = 50;
    private int itemHeight = 0;//单位是像素px
    private int itemWidth = 0;//单位是像素px
    private Boolean canEdit = false;
    private API.ShowUserPhotoWall post;
    private int page = 0;
    private boolean hasNextPage = false;


    public static ShowerPhotosFragment newInstance(String showerId, Boolean isMe) {
        ShowerPhotosFragment fragment = new ShowerPhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("showerId", showerId);
        bundle.putBoolean("isMe", isMe);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.showerId = (String) getArguments().get("showerId");
            this.canEdit = (Boolean) getArguments().get("isMe");
        }

    }

    public boolean getIsUploadComplete() {
        return !(photoWallUtils.getimgFilePaths() > 0);
    }


    @Override
    public void initView() {
        super.initView();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        itemWidth = displayMetrics.widthPixels / 3;
        itemHeight = itemWidth;
        int photosMaxNum = 0;
        if (canEdit) {
            photosMaxNum = PHOTO_MAX_NUM;
        } else {
            photosMaxNum = allImgPaths.size();
        }
        photoWallUtils = new PhotoWallUtils(ct, photosGrid, allImgPaths, itemHeight, itemWidth, R.layout.view_single_img_square,
                R.mipmap.img_photo_plus, photosMaxNum);
        photoWallUtils.enableOnItemClick(canEdit);
        if (canEdit) {
            photoWallUtils.enableOnItemLongClick(new PhotoWallUtils.DelPhoto() {
                @Override
                public void postDelPhotoRequest(int position, String imgKey) {
                    API.DeletePhoto post = new API.DeletePhoto();
                    post.imgs = imgKey;
                    post.showProgress();
                    post.post(ct, 374, false, new IHttpSuccess() {
                        @Override
                        public void handleResult(int tag, String resultData) {
                            photoWallUtils.doDelPhotoReturned();
                        }
                    });
                }
            });
        }
        photosGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        //滚动到底自动加载更多
                        if ((view.getLastVisiblePosition() == view.getCount() - 1)&&hasNextPage) {
                            loadMore();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        post = new API.ShowUserPhotoWall();
        loadMore();
    }

    private void loadMore() {
        page++;
        post.page = page;
        post.perPage = 21;
        post.userId = showerId;
        post.showProgress();
        post.post(ct, 964, true, this);
    }


    /**
     * 当编辑后点击“完成”的操作，主要是上传编辑好的照片
     */
    public void doCompleted() {
        photoWallUtils.updatePhotos(new PhotoWallUtils.GetQnTokenInterface() {

            @Override
            public void postGetQnTokenRequest() {
                API.QiNiuToken post = new API.QiNiuToken();
                post.showProgress();
                post.post(ct, 263, false, new IHttpSuccess() {
                    @Override
                    public void handleResult(int tag, String resultData) {
                        String qnToken = "";
                        try {
                            qnToken = new JSONObject(resultData).optString("token", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        photoWallUtils.doGetQnTokenReturned(qnToken, new PhotoWallUtils.UpdateCompleteInterface() {

                            @Override
                            public void UpdateCompleteHandler(List<String> imgFilePaths, String[] imageKeys) {
                                // 当所有图片上传完后
                                StringBuffer keysString = new StringBuffer();
                                if (imageKeys.length > 0) {
                                    keysString.append(imageKeys[0]);
                                    for (int i = 1; i < imageKeys.length; i++) {
                                        keysString.append("," + imageKeys[i]);
                                    }
                                }
                                API.UploadPhotosKeys post = new API.UploadPhotosKeys();
                                post.imgs = keysString.toString();
                                post.post(ct, 492, false, new SimpleHandle() {
                                            @Override
                                            public void handleResult(int tag, String resultData) {
                                                PhotosModel imgs = JSON.parseObject(resultData, PhotosModel.class);
                                                photoWallUtils.doUpdateImgkeysReturned(imgs);
                                                ((ShowerPhotowallActivity) getActivity()).setRightText(false);
                                                showToast("照片上传成功");
                                            }

                                            @Override
                                            public void handleFailure(int errorCode, CommonRespInfo respInfo) {
                                                super.handleFailure(errorCode, respInfo);
                                                photoWallUtils.doUpdateImgkeysFailReturned();
                                                showToast("照片上传失败，请重试");
                                            }
                                        }

                                );
                            }
                        });

                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoWallUtils.onActivityResult(requestCode, resultCode, data);
        if (canEdit && photoWallUtils.getimgFilePaths() > 0) {
            ((ShowerPhotowallActivity) getActivity()).setRightText(true);
        }
    }

    @Override
    public void handleResult(int tag, String resultData) {

        PageModel<SinglePhotoModel> pageModel = JSON.parseObject(resultData, new TypeReference<PageModel<SinglePhotoModel>>() {
        }.getType());

        List<SinglePhotoModel> photos = pageModel.getList();

        hasNextPage = page < pageModel.getTotalPage();

        //        List<HashMap<String, String>> imgPaths = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < photos.size(); i++) {
            HashMap<String, String> pathInfo = new HashMap<String, String>();
            pathInfo.put(PhotoWallUtils.KEY_fromTag, "0");
            pathInfo.put(PhotoWallUtils.KEY_path, photos.get(i).getImgUrl());
            pathInfo.put(PhotoWallUtils.KEY_imageKey, photos.get(i).getImgKey());
            allImgPaths.add(pathInfo);
        }
        int photosMaxNum = 0;
        if (canEdit) {
            photosMaxNum = PHOTO_MAX_NUM;
        } else {
            photosMaxNum = allImgPaths.size();
        }
        photoWallUtils.notifyPhotoWallDataSetChanged(allImgPaths, photosMaxNum);
        //        int photosMaxNum = 0;
        //        if (canEdit) {
        //            photosMaxNum = PHOTO_MAX_NUM;
        //        } else {
        //            photosMaxNum = imgPaths.size();
        //        }
        //        photoWallUtils = new PhotoWallUtils(ct, photosGrid, imgPaths, itemHeight, itemWidth, R.layout.view_single_img_square,
        //                R.mipmap.ic_launcher, photosMaxNum);
        //        photoWallUtils.enableOnItemClick(canEdit);

        //                if (canEdit) {
        //                    photoWallUtils.enableOnItemLongClick(new DelPhoto() {
        //
        //                        @Override
        //                        public void postDelPhotoRequest(int position, String imgKey) {
        //                            // TODO Auto-generated method stub
        //                            RequestParams params = new RequestParams();
        //                            params.addBodyParameter("token", application.getUser().getToken());
        //                            params.addBodyParameter("imgKey", imgKey);
        //                            loadData(asyn_shower_info_photos_delete, HttpMethod.POST, ServerPathUtile.delPhoto_Url,
        //                                    params, true, false);
        //                        }
        //                    });
        //                }
    }


    //    @Override
    //    public void userDoInUI(int async_tag, Object result, HttpTask httpTask, CommonRespInfo commonRespInfo) {
    //        // TODO Auto-generated method stub
    //        if (commonHandleHttpError(httpTask, commonRespInfo)) {
    //            switch (async_tag) {
    //                // asyn_shower_info_photos
    //
    //                //asyn_shower_info_photos_delete
    //                case 747:
    //                    photoWallUtils.doDelPhotoReturned();
    //                    break;
    //                //asyn_shower_info_photos_getQnToken
    //
    //                //asyn_shower_info_photos_talkServer
    //                case 745:
    //                    PhotosModle imgs = JSON.parseObject(commonRespInfo.data, PhotosModle.class);
    //                    photoWallUtils.doUpdateImgkeysReturned(imgs);
    //                    break;
    //                default:
    //                    break;
    //            }
    //        }
    //    }


}
