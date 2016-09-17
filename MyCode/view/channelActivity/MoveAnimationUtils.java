package net.boyazhidao.cgb.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * Created by mac on 16/6/21.
 */
public class MoveAnimationUtils {

    private static final long MOVE_ANIM_TIME = 250L;//360ms

    /**
     * @param view
     * @param toXValue 相对屏幕的left的px值
     * @param toYValue 相对屏幕的top的px值
     */
    public static void startMoveAnimation(View view, int toXValue, int toYValue, Animation.AnimationListener listener) {

        float fromXValue = 0f;
        float fromYValue = 0f;
        //        float x = 0.7f;
        //        float y = 0.7f;
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromXValue,
                Animation.ABSOLUTE, toXValue, Animation.RELATIVE_TO_SELF, fromYValue, Animation.ABSOLUTE, toYValue);
        translateAnimation.setDuration(MOVE_ANIM_TIME);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.3f);
        alphaAnimation.setDuration(MOVE_ANIM_TIME);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(listener);

        view.startAnimation(animationSet);
    }
}
