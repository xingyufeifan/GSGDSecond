package com.nandi.gsgdsecond.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ChenPeng on 2017/10/16.
 */

public class PictureUtils {
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //只返回图片的大小信息
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,                                        int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    public static void compressImageToFile(Bitmap bmp,File file) {
        int options = 30;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 灾情速报拍照相关方法
     * @param c
     * @param file
     * @return
     */
    public static Bitmap getxtsldraw(Context c, String file){
        File f = new File(file);
        Bitmap drawable = null;
        if (f.length()/1024 < 100){
            drawable = BitmapFactory.decodeFile(file);
        } else {
            Cursor cursor = c.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] {MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + " like ?",
                    new String[] {"%" + file}, null);
            if (cursor==null || cursor.getCount()==0){
                drawable = getBitmap(file, 720*1280);
            } else {
                if (cursor.moveToFirst()){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    long videoIdLong = Long.parseLong(videoId);
                    Bitmap bitmap = Thumbnails.getThumbnail(
                            c.getContentResolver(), videoIdLong, Thumbnails.MINI_KIND, options);
                    return bitmap;
                }
            }
        }

        int degree = 0;
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(file);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            if (degree != 0 && drawable != null) {
                Matrix m = new Matrix();
                m.setRotate(degree, (float) drawable.getWidth() / 2,(float) drawable.getHeight() / 2);
                drawable = Bitmap.createBitmap(drawable, 0, 0,
                        drawable.getWidth(), drawable.getHeight(), m, true);
            }
        } catch (OutOfMemoryError e){

        } catch (IOException e){
            e.printStackTrace();
        }

        return drawable;
    }

    public static Bitmap getBitmap(String imageFile, int length) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageFile, opts);
        int ins = computeSampleSize(opts, -1, length);
        opts.inSampleSize = ins;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        try {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile, opts);
            return bmp;
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}
