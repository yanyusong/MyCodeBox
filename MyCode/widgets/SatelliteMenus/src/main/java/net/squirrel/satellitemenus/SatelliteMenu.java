package net.squirrel.satellitemenus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides a "Path" like menu for android. ??
 * <p/>
 * TODO: tell about usage
 *
 * @author Siyamed SINIR
 */
public class SatelliteMenu extends FrameLayout {

    private static final float DEFAULT_FROM_DEGREE = 0f;
    private static final float DEFAULT_TO_DEGREE = 90f;
    private static final boolean DEFAULT_CLOSE_ON_CLICK = true;
    private static final int DEFAULT_EXPAND_DURATION = 400;
    private static final int DEFAULT_RADIUS_DISTANCE = 100;
    private static final int DEFAULT_IMGMAIN_RESID = R.mipmap.ic_launcher;

    private Animation mainRotateRight;
    private Animation mainRotateLeft;

    private ImageView imgMain;
    private SateliteClickedListener itemClickedListener;

    private List<SatelliteMenuItem> menuItems = new ArrayList<SatelliteMenuItem>();
    private Map<View, SatelliteMenuItem> viewToItemMap = new HashMap<View, SatelliteMenuItem>();

    private AtomicBoolean plusAnimationActive = new AtomicBoolean(false);

    // ?? how to save/restore?
    private IDegreeProvider gapDegreesProvider = new LinearDegreeProvider();

    //States of these variables are saved
    private boolean rotated = false;

    private int[] quadrant;//象限数组
    private String quadrantString;//象限字符串

    //States of these variables are saved - Also configured from XML
    private float fromDegree = DEFAULT_FROM_DEGREE;
    private float toDegree = DEFAULT_TO_DEGREE;
    private int resId = DEFAULT_IMGMAIN_RESID;
    private int radius = DEFAULT_RADIUS_DISTANCE;//单位已经是像素了
    //// TODO: 15/12/10 radius替换distance
    private int expandDuration = DEFAULT_EXPAND_DURATION;
    private boolean closeItemsOnClick = DEFAULT_CLOSE_ON_CLICK;

    private int frameWidth;//父布局宽
    private int frameHeight;//父布局高

    private int tempGravity = -1;
    private Context ct;

