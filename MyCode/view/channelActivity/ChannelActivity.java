package net.boyazhidao.cgb.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.boyazhidao.cgb.R;
import net.boyazhidao.cgb.adapter.GeneralRecyclerViewHolder;
import net.boyazhidao.cgb.adapter.dragrecycler.ItemDragHelperCallback;
import net.boyazhidao.cgb.adapter.dragrecycler.OnItemMoveListener;
import net.boyazhidao.cgb.adapter.multirecycler.ItemEntityList;
import net.boyazhidao.cgb.adapter.multirecycler.MultiRecyclerAdapter;
import net.boyazhidao.cgb.adapter.multirecycler.OnBind;
import net.boyazhidao.cgb.base.BaseToolBarActivity;
import net.boyazhidao.cgb.cache.SpCacheKey;
import net.boyazhidao.cgb.events.MyChannelsEvent;
import net.boyazhidao.cgb.http.URLS;
import net.boyazhidao.cgb.model.ChannelBean;
import net.boyazhidao.cgb.model.ComRespInfo;
import net.boyazhidao.cgb.model.MySubscribe;
import net.boyazhidao.cgb.model.PageModel;
import net.boyazhidao.cgb.utils.MoveAnimationUtils;
import net.boyazhidao.cgb.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by mac on 16/6/24.
 */
public class ChannelActivity extends BaseToolBarActivity {

    private static int MY_SUBCRIBES_HEADERS = 1;

    private RecyclerView recyclerView;
    private ChannelsAdapter adapter;

    private ItemEntityList itemEntityList = new ItemEntityList();
    private List<MySubscribe> mySubscribes = new ArrayList<>();//我的订阅
    private List<MySubscribe> tempPostSubscribes = new ArrayList<>();//将要post到服务器的我的订阅,包含排序信息

    // 是否为 编辑 模式
    private boolean isEditMode = false;
    //positions信息是否是正确的,是否MySubcribes的remove动画还未执行完,
    //true 则我的订阅不能点击,false 可以点击
    private volatile boolean mySubcribeCanClickable = true;
    private ItemTouchHelper helper;

