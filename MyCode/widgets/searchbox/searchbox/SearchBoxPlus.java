package cn.aibianli.sdot.common.widgets.searchbox.searchbox;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.aibianli.sdot.R;
import cn.aibianli.sdot.common.widgets.io.codetail.animation.ReverseInterpolator;
import cn.aibianli.sdot.common.widgets.io.codetail.animation.SupportAnimator;
import cn.aibianli.sdot.common.widgets.io.codetail.animation.ViewAnimationUtils;
import cn.aibianli.sdot.common.widgets.io.codetail.widget.RevealFrameLayout;
import cn.aibianli.sdot.common.widgets.searchbox.materialmenu.MaterialMenuDrawable;
import cn.aibianli.sdot.common.widgets.searchbox.materialmenu.MaterialMenuView;

/**
 * Created by mac on 16/8/6.
 */
public class SearchBoxPlus extends FrameLayout implements MaterialMenuActions, EndImgActions, SearchEditActions, SelectMenuActions {

    //控件
    private EditText editSearch;
    private ImageView imgEnd;
    private TextView selectType;
    private MaterialMenuView materialMenu;
    private LinearLayout laySearchBoxPlus;
    private RevealFrameLayout laySearchBox;
    //    private FrameLayout bgOutside;
    //action 接口
    private ISelectMenuView selectMenuViewImp;

    //状态
    private boolean curSearchIsOpen = false;//当前search open状态

    private ImgState curImgState = ImgState.None;

    //config
    private boolean animateDrawerLogo = true;
    private boolean autoShowHideSelectView = true;//是否自动展开selectPopupView

    private SearchBoxOpenCloseListener openCloseListener;


    private Context context;

    public SearchBoxPlus(Context context) {
        this(context, null);
    }

    public SearchBoxPlus(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBoxPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.searchbox, this);