    public SatelliteMenu(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SatelliteMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SatelliteMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        ct = context;
        LayoutInflater.from(ct).inflate(R.layout.view_sat_main, this, true);
        imgMain = (ImageView) findViewById(R.id.sat_main);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SatelliteMenu, defStyle, 0);
            fromDegree = typedArray.getFloat(R.styleable.SatelliteMenu_fromDegrees, DEFAULT_FROM_DEGREE);
            toDegree = typedArray.getFloat(R.styleable.SatelliteMenu_toDegrees, DEFAULT_TO_DEGREE);
            closeItemsOnClick = typedArray.getBoolean(R.styleable.SatelliteMenu_closeOnClick, DEFAULT_CLOSE_ON_CLICK);
            expandDuration = typedArray.getInt(R.styleable.SatelliteMenu_expandDuration, DEFAULT_EXPAND_DURATION);
            resId = typedArray.getResourceId(R.styleable.SatelliteMenu_mainImage, DEFAULT_IMGMAIN_RESID);
            //单位：像素
            radius = typedArray.getDimensionPixelSize(R.styleable.SatelliteMenu_radius, DEFAULT_RADIUS_DISTANCE);
            typedArray.recycle();
        }
        quadrant = judgeQuadrant((int) fromDegree, (int) toDegree);
        quadrantString = judgePart(quadrant);
        initMainItem(resId);
    }

    private int[] judgeQuadrant(int fromDegree, int toDegree) {
        int n = 0;
        if (toDegree < fromDegree)
            toDegree += 360;
        n = (toDegree % 90 == 0 && fromDegree % 90 == 0) ? toDegree / 90 - fromDegree / 90 : toDegree / 90 - fromDegree / 90 + 1;
        if (n > 4)
            n = 4;
        int[] quadrant = new int[n];
        int i;
        for (i = 0; i < n; i++) {
            quadrant[i] = fromDegree / 90 + i + 1;
            if (quadrant[i] > 4)
                quadrant[i] -= 4;
        }
        return quadrant;
    }

    private String judgePart(int[] quadrant) {
        StringBuilder tag = new StringBuilder("");
        for (int i = 0; i < quadrant.length; i++) {
            tag.append(String.valueOf(quadrant[i]));
        }
        return tag.toString();
    }

    private void onClick() {
        //		compareAndSet(boolean expect, boolean update)
        //		这个方法主要两个作用
        //		1. 比较AtomicBoolean和expect的值，如果一致，执行方法内的语句。其实就是一个if语句
        //		2. 把AtomicBoolean的值设成update
        //		比较最要的是这两件事是一气呵成的，这连个动作之间不会被打断，任何内部或者外部的语句都不可能在两个动作之间运行。
        //AtomicBoolean plusAnimationActive
        //检测动画是否执行完了，没有的话，原子值AtomicBoolean还更新为true
        if (plusAnimationActive.compareAndSet(false, true)) {
            if (!rotated) {
                imgMain.startAnimation(mainRotateLeft);
                for (SatelliteMenuItem item : menuItems) {
                    item.getView().startAnimation(item.getOutAnimation());
                }
            } else {
                imgMain.startAnimation(mainRotateRight);
                for (SatelliteMenuItem item : menuItems) {
                    item.getView().startAnimation(item.getInAnimation());
                }
            }
            rotated = !rotated;
        }
    }

    private void openItems() {
        if (plusAnimationActive.compareAndSet(false, true)) {
            if (!rotated) {
                imgMain.startAnimation(mainRotateLeft);
                for (SatelliteMenuItem item : menuItems) {
                    item.getView().startAnimation(item.getOutAnimation());
                }
            }
            rotated = !rotated;
        }
    }

    private void closeItems() {
        if (plusAnimationActive.compareAndSet(false, true)) {
            if (rotated) {
                imgMain.startAnimation(mainRotateRight);
                for (SatelliteMenuItem item : menuItems) {
                    item.getView().startAnimation(item.getInAnimation());
                }
            }
            rotated = !rotated;
        }
    }

    private void initMainItem(int ResId) {
        imgMain.setImageResource(ResId);
        LayoutParams params = (LayoutParams) imgMain.getLayoutParams();
        switch (quadrantString) {
            case "1":
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case "2":
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case "3":
                params.gravity = Gravity.RIGHT | Gravity.TOP;
                break;
            case "4":
                params.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case "12":
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                break;
            case "23":
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                break;
            case "34":
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case "41":
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
            default:
                params.gravity = Gravity.CENTER;
                break;
        }

        imgMain.setLayoutParams(params);
        tempGravity = params.gravity;

        mainRotateLeft = SatelliteAnimationCreator.createMainButtonAnimation(ct);
        mainRotateRight = SatelliteAnimationCreator.createMainButtonInverseAnimation(ct);

        Animation.AnimationListener plusAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                plusAnimationActive.set(false);
            }
        };

        mainRotateLeft.setAnimationListener(plusAnimationListener);
        mainRotateRight.setAnimationListener(plusAnimationListener);
        imgMain.setVisibility(VISIBLE);
        imgMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iMainItemClickListener == null) {
                    SatelliteMenu.this.onClick();
                } else {
                    iMainItemClickListener.onClick(rotated);
                    SatelliteMenu.this.onClick();
                }
            }
        });
    }

    public interface IMainItemClickListener {
        /**
         *在该方法执行完后menu会自动执行toggle操作
         * @param rotated 返回当前是什么状态，true；open；false：close
         * */
        void onClick(boolean rotated);
    }

    private IMainItemClickListener iMainItemClickListener;

    public void setMainItemClick(IMainItemClickListener mIMainItemClickListener) {
        this.iMainItemClickListener = mIMainItemClickListener;
    }

    public boolean getIsRotated(){
        return rotated;
    }

    public void addItems(List<SatelliteMenuItem> items) {

        menuItems.addAll(items);
        this.removeView(imgMain);//先remove后add是为了保证imgmain始终在最上边
        int[] degrees = getDegrees(menuItems.size(), (int) fromDegree, (int) toDegree);
        int index = 0;
        for (SatelliteMenuItem menuItem : menuItems) {
            int translateX = SatelliteAnimationCreator.getTranslateX(
                    degrees[index], radius);
            int translateY = SatelliteAnimationCreator.getTranslateY(
                    degrees[index], radius);
            ImageView itemView = (ImageView) LayoutInflater.from(getContext())
                    .inflate(R.layout.view_sat_item_cr, this, false);
            itemView.setTag(menuItem.getId());
            itemView.setVisibility(View.GONE);

            if (menuItem.getImgResourceId() > 0) {
                itemView.setImageResource(menuItem.getImgResourceId());
            } else if (menuItem.getImgDrawable() != null) {
                itemView.setImageDrawable(menuItem.getImgDrawable());
            }

            Animation itemOut = SatelliteAnimationCreator.createItemOutAnimation(getContext(), index, expandDuration, translateX, translateY);
            Animation itemIn = SatelliteAnimationCreator.createItemInAnimation(getContext(), index, expandDuration, translateX, translateY);
            Animation itemClick = SatelliteAnimationCreator.createItemClickAnimation(getContext());

            menuItem.setView(itemView);
            menuItem.setInAnimation(itemIn);
            menuItem.setOutAnimation(itemOut);
            menuItem.setClickAnimation(itemClick);
            menuItem.setTranslateX(translateX);
            menuItem.setTranslateY(translateY);

            itemIn.setAnimationListener(new SatelliteAnimationListener(itemView, true, viewToItemMap));
            itemOut.setAnimationListener(new SatelliteAnimationListener(itemView, false, viewToItemMap));
            itemClick.setAnimationListener(new SatelliteItemClickAnimationListener(this, menuItem.getId()));

            LayoutParams params = (LayoutParams) itemView.getLayoutParams();
            params.gravity = tempGravity;
            itemView.setLayoutParams(params);
            addView(itemView);
            viewToItemMap.put(itemView, menuItem);
            index++;
        }
        this.addView(imgMain);
    }

    //todo
    public void setItemsOnTouchListener(final SateliteClickedListener onClickedListener) {

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int downx = 0;
                int downy = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downx = (int) event.getRawX();
                        downy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:

                        break;

                    default:
                        break;
                }
                int clickedItemId = checkItemNum(downx, downy);
                if (rotated && clickedItemId >= 0) {
                    closeItems();
                    onClickedListener.eventOccured(clickedItemId);
                    return true;
                }
                return false;
            }
        });
    }

    private int checkItemNum(int downx, int downy) {
        for (SatelliteMenuItem item : menuItems) {
            //// TODO: 15/12/11
            int[] location = new int[2];
            ImageView view = item.getView();
            view.getLocationOnScreen(location);
            int left = location[0];
            int right = left + view.getWidth();
            int top = location[1];
            int bottom = top + view.getHeight();

            int cloneLeft = left + item.getTranslateX();
            int cloneRight = cloneLeft + view.getWidth();
            int cloneTop = top + item.getTranslateY();
            int cloneBottom = cloneTop + view.getHeight();

            if (downx >= cloneLeft && downx <= cloneRight && downy >= cloneTop && downy <= cloneBottom) {
                return item.getId();
                //                AnticipateOvershootInterpolator
            }
        }
        return -1;
    }

    private int[] getDegrees(int count, int fromDeg, int toDeg) {
        return gapDegreesProvider.getDegrees(count, fromDeg, toDeg);
    }

    //	private void resetItems() {
    //		if (menuItems.size() > 0) {
    //			List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>(
    //					menuItems);
    //			menuItems.clear();
    //			this.removeAllViews();
    //			addItems(items);
    //		}
    //	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //        int measuredWidth = measureWidth(widthMeasureSpec);
        //        int measuredHeight = measureHeight(heightMeasureSpec);

        //测量子孙的尺寸并保存起来
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }

        int mainItemH = imgMain.getHeight();
        int itemH = getChildAt(0).getMeasuredHeight();

        int overShootDistance = 50;
        int shortEdge = radius + itemH / 2 + mainItemH / 2 + overShootDistance;
        int longEdge = 2 * radius + itemH / 2 + mainItemH / 2 + overShootDistance * 2;
        //// TODO: 15/12/15
        switch (quadrantString) {
            case "1":
                frameWidth = frameHeight = shortEdge;
                break;
            case "2":
                frameWidth = frameHeight = shortEdge;
                break;
            case "3":
                frameWidth = frameHeight = shortEdge;
                break;
            case "4":
                frameWidth = frameHeight = shortEdge;
                break;
            case "12":
                frameWidth = longEdge;
                frameHeight = shortEdge;
                break;
            case "23":
                frameWidth = shortEdge;
                frameHeight = longEdge;
                break;
            case "34":
                frameWidth = longEdge;
                frameHeight = shortEdge;
                break;
            case "41":
                frameWidth = shortEdge;
                frameHeight = longEdge;
                break;
            default:
                frameWidth = frameHeight = longEdge;
                break;
        }
        setMeasuredDimension(frameWidth, frameHeight);
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                break;
        }
        return result;
    }

    private static class SatelliteItemClickAnimationListener implements Animation.AnimationListener {
        private WeakReference<SatelliteMenu> menuRef;
        private int tag;

        public SatelliteItemClickAnimationListener(SatelliteMenu menu, int tag) {
            this.menuRef = new WeakReference<SatelliteMenu>(menu);
            this.tag = tag;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
            SatelliteMenu menu = menuRef.get();
            if (menu != null && menu.closeItemsOnClick) {
                menu.close();
                if (menu.itemClickedListener != null) {
                    //item的点击事件
                    //// TODO: 15/12/11
                    menu.itemClickedListener.eventOccured(tag);
                }
            }
        }
    }

    private static class SatelliteAnimationListener implements Animation.AnimationListener {
        private WeakReference<View> viewRef;
        private boolean isInAnimation;
        private Map<View, SatelliteMenuItem> viewToItemMap;

        public SatelliteAnimationListener(View view, boolean isIn, Map<View, SatelliteMenuItem> viewToItemMap) {
            this.viewRef = new WeakReference<View>(view);
            this.isInAnimation = isIn;
            this.viewToItemMap = viewToItemMap;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (viewRef != null) {
                View view = viewRef.get();
                if (view != null) {
                    SatelliteMenuItem menuItem = viewToItemMap.get(view);
                    if (isInAnimation) {

                    } else {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (viewRef != null) {
                View view = viewRef.get();
                if (view != null) {
                    SatelliteMenuItem menuItem = viewToItemMap.get(view);

                    if (isInAnimation) {
                        view.setVisibility(View.GONE);
                    } else {

                    }
                }
            }
        }
    }

    public Map<View, SatelliteMenuItem> getViewToItemMap() {
        return viewToItemMap;
    }

    //    /**
    //     * Sets the click listener for satellite items.
    //     *
    //     * @param itemClickedListener
    //     */
    //    public void setOnItemClickedListener(SateliteClickedListener itemClickedListener) {
    //        this.itemClickedListener = itemClickedListener;
    //    }

    /**
     * Sets the image resource for the center button.
     *
     * @param resource The image resource.
     */
    public void setMainImage(int resource) {
        this.imgMain.setImageResource(resource);
    }

    /**
     * Sets the image drawable for the center button.
     *
     * @param drawable The image drawable.
     */
    public void setMainImage(Drawable drawable) {
        this.imgMain.setImageDrawable(drawable);
    }

    /**
     * Defines if the menu shall collapse the items when an item is clicked. Default value is true.
     *
     * @param closeItemsOnClick
     */
    public void setCloseItemsOnClick(boolean closeItemsOnClick) {
        this.closeItemsOnClick = closeItemsOnClick;
    }

    /**
     * The listener class for item click event.
     *
     * @author Siyamed SINIR
     */
    public interface SateliteClickedListener {
        /**
         * When an item is clicked, informs with the id of the item, which is given while adding the items.
         *
         * @param id The id of the item.
         */
        void eventOccured(int id);
    }

    /**
     * Expand the menu items.
     */
    public void expand() {
        openItems();
    }

    /**
     * Collapse the menu items
     */
    public void close() {
        closeItems();
    }

    //    @Override
    //    protected Parcelable onSaveInstanceState() {
    //        Parcelable superState = super.onSaveInstanceState();
    //        SavedState ss = new SavedState(superState);
    //        ss.rotated = rotated;
    //        //		ss.totalSpacingDegree = totalSpacingDegree;
    //        //        ss.satelliteDistance = satelliteDistance;
    //        //		ss.measureDiff = measureDiff;
    //        ss.expandDuration = expandDuration;
    //        ss.closeItemsOnClick = closeItemsOnClick;
    //        return ss;
    //    }

    //    @Override
    //    protected void onRestoreInstanceState(Parcelable state) {
    //        SavedState ss = (SavedState) state;
    //        rotated = ss.rotated;
    //        //		totalSpacingDegree = ss.totalSpacingDegree;
    //        //        satelliteDistance = ss.satelliteDistance;
    //        //		measureDiff = ss.measureDiff;
    //        expandDuration = ss.expandDuration;
    //        closeItemsOnClick = ss.closeItemsOnClick;
    //
    //        super.onRestoreInstanceState(ss.getSuperState());
    //    }

    //    static class SavedState extends BaseSavedState {
    //        boolean rotated;
    //        private float totalSpacingDegree;
    //        private int satelliteDistance;
    //        //		private int measureDiff;
    //        private int expandDuration;
    //        private boolean closeItemsOnClick;
    //
    //        SavedState(Parcelable superState) {
    //            super(superState);
    //        }
    //
    //        public SavedState(Parcel in) {
    //            super(in);
    //            rotated = Boolean.valueOf(in.readString());
    //            totalSpacingDegree = in.readFloat();
    //            satelliteDistance = in.readInt();
    //            //			measureDiff = in.readInt();
    //            expandDuration = in.readInt();
    //            closeItemsOnClick = Boolean.valueOf(in.readString());
    //        }
    //
    //        @Override
    //        public int describeContents() {
    //            return 0;
    //        }
    //
    //        @Override
    //        public void writeToParcel(Parcel out, int flags) {
    //            out.writeString(Boolean.toString(rotated));
    //            out.writeFloat(totalSpacingDegree);
    //            out.writeInt(satelliteDistance);
    //            //			out.writeInt(measureDiff);
    //            out.writeInt(expandDuration);
    //            out.writeString(Boolean.toString(closeItemsOnClick));
    //        }
    //
    //        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
    //            public SavedState createFromParcel(Parcel in) {
    //                return new SavedState(in);
    //            }
    //
    //            public SavedState[] newArray(int size) {
    //                return new SavedState[size];
    //            }
    //        };
    //    }
}
