package net.squirrel.satellitemenus;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Menu Item.
 * <p/>
 * TODO: tell about usage
 *
 * @author Siyamed SINIR
 */
public class SatelliteMenuItem {
    private int id;
    private int imgResourceId;
    private Drawable imgDrawable;
    private ImageView view;
    private Animation outAnimation;
    private Animation inAnimation;
    private Animation clickAnimation;
    private int translateX;
    private int translateY;

    public int getTranslateY() {
        return translateY;
    }

    public void setTranslateY(int translateY) {
        this.translateY = translateY;
    }

    public int getTranslateX() {
        return translateX;
    }

    public void setTranslateX(int translateX) {
        this.translateX = translateX;
    }

    public SatelliteMenuItem(int id, int imgResourceId) {
        this.imgResourceId = imgResourceId;
        this.id = id;
    }

    public SatelliteMenuItem(int id, Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgResourceId() {
        return imgResourceId;
    }

    public void setImgResourceId(int imgResourceId) {
        this.imgResourceId = imgResourceId;
    }

    public Drawable getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    void setView(ImageView view) {
        this.view = view;
    }

    ImageView getView() {
        return view;
    }

    void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    Animation getInAnimation() {
        return inAnimation;
    }

    void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    Animation getOutAnimation() {
        return outAnimation;
    }

    void setClickAnimation(Animation clickAnim) {
        this.clickAnimation = clickAnim;
    }

    Animation getClickAnimation() {
        return clickAnimation;
    }
}