    private boolean isMySubcribesAdded = false;
    private boolean isMySubcribesEdited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolBarTitle("订阅");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(manager);
        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
        adapter = new ChannelsAdapter(mContext, itemEntityList);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == R.layout.item_channel_my || viewType == R.layout.item_channel_all ? 1 : 4;
            }
        });
        recyclerView.setAdapter(adapter);

        getChannelData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel;
    }

    private Handler animHandler = new Handler();

    private void getChannelData() {
        itemEntityList.addOnBind(R.layout.item_channel_my_header, new OnBind() {

            @Override
            public void onBindChildViewData(GeneralRecyclerViewHolder holder, Object itemData, int position) {
                holder.setText(R.id.tv, (String) itemData);
                final TextView editBtn = holder.getChildView(R.id.tv_btn_edit);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEditMode) {
                            switchEditMode(true);
                            editBtn.setText(R.string.finish);
                        } else {
                            switchEditMode(false);
                            editBtn.setText(R.string.edit);
                            postMySubcribes(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    String statusCode = "";
                                    String message = e.getMessage();
                                    statusCode = message.split(":")[1];
                                    if (statusCode.equals(" 401")) {
                                        startLoginActivity();
                                    }else{
                                        showToast("保存我的订阅失败!");
                                    }
                                    isMySubcribesEdited = true;
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    SPUtils.put(mContext,SpCacheKey.mySubcribes,tempPostSubscribes);
                                    showToast("保存我的订阅成功!");
                                    isMySubcribesEdited = false;
                                    EventBus.getDefault().post(new MyChannelsEvent(tempPostSubscribes));
                                }
                            });
                        }
                    }
                });
            }
        })
                .addOnBind(R.layout.item_channel_my, new OnBind() {
                    @Override
                    public void onBindChildViewData(final GeneralRecyclerViewHolder holder, final Object itemData, final int position) {
                        TextView tv = holder.getChildView(R.id.tv);
                        ImageView img = holder.getChildView(R.id.img_edit);
                        tv.setText(TextUtils.isEmpty(((MySubscribe) itemData).getName()) ? ((MySubscribe) itemData).getSequenceCode() : ((MySubscribe) itemData).getName());
                        img.setVisibility(isEditMode ? View.VISIBLE : View.INVISIBLE);

                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isEditMode) {
                                    if (mySubcribeCanClickable) {

                                        mySubcribeCanClickable = false;
                                        //只能一个一个且等notifyItemRangeChanged执行完了才能remove,不然快速点击倒数第二个,
                                        // 再点倒数第一个会出现数组越界,即position位置异常了
                                        mySubscribes.remove(position - MY_SUBCRIBES_HEADERS);
                                        itemEntityList.remove(position);
                                        adapter.notifyItemRemoved(position);

                                        //直接执行会出现remove动画卡顿,所以postdelay等动画执行完毕再刷新数据,使能点击事件
                                        //                                    adapter.notifyItemRangeChanged(position, mySubscribes.size() - (position - MY_SUBCRIBES_HEADERS));
                                        //                                    adapter.notifyDataSetChanged();
                                        animHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("pos", "Holder的position=" + position + ",ChangedItemCount==" + (mySubscribes.size() - (position - MY_SUBCRIBES_HEADERS)));
                                                adapter.notifyDataSetChanged();
                                                //                     使用这个要详细再判断rang范围                           adapter.notifyItemRangeChanged(position, mySubscribes.size() - (position - MY_SUBCRIBES_HEADERS));
                                                mySubcribeCanClickable = true;
                                            }
                                        }, 480);
                                        isMySubcribesEdited = true;
                                    }

                                } else {
                                    //// TODO: 16/6/27 非编辑模式点击我的频道
                                }
                            }
                        });


                        tv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (!isEditMode) {
                                    switchEditMode(true);
                                    // header 按钮文字 改成 "完成"
                                    List<Integer> postions = itemEntityList.getPositionsForViewType(R.layout.item_channel_my_header);
                                    for (int i = 0; i < postions.size(); i++) {
                                        View view = recyclerView.getChildAt(0);
                                        TextView tvBtnEdit = (TextView) view.findViewById(R.id.tv_btn_edit);
                                        if (tvBtnEdit != null) {
                                            tvBtnEdit.setText(R.string.finish);
                                        }
                                    }
                                }
                                helper.startDrag(holder);
                                isMySubcribesEdited = true;
                                return true;
                            }
                        });

                    }
                })
                .addOnBind(R.layout.item_channel_all_header, new OnBind() {
                    @Override
                    public void onBindChildViewData(GeneralRecyclerViewHolder holder, Object itemData, int position) {
                        holder.setText(R.id.tv, (String) itemData);
                    }
                })
                .addOnBind(R.layout.item_channel_header, new OnBind() {
                    @Override
                    public void onBindChildViewData(GeneralRecyclerViewHolder holder, Object itemData, int position) {
                        holder.setText(R.id.tv, (String) itemData);
                    }
                })
                .addOnBind(R.layout.item_channel_all, new OnBind() {
                            @Override
                            public void onBindChildViewData(final GeneralRecyclerViewHolder holder, final Object itemData, final int position) {
                                TextView tv = holder.getChildView(R.id.tv);
                                tv.setText(((ChannelBean.LevelCategoryEntity) itemData).getName());
                                final String sequenceCode = ((ChannelBean.LevelCategoryEntity) itemData).getSequenceCode();
                                tv.setOnClickListener(new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View v) {
                                                              for (MySubscribe subcribe :
                                                                      mySubscribes) {
                                                                  if (subcribe.getSequenceCode().equals(sequenceCode)) {
                                                                      Toast.makeText(mContext, "您已经订阅过了" + ((ChannelBean.LevelCategoryEntity) itemData).getName() + "!", Toast.LENGTH_SHORT).show();
                                                                      return;
                                                                  }
                                                              }

                                                              MySubscribe mySubscribe = new MySubscribe();
                                                              mySubscribe.setSequenceCode(sequenceCode);
                                                              mySubscribe.setRankNum(mySubscribes.size());
                                                              mySubscribe.setName(((ChannelBean.LevelCategoryEntity) itemData).getName());
                                                              mySubscribes.add(mySubscribe);
                                                              final int insertedPos = MY_SUBCRIBES_HEADERS + mySubscribes.size() - 1;
                                                              itemEntityList.addItem(insertedPos, R.layout.item_channel_my, mySubscribe);

                                                              RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                                                              View lastView = manager.findViewByPosition(insertedPos - 1);//要插入的前一个位置的view
                                                              int x = 0;
                                                              int y = 0;
                                                              if (recyclerView.indexOfChild(lastView) >= 0) {
                                                                  int[] location = new int[2];
                                                                  lastView.getLocationOnScreen(location);

                                                                  if (recyclerView.getWidth() - manager.getDecoratedRight(lastView) > recyclerView.getPaddingRight()) {

                                                                      x = location[0] + lastView.getWidth();
                                                                      y = location[1];
                                                                  } else {
                                                                      int[] location1 = new int[2];
                                                                      recyclerView.getLocationOnScreen(location1);
                                                                      x = location1[0] + recyclerView.getPaddingLeft();
                                                                      y = location[1] + lastView.getHeight();
                                                                  }

                                                              } else {
                                                                  //不在屏幕中
                                                                  x = 360;
                                                                  y = -50;
                                                              }
                                                              Log.e("p", "x=" + x + ",y=" + y);
                                                              //点击删除
                                                              final ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
                                                              int currentPosition = holder.getAdapterPosition();
                                                              //                                                              // 如果RecyclerView滑动到底部,移动的目标位置的y轴 - height
                                                              View currentView = manager.findViewByPosition(currentPosition);

                                                              int[] currentViewLocations = new int[2];
                                                              currentView.getLocationOnScreen(currentViewLocations);

                                                              final View mirrorView = addMirrorView(viewGroup, recyclerView, currentView);
                                                              MoveAnimationUtils.startMoveAnimation(mirrorView, x - currentViewLocations[0], y - currentViewLocations[1], new Animation.AnimationListener() {
                                                                  @Override
                                                                  public void onAnimationStart(Animation animation) {
                                                                      adapter.notifyItemInserted(insertedPos);
                                                                      isMySubcribesAdded = true;
                                                                  }

                                                                  @Override
                                                                  public void onAnimationEnd(Animation animation) {
                                                                      viewGroup.removeView(mirrorView);
                                                                  }

                                                                  @Override
                                                                  public void onAnimationRepeat(Animation animation) {

                                                                  }
                                                              });
                                                          }
                                                      }

                                );
                            }
                        }

                );

        getAllCategorys();//先获取全部分类,再获取我的订阅

    }

    /**
     * 添加需要移动的 镜像View
     */
    private ImageView addMirrorView(ViewGroup parent, RecyclerView recyclerView, View view) {
        /**
         * 我们要获取cache首先要通过setDrawingCacheEnable方法开启cache，然后再调用getDrawingCache方法就可以获得view的cache图片了。
         buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，若果cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         若想更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         当调用setDrawingCacheEnabled方法设置为false, 系统也会自动把原来的cache销毁。
         */
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        final ImageView mirrorView = new ImageView(recyclerView.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int[] parenLocations = new int[2];
        recyclerView.getLocationOnScreen(parenLocations);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);

        return mirrorView;
    }

    private void postMySubcribes(StringCallback callback) {
        List<MySubscribe> tempSubscribes = new ArrayList<>();//我的订阅
        List<Object> tempMySubscribes = itemEntityList.getItemDatasForViewType(R.layout.item_channel_my);
        for (int i = 0; i < tempMySubscribes.size(); i++) {
            MySubscribe subcribe = (MySubscribe) tempMySubscribes.get(i);
            tempSubscribes.add(new MySubscribe(tempMySubscribes.size() - i, subcribe.getSequenceCode(), subcribe.getName()));
        }

        StringBuilder stringBuilder = new StringBuilder(SPUtils.get(mContext, SpCacheKey.tokenType, "").toString());
        stringBuilder.append(" ");
        stringBuilder.append(SPUtils.get(mContext, SpCacheKey.accessToken, "").toString());
        tempPostSubscribes = tempSubscribes;
        String json = new Gson().toJson(tempSubscribes);
        OkHttpUtils
                .postString()
                .url(URLS.Post_Save_Subcribes)
                .addHeader("Authorization", stringBuilder.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(callback);
    }

    private void getMySubcribes() {
        StringBuilder stringBuilder = new StringBuilder(SPUtils.get(mContext, SpCacheKey.tokenType, "").toString());
        stringBuilder.append(" ");
        stringBuilder.append(SPUtils.get(mContext, SpCacheKey.accessToken, "").toString());

        OkHttpUtils
                .get()
                .url(URLS.Get_Supplier_Subcribes)
                .addHeader("Authorization", stringBuilder.toString())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        String statusCode = "";
                        String message = e.getMessage();
                        statusCode = message.split(":")[1];
                        if (statusCode.equals(" 401")) {
                            startLoginActivity();
                        }else{
                            showToast("获取我的订阅失败!");
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        ComRespInfo comRespInfo = JSON.parseObject(response, ComRespInfo.class);
                        PageModel pageModel = JSON.parseObject(comRespInfo.getData(), PageModel.class);
                        List<MySubscribe> tempList = JSON.parseArray(pageModel.getList(), MySubscribe.class);
                        mySubscribes.addAll(0, tempList);
                        itemEntityList.addItems(MY_SUBCRIBES_HEADERS, R.layout.item_channel_my, tempList);
                        adapter.notifyDataSetChanged();

                    }
                });

    }

    private void getAllCategorys() {
        OkHttpUtils
                .get()
                .url(URLS.Get_All_Categorys)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        showToast("获取全部分类失败!");
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        ComRespInfo data = JSON.parseObject(response, ComRespInfo.class);
                        PageModel pageModel = JSON.parseObject(data.getData(), PageModel.class);
                        List<ChannelBean> list = JSON.parseArray(pageModel.getList(), ChannelBean.class);

                        itemEntityList.addItem(R.layout.item_channel_my_header, "我的订阅")
                                //                                .addItems(R.layout.item_channel_my, items)
                                .addItem(R.layout.item_channel_all_header, "全部分类");

                        for (int i = 0; i < list.size(); i++) {
                            itemEntityList.addItem(R.layout.item_channel_header, list.get(i).getName());
                            itemEntityList.addItems(R.layout.item_channel_all, list.get(i).getLevelCategory());
                        }

                        adapter.notifyDataSetChanged();

                        getMySubcribes();

                    }
                });

    }

    /**
     * 切换编辑模式和完成模式
     */
    private void switchEditMode(boolean canEditable) {
        isEditMode = canEditable;
        List<Integer> postions = itemEntityList.getPositionsForViewType(R.layout.item_channel_my);
        for (int i = 0; i < postions.size(); i++) {
            setShowDeleteIcon(postions.get(i), canEditable);
        }
    }

    private void setShowDeleteIcon(int position, boolean isShow) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            ImageView imgEdit = (ImageView) viewHolder.itemView.findViewById(R.id.img_edit);
            if (imgEdit != null) {
                imgEdit.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
            }
        }

    }


    class ChannelsAdapter extends MultiRecyclerAdapter implements OnItemMoveListener {

        public ChannelsAdapter(Context ct, ItemEntityList itemList) {
            super(ct, itemList);
        }

        @Override
        public void onItemMove(int viewType, int fromPosition, int toPosition) {
            Object object = itemList.getItemData(fromPosition);
            itemList.remove(fromPosition);
            itemList.addItem(toPosition, viewType, object);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (isMySubcribesAdded && (!isMySubcribesEdited)) {
                postMySubcribes(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String statusCode = "";
                        String message = e.getMessage();
                        statusCode = message.split(":")[1];
                        if (statusCode.equals(" 401")) {
                            startLoginActivity();
                        }else{
                            showToast("保存我的订阅失败!");
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SPUtils.put(mContext,SpCacheKey.mySubcribes,tempPostSubscribes);
                        EventBus.getDefault().post(new MyChannelsEvent(tempPostSubscribes));
                    }

                    @Override
                    public void onAfter(int id) {
                        finish();
                    }
                });
            }

            if (isMySubcribesEdited) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("是否保存更改?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postMySubcribes(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        String statusCode = "";
                                        String message = e.getMessage();
                                        statusCode = message.split(":")[1];
                                        if (statusCode.equals(" 401")) {
                                            startLoginActivity();
                                        }else{
                                            showToast("保存我的订阅失败!");
                                        }
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        SPUtils.put(mContext,SpCacheKey.mySubcribes,tempPostSubscribes);
                                        EventBus.getDefault().post(new MyChannelsEvent(tempPostSubscribes));
                                    }

                                    @Override
                                    public void onAfter(int id) {
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
            if ((!isMySubcribesAdded) && (!isMySubcribesEdited)) {
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }


}
