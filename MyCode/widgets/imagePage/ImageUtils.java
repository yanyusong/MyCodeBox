package net.xichiheng.yulewa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import net.xichiheng.yulewa.activity.ImagePageActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


public class ImageUtils {

    public static String TAG_path = "path";
    /**
     * "0":网络图片
     * "1":本地图片
     */
    public static String TAG_fromTag = "fromTag";

    /**
     * 转换图片成圆�?
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap (Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth ();
        int height = bitmap.getHeight ();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap (width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (output);

        final int color = 0xff424242;
        final Paint paint = new Paint ();
        final Rect src = new Rect ((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect ((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF (dst);

        paint.setAntiAlias (true);

        canvas.drawARGB (0, 0, 0, 0);
        paint.setColor (color);
        canvas.drawRoundRect (rectF, roundPx, roundPx, paint);

        paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap (bitmap, src, dst, paint);
        return output;
    }

    //圆角图片
    public static Bitmap getRoundedCornerBitmap (Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap (bitmap.getWidth (), bitmap
                .getHeight (), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (output);

        final int color = 0xff424242;
        final Paint paint = new Paint ();
        final Rect rect = new Rect (0, 0, bitmap.getWidth (), bitmap.getHeight ());
        final RectF rectF = new RectF (rect);

        paint.setAntiAlias (true);
        canvas.drawARGB (0, 0, 0, 0);
        paint.setColor (color);
        /**
         * 画一个圆角矩�?
         * rectF: 矩形
         *  n 圆角在x轴上或y轴上的半�?
         */
        canvas.drawRoundRect (rectF, roundPx, roundPx, paint);
        //设置两张图片相交时的模式
        //setXfermode前的�?dst 之后的是src
        //在正常的情况下，在已有的图像上绘图将会在其上面添加一层新的形状�?
        //如果新的Paint是完全不透明的，那么它将完全遮挡住下面的Paint�?
        //PorterDuffXfermode就可以来解决这个问题
        //canvas原有的图�?可以理解为背�?就是dst
        //新画上去的图�?可以理解为前�?就是src
        //	      paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap (bitmap, rect, rect, paint);

        return output;
    }

    //bitmap转换成byte数组
    public static byte[] getByteArrayFromBitmap (Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled ()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream ();
            bitmap.compress (Bitmap.CompressFormat.PNG, 0, bos);
            return bos.toByteArray ();
        } else {
            return null;
        }
    }

    /**
     * Drawable To Bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        int width = drawable.getIntrinsicWidth ();
        int height = drawable.getIntrinsicHeight ();
        Bitmap bitmap = Bitmap.createBitmap (width, height, drawable
                .getOpacity () != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas (bitmap);
        drawable.setBounds (0, 0, width, height);
        drawable.draw (canvas);
        return bitmap;

    }

    //按照原本比例 大小压缩�? 在按照质量压�?
    public static byte[] getimage (String srcPath, float width, float height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options ();
        //�?��读入图片，此时把options.inJustDecodeBounds 设回true�?
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile (srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比�?由于是固定比例缩放，只用高或者宽其中�?��数据进行计算即可
        int be = 1;//be=1表示不缩�?
        if (w > h && w > height) {//如果宽度大的话根据宽度固定大小缩�?
            be = (int) (newOpts.outWidth / height);
        } else if (w < h && h > width) {//如果高度高的话根据宽度固定大小缩�?
            be = (int) (newOpts.outHeight / width);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false�?
        Bitmap bitmap = BitmapFactory.decodeFile (srcPath, newOpts);
        return compressImage (bitmap);//压缩好比例大小后再进行质量压�?
    }

    //码流压缩
    private static byte[] compressImage (Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        image.compress (Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这�?00表示不压缩，把压缩后的数据存放到baos�?
        int options = 100;
        while (baos.toByteArray ().length / 1024 > 200) {  //循环判断如果压缩后图片是否大�?00kb,大于继续压缩
            baos.reset ();//重置baos即清空baos
            image.compress (Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos�?
            options -= 10;//每次都减�?0
        }
        return baos.toByteArray ();
    }

    // 大小缩放
    public Bitmap img_comp_b (Bitmap image, float width) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        image.compress (Bitmap.CompressFormat.PNG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream (baos.toByteArray ());
        BitmapFactory.Options newOpts = new BitmapFactory.Options ();
        //�?��读入图片，此时把options.inJustDecodeBounds 设回true�?
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream (isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比�?由于是固定比例缩放，只用高或者宽其中�?��数据进行计算即可
        int be = 1;//be=1表示不缩�?
        if (w > h && w < width) {//如果宽度大的话根据宽度固定大小缩�?
            be = (int) (w / width);
        }
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false�?
        isBm = new ByteArrayInputStream (baos.toByteArray ());
        bitmap = BitmapFactory.decodeStream (isBm, null, newOpts);
        return bitmap;//不进行质量压�?
    }

    /*
     * 获取图片大小
     */
    @SuppressLint ("NewApi")
    public static int getBitmapSize (Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount () / 1024;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount () / 1024;
        }
        return bitmap.getRowBytes () * bitmap.getHeight () / 1024;                //earlier version
    }

    /**
     * 获取原图片的网络地址
     *
     * @param url
     * @return
     */
    public static String getPrimaryImageUrl (String url) {
        //			String cutStr = "?imageMogr2";
        String str = "";
        //			// imgUrl为空时候返回null
        //			if (url == null) {
        //				return null;
        //			}
        //			// 判断返回的是原图还是截取后的
        //			if (url.indexOf(cutStr) == -1) {
        //				// 原图
        //				str = url;
        //			} else {
        //				// 截取后的
        //				str = url.substring(0, url.indexOf(cutStr));
        //			}
        str = url;
        return str;

    }

    /**
     * 通用的加载方法，可显示网络和本地的图片
     *
     * @param mContext
     * @param i
     * @param paths
     */
    public static void imageBrower (Context mContext, int i, List<HashMap<String, String>> paths) {
        Intent intent = new Intent (mContext, ImagePageActivity.class);
        intent.putExtra (ImagePageActivity.EXTRA_IMAGE_PATHS, (Serializable) paths);
        intent.putExtra (ImagePageActivity.EXTRA_IMAGE_INDEX, i);
        mContext.startActivity (intent);
    }
}













