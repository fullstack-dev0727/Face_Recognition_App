package com.facialrecognition.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.tzutalin.dlib.PeopleDet;
import com.tzutalin.dlib.VisionDetPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by administrator on 2/25/16.
 */
public class Constants {
    public static final String packageName = "com.facialrecognition.main";

    public static PeopleDet peopleDet = null;
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_PICK = 2;
    public static final int REQUEST_CROP = 3;
    public static ArrayList<ProductInfo> products;

    public static void setWidth(Context context, int width) {
        SharedPreferences.Editor editor = context.getSharedPreferences(packageName, 0).edit();
        editor.putInt("width", width);
        editor.commit();
    }

    public static int getWidth(Context context) {
        SharedPreferences pref = context.getSharedPreferences(packageName, 0);
        return pref.getInt("width", 720);

    }

    public static void setHeight(Context context, int height) {
        SharedPreferences.Editor editor = context.getSharedPreferences(packageName, 0).edit();
        editor.putInt("height", height);
        editor.commit();
    }

    public static int getHeight(Context context) {
        SharedPreferences pref = context.getSharedPreferences(packageName, 0);
        return pref.getInt("height", 1280);

    }

    public static void setDensity(Context context, float density) {
        SharedPreferences.Editor editor = context.getSharedPreferences(packageName, 0).edit();
        editor.putFloat("density", density);
        editor.commit();
    }

    public static float getDensity(Context context) {
        SharedPreferences pref = context.getSharedPreferences(packageName, 0);
        return pref.getFloat("density", 2.0f);

    }
    public static Bitmap getBitmapWithRect(Bitmap entireBitmap, Rect rect) {
        Bitmap croppedBitmap = Bitmap.createBitmap(
                entireBitmap,
                rect.left,
                rect.top,
                rect.width(),
                rect.height()
        );
        return croppedBitmap;
    }
    public static Rect getRectFromPoints(ArrayList<VisionDetPoint> landmarks) {
        if (landmarks != null && landmarks.size() > 0) {
            VisionDetPoint point = landmarks.get(0);
            int minX = point.getX();
            int maxX = point.getX();
            int minY = point.getY();
            int maxY = point.getY();
            for (int i = 1; i < landmarks.size(); i++) {
                VisionDetPoint iPoint = landmarks.get(i);
                if (minX > iPoint.getX())
                    minX = iPoint.getX();
                if (minY > iPoint.getY())
                    minY = iPoint.getY();
                if (maxX < iPoint.getX())
                    maxX = iPoint.getX();
                if (maxY < iPoint.getY())
                    maxY = iPoint.getY();
            }
            return new Rect(minX, minY, maxX, maxY);
        }
        return null;
    }
    public static int calcAverageColors(int color1, int color2, int color3) {
        int red1 = (color1 >> 16) & 0xFF; // Color.red
        int green1 = (color1 >> 8) & 0xFF; // Color.greed
        int blue1 = (color1 & 0xFF); // Color.blue

        int red2 = (color2 >> 16) & 0xFF; // Color.red
        int green2 = (color2 >> 8) & 0xFF; // Color.greed
        int blue2 = (color2 & 0xFF); // Color.blue

        int red3 = (color3 >> 16) & 0xFF; // Color.red
        int green3 = (color3 >> 8) & 0xFF; // Color.greed
        int blue3 = (color3 & 0xFF); // Color.blue

        int averageColor = Color.argb(
                255,
                (red1 + red2 + red3) / 3,
                (green1 + green2 + green3) / 3,
                (blue1 + blue2 + blue3) / 3);
        return averageColor;
    }
    public static int getDominantColor(Bitmap bitmap) {
        if (null == bitmap) return Color.TRANSPARENT;

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int alphaBucket = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        int pixelCount = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[pixelCount];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = 0, h = bitmap.getHeight(); y < h; y++)
        {
            for (int x = 0, w = bitmap.getWidth(); x < w; x++)
            {
                int color = pixels[x + y * w]; // x + y * width
                redBucket += (color >> 16) & 0xFF; // Color.red
                greenBucket += (color >> 8) & 0xFF; // Color.greed
                blueBucket += (color & 0xFF); // Color.blue
                if (hasAlpha) alphaBucket += (color >>> 24); // Color.alpha
            }
        }
        int averageColor = Color.argb(
                (hasAlpha) ? (alphaBucket / pixelCount) : 255,
                redBucket / pixelCount,
                greenBucket / pixelCount,
                blueBucket / pixelCount);
        return averageColor;
    }
    public static ProductInfo getClosestProduct(ArrayList<ProductInfo> productList, int averageColor) {
        double minDist = 1000;
        ProductInfo closestProduct = new ProductInfo();
        int red = (averageColor >> 16) & 0xFF; // Color.red
        int green = (averageColor >> 8) & 0xFF; // Color.greed
        int blue = (averageColor & 0xFF); // Color.blue
        for (int i = 0; i < productList.size(); i++) {
            ProductInfo info = productList.get(i);
            double dist = Math.sqrt(Math.pow(red - info.red, 2.0f) + Math.pow(green - info.green, 2.0f) + Math.pow(blue - info.blue, 2.0f));
            if (minDist > dist) {
                minDist = dist;
                closestProduct = info;
            }

        }
        return closestProduct;
    }

    public static Bitmap filledImageFrom(Bitmap src, int colorToReplace) {
        int replaceRed = (colorToReplace >> 16) & 0xFF; // Color.red
        int replaceGreen = (colorToReplace >> 8) & 0xFF; // Color.greed
        int replaceBlue = (colorToReplace & 0xFF); // Color.blue

        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int index = y * width + x;
                pixel = pixels[index];
                A = Color.alpha(pixel);
                pixels[index] = Color.argb(A,replaceRed,replaceGreen,replaceBlue);
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
    public static Bitmap addMaskToImage(Bitmap bmp1, Bitmap bmp2, Rect rect) {
        Canvas canvas = new Canvas(bmp1);
        canvas.drawBitmap(bmp2, null, rect, new Paint());
        return bmp1;
    }

    public static Bitmap maskBitmap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap result = Bitmap.createBitmap(bmp2.getWidth(), bmp2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawBitmap(bmp1, 0, 0, null);
        mCanvas.drawBitmap(bmp2, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }

    public static Rect increaseRect(Rect originRect, float xRate, float yRate) {
        int width = originRect.width();
        int height = originRect.height();
        int x = originRect.left;
        int y = originRect.top;
        return new Rect((int) (x - width * xRate), (int) (y - height * yRate), (int) (x + width * xRate + width), (int) (y + height * yRate + height));
    }
    public static String readPlistFromAssets(Context context) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br=null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open("products.plist")));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }
}