        this.curSearchIsOpen = false;
        this.context = context;
        //        bgOutside = (FrameLayout) findViewById(R.id.bg_outside);
        laySearchBoxPlus = (LinearLayout) findViewById(R.id.lay_search);
        laySearchBox = (RevealFrameLayout) findViewById(R.id.searchbox_rfl);
        materialMenu = (MaterialMenuView) findViewById(R.id.material_menu_button);
        selectType = (TextView) findViewById(R.id.select_type);
        editSearch = (EditText) findViewById(R.id.edit_search);
        imgEnd = (ImageView) findViewById(R.id.img_end);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RelativeLayout searchRoot = (RelativeLayout) findViewById(R.id.search_root);
            LayoutTransition lt = new LayoutTransition();
            lt.setDuration(100);
            searchRoot.setLayoutTransition(lt);
        }

        editSearch.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (selectMenuViewImp != null && selectMenuViewImp.isShowing()) {
                        selectMenuViewImp.hideSelectView();
                    }
                }
                return false;
            }
        });

        //        bgOutside.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if (curSearchIsOpen) {
        //                    hideSearchBoxPlus();
        //                }
        //            }
        //        });

    }

    public void setSearchBoxOpenCloseListener(SearchBoxOpenCloseListener listener) {
        this.openCloseListener = listener;
    }


    public void setAutoShowHideSelectView(boolean autoShowHideSelectView) {
        this.autoShowHideSelectView = autoShowHideSelectView;
    }

    public void setMaterialMenuClickedListener(final OnMaterialMenuClickedListener onMaterialMenuClickedListener) {

        materialMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onMaterialMenuClickedListener.materialMenuClicked();
            }
        });

    }

    public EditText getEditSearch() {
        return editSearch;
    }


    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void showSearchBoxPlus() {
        revealFrom(getScreenWidth(getContext()) - 40, 40);
    }

    public void hideSearchBoxPlus() {
        hideCircularly(getScreenWidth(getContext()), 0);
    }

    public boolean isShowing() {
        return curSearchIsOpen;
    }

    @Override
    public void switchImgState(ImgState state) {

        switch (state) {
            case Clear:
                if (imgEnd.getVisibility() != VISIBLE) {
                    imgEnd.setVisibility(VISIBLE);
                }
                imgEnd.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_clear));
                curImgState = ImgState.Clear;
                break;
            case Search:
                if (imgEnd.getVisibility() != VISIBLE) {
                    imgEnd.setVisibility(VISIBLE);
                }
                imgEnd.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_search_black));
                curImgState = ImgState.Search;
                break;
            case None:
                if (imgEnd.getVisibility() != GONE) {
                    imgEnd.setVisibility(GONE);
                }
                curImgState = ImgState.None;
                break;
            default:
                break;
        }
    }

    public void setEndImgClickListener(final OnEndImgClickListener mEndImgClickListener) {
        if (mEndImgClickListener != null) {
            imgEnd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEndImgClickListener.onEndImgClick(curImgState);
                }
            });
        }
    }

    @Override
    public void openSearcher() {
        if (!curSearchIsOpen) {
            if (animateDrawerLogo) {
                this.materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
            }
            this.editSearch.setVisibility(VISIBLE);
            this.selectType.setVisibility(VISIBLE);
            this.imgEnd.setVisibility(VISIBLE);
            switchImgState(curImgState);
        }
        curSearchIsOpen = true;
    }

    @Override
    public void closeSearcher() {
        if (curSearchIsOpen) {
            if (animateDrawerLogo) {
                this.materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            }
            this.editSearch.setVisibility(GONE);
            this.selectType.setVisibility(GONE);
            this.imgEnd.setVisibility(GONE);
        }

        curSearchIsOpen = false;
    }

    @Override
    public void toggleSearcher() {
        if (curSearchIsOpen) {
            closeSearcher();
            curSearchIsOpen = false;
        } else {
            openSearcher();
            curSearchIsOpen = true;
        }
    }


    public TextView getSelectMenu() {
        return selectType;
    }

    public MaterialMenuView getMaterialMenu() {
        return materialMenu;
    }

    @Override
    public String getSelectMenuText() {
        return selectType.getText().toString();
    }

    @Override
    public void setSearchText(String searchText) {
        editSearch.setText(searchText);
    }

    @Override
    public String getSearchText() {
        return editSearch.getText().toString();
    }

    @Override
    public void clearSearchText() {
        editSearch.getText().clear();
    }

    @Override
    public void setSelectMenuText(String text) {
        selectType.setText(text);
    }

    public void setSelectMenuViewImp(ISelectMenuView selectMenuViewImp) {
        if (selectMenuViewImp != null) {
            this.selectMenuViewImp = selectMenuViewImp;
        }

    }

    public ISelectMenuView getSelectMenuViewImp() {
        return selectMenuViewImp;
    }

    public void setOnSelectMenuClickListener(final OnSelectMenuClickListener selectMenuListener) {

        selectType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectMenuListener != null) {
                    selectMenuListener.onSelectMenuClick();
                }
            }
        });
    }

    public void hideCircularly(int x, int y) {

        RelativeLayout root = (RelativeLayout) findViewById(R.id.search_root);
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96,
                r.getDisplayMetrics());
        int finalRadius = (int) Math.max(laySearchBoxPlus.getWidth() * 1.5, px);

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(
                root, x, y, 0, finalRadius);
        animator.setInterpolator(new ReverseInterpolator());
        animator.setDuration(500);
        animator.addListener(new SupportAnimator.AnimatorListener() {

            @Override
            public void onAnimationStart() {
                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                if (selectMenuViewImp != null && autoShowHideSelectView) {
                    selectMenuViewImp.hideSelectView();
                }
                closeSearcher();
            }

            @Override
            public void onAnimationEnd() {
                if (laySearchBox.getVisibility() != GONE) {
                    laySearchBox.setVisibility(GONE);
                }
                if (laySearchBoxPlus.getVisibility() != GONE) {
                    laySearchBoxPlus.setVisibility(GONE);
                }
                setVisibility(View.GONE);
                if (openCloseListener != null) {
                    openCloseListener.doOnClosed();
                }
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }

        });
        animator.start();
    }


    public void revealFrom(float x, float y) {
        setVisibility(VISIBLE);
        if (laySearchBoxPlus.getVisibility() != VISIBLE) {
            laySearchBoxPlus.setVisibility(VISIBLE);
        }
        if (laySearchBox.getVisibility() != VISIBLE) {
            laySearchBox.setVisibility(VISIBLE);
        }
        RelativeLayout root = (RelativeLayout) findViewById(R.id.search_root);
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96,
                r.getDisplayMetrics());

        int finalRadius = (int) Math.max(laySearchBoxPlus.getWidth(), px);

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(
                root, (int) x, (int) y, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.addListener(new SupportAnimator.AnimatorListener() {

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationEnd() {
                openSearcher();
                if (openCloseListener != null) {
                    openCloseListener.doOnOpened();
                }
                if (selectMenuViewImp != null && autoShowHideSelectView) {
                    selectMenuViewImp.showSelectView();
                }
            }

            @Override
            public void onAnimationRepeat() {

            }

            @Override
            public void onAnimationStart() {

            }

        });
        animator.start();
    }

}

















