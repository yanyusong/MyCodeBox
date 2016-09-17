package net.xichiheng.yulewa.fragment.show;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.adapter.GeneralListAdapter;
import net.xichiheng.yulewa.adapter.GeneralRecyclerViewHolder;
import net.xichiheng.yulewa.adapter.ViewHolder;
import net.xichiheng.yulewa.base.BaseRecyclerViewFragment;
import net.xichiheng.yulewa.http.API;
import net.xichiheng.yulewa.interfaces.IEditInputBtnListener;
import net.xichiheng.yulewa.interfaces.IHttpSuccess;
import net.xichiheng.yulewa.interfaces.IPopSoftInputListener;
import net.xichiheng.yulewa.model.DynamicItem;
import net.xichiheng.yulewa.model.DynamicReply;
import net.xichiheng.yulewa.model.PageModel;
import net.xichiheng.yulewa.utils.ImageUtils;
import net.xichiheng.yulewa.widget.MyListView;
import net.xichiheng.yulewa.widget.TvClickSpan;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DynamicListFragment extends BaseRecyclerViewFragment<DynamicItem> {

    private static int ASNY_REPLY_OR_COMMENT_DYNAMIC = 751;// 评论／回复动态

    private Boolean isMe = true; //是否是自己

    /**
     * 将获取到的数据传递备份过来，方便删除，更新等操作
     */
    private volatile List<DynamicItem> allDynamicItems = new ArrayList<DynamicItem>();

    private static String Tag_itemPosition = "itemPosition";//当前回复／评论应该在的动态列表中的位置
    private static String Tag_replyPosition = "replyPosition";//当前回复／评论应该在的评论列表中的位置
    /**
     * 正在上传的评论或回复的队列，保存了每一条上传的reply的信息
     * itemPosition
     * replyPosition
     */
    private List<HashMap<String, Integer>> upLoadingRepliesInfo = new ArrayList<HashMap<String, Integer>>();
    /**
     * 正在删除的评论或回复的队列，保存了每一条正在删除的reply的信息
     * itemPosition
     * replyPosition
     */
    private List<HashMap<String, Integer>> deletingRepliesInfo = new ArrayList<HashMap<String, Integer>>();


    @Override
    public List<DynamicItem> handleData(PageModel<String> mPageModel) {
        List<DynamicItem> dynamics = JSON.parseArray(mPageModel.getList().toString(),
                DynamicItem.class);
        allDynamicItems = dynamics;
        return dynamics;
    }


    @Override
    public void OnItemClicked(DynamicItem itemData, int position) {

    }

    @Override
    public void bindChildViewsData(GeneralRecyclerViewHolder mViewHolder, final DynamicItem itemData, final int position, ImageOptions imageOptions) {

        final int curItemPosition = position;
        LinearLayout layTimeToday = mViewHolder.getChildView(R.id.dynamic_lay_time_today);
        RelativeLayout layTimeDate = mViewHolder.getChildView(R.id.dynamic_lay_time_date);
        TextView tvTimeToday = mViewHolder.getChildView(R.id.dynamic_time_today);
        TextView tvTimeMinutes = mViewHolder.getChildView(R.id.dynamic_time_minutes);
        TextView tvTimeDate = mViewHolder.getChildView(R.id.dynamic_time_date);
        TextView tvTimeMonth = mViewHolder.getChildView(R.id.dynamic_time_month);
        TextView content = mViewHolder.getChildView(R.id.dynamic_content);
        GridView photosWall = mViewHolder.getChildView(R.id.dynamic_imgs);
        final TextView praise = mViewHolder.getChildView(R.id.dynamic_praise);
        TextView comment = mViewHolder.getChildView(R.id.dynamic_comment);
        ImageView delete = mViewHolder.getChildView(R.id.dynamic_delete);
        final MyListView commentList = mViewHolder
                .getChildView(R.id.dynamic_comment_list);

        if ((boolean) extra.get("isMe")) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        if (itemData.getFriendDate().isEmpty()) {
            layTimeDate.setVisibility(View.VISIBLE);
            layTimeToday.setVisibility(View.GONE);
            String date = itemData.getRuleDate().substring(0, 2);
            String month = itemData.getRuleDate().substring(2);
            tvTimeDate.setText(date);
            tvTimeMonth.setText(month);
        } else {
            layTimeDate.setVisibility(View.GONE);
            layTimeToday.setVisibility(View.VISIBLE);
            tvTimeToday.setText(itemData.getRuleDate());
            tvTimeMinutes.setText(itemData.getFriendDate());
        }
        content.setText(itemData.getContent());
        if (itemData.getImgs().size() == 0) {
            photosWall.setVisibility(View.GONE);
        } else {
            photosWall.setVisibility(View.VISIBLE);
            List<String> imgs = itemData.getImgs();
            int imgWidth = (ct.getResources().getDisplayMetrics().widthPixels - 140) / 3;
            DynamicImgsAdapter imgsAdapter = new DynamicImgsAdapter(ct, imgs,
                    R.layout.view_single_img_square, imgWidth, imgWidth);
            photosWall.setAdapter(imgsAdapter);
            photosWall.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // 点击图片查看大图
                    List<HashMap<String, String>> paths = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < itemData.getImgs().size(); i++) {
                        if (!itemData.getImgs().get(i).isEmpty()) {
                            String path = ImageUtils.getPrimaryImageUrl(itemData.getImgs().get(i));
                            HashMap<String, String> pathInfo = new HashMap<String, String>();
                            pathInfo.put(ImageUtils.TAG_path, path);
                            pathInfo.put(ImageUtils.TAG_fromTag, "0");
                            paths.add(pathInfo);
                        }
                    }
                    ImageUtils.imageBrower(ct, position, paths);
                }
            });
        }
        praise.setText(itemData.getDynamicPraiseCount());
        if (itemData.getIsPraise().equals("1")) {
            Drawable praiseImg = null;
            praiseImg = ct.getResources().getDrawable(R.mipmap.icon_dynamic_praise_checked);
            praise.setCompoundDrawablesWithIntrinsicBounds(praiseImg, null, null, null);
        } else {
            Drawable praiseImg = null;
            praiseImg = ct.getResources().getDrawable(R.mipmap.img_swdetail_dynamic_praise);
            praise.setCompoundDrawablesWithIntrinsicBounds(praiseImg, null, null, null);
        }

        comment.setText(itemData.getReplyCount() + "");
        if (itemData.getReplyCount() > 0) {
            commentList.setVisibility(View.VISIBLE);
            final List<DynamicReply> replies = itemData.getReplyData();
            commentList.setAdapter(new DynamicReplyAdapter(ct, replies,
                    R.layout.item_dynamicreply_list));
            commentList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    final int clickedReplyPos = position;

                    KLog.e("当前点击的是动态item" + curItemPosition + "的repley" + clickedReplyPos);
                    if (replies.get(clickedReplyPos).getId().equals("-1")) {
                        return;
                    }
                    // 点击评论或回复
                    if ((replies.get(clickedReplyPos).getReplyUserId().equals(application.getUser().getUserId() + ""))
                            || (replies.get(clickedReplyPos).getReplyUserId().isEmpty() && replies.get(clickedReplyPos).getRepliedUserId().equals(application.getUser().getUserId() + ""))) {
                        //点击评论者自己的评论弹出删除选项
                        new AlertDialog.Builder(ct)
                                .setTitle("提示")
                                .setMessage("您确定要删除这条评论？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        API.DeleteShowDynamicReply post = new API.DeleteShowDynamicReply();
                                        post.id = Integer.valueOf(replies.get(clickedReplyPos).getId());
                                        post.post(getActivity(), 335, false, new IHttpSuccess() {
                                            @Override
                                            public void handleResult(int tag, String resultData) {
                                                showToast("评论删除成功");
                                            }
                                        });
                                        allDynamicItems.get(curItemPosition).getReplyData().remove(clickedReplyPos);
                                        int replyCount = allDynamicItems.get(curItemPosition).getReplyCount() - 1;
                                        allDynamicItems.get(curItemPosition).setReplyCount(replyCount);
                                        updateData(curItemPosition);
                                        dialog.dismiss();
                                    }
                                }).show();

                    } else {

                        ((IPopSoftInputListener) getActivity()).popUpSoftInput("回复"
                                        + replies.get(clickedReplyPos).getReplyUser() + ":",
                                new IEditInputBtnListener() {

                                    @Override
                                    public void BtnSendOnClick(String text) {
                                        API.ShowSaveReply post = new API.ShowSaveReply();
                                        post.tocken = application.getUser().getToken();
                                        post.dynamicId = Integer.valueOf(itemData.getId());
                                        post.replyId = Integer.valueOf(replies.get(clickedReplyPos).getId());
                                        post.content = text;
                                        post.post(getActivity(), ASNY_REPLY_OR_COMMENT_DYNAMIC, false, new IHttpSuccess() {
                                            @Override
                                            public void handleResult(int tag, String resultData) {
                                                //添加回复
                                                handleRepliesTaskQueue(tag, resultData);
                                            }
                                        });

                                        DynamicReply replyItem = new DynamicReply("-1", application.getUser().getUserId() + "", application.getUser().getNickname(),
                                                replies.get(clickedReplyPos).getReplyUserId(), replies.get(clickedReplyPos).getReplyUser(), text, "0");
                                        int curReplyCount = allDynamicItems.get(curItemPosition)
                                                .getReplyCount() + 1;
                                        allDynamicItems.get(curItemPosition).setReplyCount(
                                                curReplyCount);
                                        allDynamicItems.get(curItemPosition).getReplyData()
                                                .add(replyItem);

                                        HashMap<String, Integer> replyItemPosInfo = new HashMap<String, Integer>();
                                        replyItemPosInfo.put(Tag_itemPosition, curItemPosition);
                                        replyItemPosInfo.put(Tag_replyPosition, curReplyCount - 1);
                                        //维护的上传队列
                                        upLoadingRepliesInfo.add(replyItemPosInfo);
                                        updateData(curItemPosition);

                                        showToast("回复成功");
                                    }
                                });
                    }
                }
            });
        } else {
            commentList.setVisibility(View.GONE);
        }

        praise.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点赞
                if (itemData.getIsPraise().equals("0")) {
                    KLog.e("当前要赞的是item" + curItemPosition);

                    API.PraiseDynamic post = new API.PraiseDynamic();
                    post.tocken = application.getUser().getToken();
                    post.dynamicId = Integer.valueOf(itemData.getId());
                    post.type = 1;
                    post.post(getActivity(), 998, false, null);

                    Drawable praiseImg = null;
                    praiseImg = ct.getResources().getDrawable(R.mipmap.icon_dynamic_praise_checked);
                    praise.setCompoundDrawablesWithIntrinsicBounds(praiseImg, null, null, null);

                    int count = Integer.valueOf(allDynamicItems.get(curItemPosition).getDynamicPraiseCount());
                    allDynamicItems.get(curItemPosition).setDynamicPraiseCount(String.valueOf(count + 1));
                    allDynamicItems.get(curItemPosition).setIsPraise("1");
                    updateData(curItemPosition);
                    showToast("赞成功");
                } else {
                    //取消赞
                    KLog.e("当前要取消赞的是item" + curItemPosition);

                    API.PraiseDynamic post = new API.PraiseDynamic();
                    post.tocken = application.getUser().getToken();
                    post.dynamicId = Integer.valueOf(itemData.getId());
                    post.type = 0;
                    post.post(getActivity(), 887, false, null);

                    Drawable praiseImg = null;
                    praiseImg = ct.getResources().getDrawable(R.mipmap.img_swdetail_dynamic_praise);
                    praise.setCompoundDrawablesWithIntrinsicBounds(praiseImg, null, null, null);

                    int count = Integer.valueOf(allDynamicItems.get(curItemPosition).getDynamicPraiseCount());
                    allDynamicItems.get(curItemPosition).setDynamicPraiseCount(String.valueOf(count - 1));
                    allDynamicItems.get(curItemPosition).setIsPraise("0");
                    updateData(curItemPosition);
                    showToast("取消赞成功");
                }

            }
        });

        comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 评论
                ((IPopSoftInputListener) getActivity()).popUpSoftInput(
                        "我也说一句...", new IEditInputBtnListener() {

                            @Override
                            public void BtnSendOnClick(String text) {

                                API.ShowSaveReply post = new API.ShowSaveReply();
                                post.dynamicId = Integer.valueOf(itemData.getId());
                                post.content = text;
                                post.tocken = application.getUser().getToken();
                                post.post(getActivity(), ASNY_REPLY_OR_COMMENT_DYNAMIC, false, new IHttpSuccess() {
                                    @Override
                                    public void handleResult(int tag, String resultData) {
                                        handleRepliesTaskQueue(tag, resultData);
                                    }
                                });

                                DynamicReply replyItem = new DynamicReply("-1", application.getUser().getUserId() + "", application.getUser().getNickname(), "0", "0", text, "0");

                                int curReplyCount = allDynamicItems.get(curItemPosition)
                                        .getReplyCount() + 1;
                                allDynamicItems.get(curItemPosition).setReplyCount(
                                        curReplyCount);
                                allDynamicItems.get(curItemPosition).getReplyData()
                                        .add(replyItem);

                                HashMap<String, Integer> replyItemPosInfo = new HashMap<String, Integer>();
                                replyItemPosInfo.put(Tag_itemPosition, curItemPosition);
                                replyItemPosInfo.put(Tag_replyPosition, curReplyCount - 1);
                                upLoadingRepliesInfo.add(replyItemPosInfo);
                                updateData(curItemPosition);
                                if (commentList.getVisibility() == View.GONE) {
                                    commentList.setVisibility(View.VISIBLE);
                                }
                                showToast("评论成功");
                            }
                        });
            }
        });

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ct).setTitle("提示").setMessage("您确定要删除这条动态吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        API.DeleteShowerDynamic post = new API.DeleteShowerDynamic();
                        post.id = itemData.getId();
                        post.post(ct, 295, false, new IHttpSuccess() {
                            @Override
                            public void handleResult(int tag, String resultData) {

                            }
                        });
                        notifyItemRemoved(curItemPosition);
                        getItemDatas().remove(curItemPosition);
                        allDynamicItems = getItemDatas();
                        getAdapter().notifyItemRangeChanged(0, getItemDatasCount());
                        dialog.dismiss();
                    }
                }).show();
            }
        });


    }

    private void handleRepliesTaskQueue(int tag, String resultData) {
        if (tag == ASNY_REPLY_OR_COMMENT_DYNAMIC) {
            if (!upLoadingRepliesInfo.isEmpty()) {

                DynamicReply replyItem = JSON.parseObject(resultData,
                        DynamicReply.class);
                int itemPosition = upLoadingRepliesInfo.get(0).get(Tag_itemPosition);
                int replyPosition = upLoadingRepliesInfo.get(0).get(Tag_replyPosition);
                allDynamicItems.get(itemPosition).getReplyData().set(replyPosition, replyItem);
                upLoadingRepliesInfo.remove(0);
                updateData(itemPosition);

            }

        }
    }

    class DynamicImgsAdapter extends GeneralListAdapter<String> {

        private int mWidth = 0;
        private int mHeight = 0;
        private Context mContext;

        public DynamicImgsAdapter(Context ct, List<String> data,
                                  int itemLayoutId, int itemWidth, int itemHeight) {
            super(ct, data, itemLayoutId);
            this.mContext = ct;
            this.mWidth = itemWidth;
            this.mHeight = itemHeight;
        }

        @Override
        public void SetChildViewData(ViewHolder mViewHolder, String itemData, int position, ImageOptions imageOptions) {
            ImageView img = mViewHolder.getChildView(R.id.view_single_img_square);
            if (!itemData.isEmpty()) {
                Picasso.with(mContext).load(itemData).resize(mWidth, mHeight).centerCrop()
                        .placeholder(R.mipmap.img_actposter_nodata).error(R.mipmap.img_actposter_nodata).into(img);
            }
        }
    }

    class DynamicReplyAdapter extends GeneralListAdapter<DynamicReply> {

        public DynamicReplyAdapter(Context ct, List<DynamicReply> data,
                                   int itemLayoutId) {
            super(ct, data, itemLayoutId);
        }

        @Override
        public void SetChildViewData(ViewHolder mViewHolder, DynamicReply itemData, int position, ImageOptions imageOptions) {
            TextView reply = mViewHolder.getChildView(R.id.dynamic_reply);
            String replyContent = itemData.getContent();
            String replyUserId = itemData.getReplyUserId();
            String repliedUserId = itemData.getRepliedUserId();
            //            String replyUserName = itemData.getReplyUser();
            //            String repliedUserName = itemData.getRepliedUser();

            SpannableString replyUserName = new SpannableString(itemData.getReplyUser());

            TvClickSpan replyUserNameSpan = new TvClickSpan(ct, 0xff3b65b1, new TvClickSpan.iTvOnClickSpanListener() {
                @Override
                public void OnClick() {
                    //// TODO: 15/12/27 动态评论人的点击事件
                }
            });
            replyUserName.setSpan(replyUserNameSpan, 0, replyUserName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            if (!repliedUserId.equals("0")) {
                SpannableString repliedUserName = new SpannableString(itemData.getRepliedUser());
                TvClickSpan repliedUserNameSpan = new TvClickSpan(ct, 0xff3b65b1, new TvClickSpan.iTvOnClickSpanListener() {
                    @Override
                    public void OnClick() {
                        //// TODO: 15/12/27 被评论人的点击事件
                    }
                });
                repliedUserName.setSpan(repliedUserNameSpan, 0, repliedUserName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                //回复
                reply.setText(replyUserName);
                reply.append("回复");
                reply.append(repliedUserName);
                reply.append(" : ");
                reply.append(replyContent);
            } else {
                //评论
                reply.setText(replyUserName);
                reply.append(" : ");
                reply.append(replyContent);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        super.onActivityResult(requestCode, resultCode, data);
        //        if (resultCode == getActivity().RESULT_OK) {
        //            if (requestCode == ((ShowerDynamicActivity)getActivity()).Tag_RiseDynamicReturn) {
        //                boolean isReFresh = (Boolean) data.getExtras().get("dynamic");
        //                if (isReFresh) {
        //                    reLoadData();
        //                }
        //            }
        //        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }


}
