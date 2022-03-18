package com.facialrecognition.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facialrecognition.utils.ActionSheet;
import com.facialrecognition.utils.Constants;
import com.facialrecognition.utils.ParsePlistParser;
import com.facialrecognition.utils.ProductInfo;
import com.tzutalin.dlib.VisionDetPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

public class MainActivity extends FragmentActivity implements View.OnClickListener, ActionSheet.ActionSheetListener, View.OnTouchListener {
    private Button                          selectImageButton;
    private Button                          detectFaceButton;
    private Button                          tempButton;
    private Button                          averageButton;
    private ImageView                       photoImageView;
    private ImageView                       maskImageView;
    private ImageView                       lipStickImageView;
    private ImageView                       blushImageView;
    private ImageView                       concealerImageView;
    private ImageView                       cheekSampleImageView;
    private ImageView                       lipStrongImageView;
    private ImageView                       lipLightImageView;
    private ImageView                       whiteSampleImageView;
    private ImageView                       productImageView;
    private TextView                        productNameTextView;
    private TextView                        percentTextView;
    private SeekBar                         percentSeekBar;
    private TextView                        lipStickAlphaTextView;
    private SeekBar                         lipStickSeekBar;
    private ProgressDialog                  progressDialog;
    private String                          filePath;
    private ArrayList<VisionDetPoint>       landmarkPoints;

    private Handler                         handler;
    private int[]                           foreHeadPoints;
    private int[]                           lipStrongPoints;
    private int[]                           lipLightPoints;
    private int[]                           nosePoints;
    private int[]                           leftEyePoints;
    private int[]                           rightEyePoints;
    private int[]                           leftEyeBrowPoints;
    private int[]                           rightEyeBrowPoints;
    private int[]                           mouthPoints;
    private int[]                           topLipArray;
    private int[]                           bottomLipArray;
    private int[]                           leftConcealerArray;
    private int[]                           rightConcealerArray;
    private int                             whiteBalancedColor = 0;
    private RelativeLayout                  contentRelativeLayout;
    private ImageView                       cursorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case 0: {
                        showKeepActionSheet();
                    }
                    break;
                    case 1: {
                        showPictureActionSheet();
                    }
                    break;
                    default:
                        break;
                }
            }
        };

        initView();
        setListener();
        initData();
    }

    public void initView() {
        selectImageButton = (Button) findViewById(R.id.selectImageButton);
        detectFaceButton = (Button) findViewById(R.id.detectFaceButton);
        tempButton = (Button) findViewById(R.id.tempButton);
        averageButton = (Button) findViewById(R.id.averageButton);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        maskImageView = (ImageView) findViewById(R.id.maskImageView);
        lipStickImageView = (ImageView) findViewById(R.id.lipStickImageView);
        blushImageView = (ImageView) findViewById(R.id.blushImageView);
        concealerImageView = (ImageView) findViewById(R.id.concealerImageView);
        cheekSampleImageView = (ImageView) findViewById(R.id.cheekSampleImageView);
        lipStrongImageView = (ImageView) findViewById(R.id.lipStrongImageView);
        lipLightImageView = (ImageView) findViewById(R.id.lipLightImageView);
        whiteSampleImageView = (ImageView) findViewById(R.id.whiteSampleImageView);
        productImageView = (ImageView) findViewById(R.id.productImageView);
        productNameTextView = (TextView) findViewById(R.id.productNameTextView);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentSeekBar = (SeekBar) findViewById(R.id.percentSeekBar);
        lipStickAlphaTextView = (TextView) findViewById(R.id.lipStickAlphaTextView);
        lipStickSeekBar = (SeekBar) findViewById(R.id.lipStickSeekBar);
        contentRelativeLayout = (RelativeLayout) findViewById(R.id.contentRelativeLayout);
        cursorImageView = (ImageView) findViewById(R.id.cursorImageView);
    }

    public void setListener() {
        contentRelativeLayout.setOnTouchListener(this);
        selectImageButton.setOnClickListener(this);
        detectFaceButton.setOnClickListener(this);
        tempButton.setOnClickListener(this);
        averageButton.setOnClickListener(this);
        percentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float percent = progress / 100.0f;
                blushImageView.setAlpha(percent);
                percentTextView.setText(String.format("%.2f", percent));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lipStickSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float percent = progress / 100.0f;
                concealerImageView.setAlpha(percent);
                lipStickAlphaTextView.setText(String.format("%.2f", percent));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void initData() {
        setTheme(R.style.ActionSheetStyleIOS7);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        handler.sendEmptyMessage(1);
        foreHeadPoints = new int[]{ 21, 22 };
        lipStrongPoints = new int[]{ 51, 61, 62 };
        lipLightPoints = new int[]{ 56, 58, 67, 65 };
        nosePoints = new int[]{ 29, 31, 33, 35 };
        leftEyePoints = new int[]{ 36, 37, 38, 39, 40, 41 };
        leftEyeBrowPoints = new int[]{ 17, 18, 19, 20, 21 };
        rightEyePoints = new int[]{ 42, 43, 44, 45, 46, 47 };
        rightEyeBrowPoints = new int[]{ 22, 23, 24, 25, 26 };
        mouthPoints = new int[]{ 48, 50, 54, 57 };
        topLipArray = new int[]{ 48, 49, 50, 51, 52, 53, 54, 64, 63, 62, 61, 60};
        bottomLipArray = new int[]{ 48, 60, 67, 66, 65, 64, 54, 55, 56, 57, 58, 59};
        leftConcealerArray = new int[]{ 36, 41, 40, 39 };
        rightConcealerArray = new int[]{ 42, 47, 46, 45 };

        String xml = Constants.readPlistFromAssets(this);
        ParsePlistParser pp = new ParsePlistParser();
        Constants.products = pp.parsePlist(xml);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        // Handle event here instead of using an OnTouchListener
        return false;
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getX();
        final int Y = (int) event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                cursorImageView.setX(X);
                cursorImageView.setY(Y);
                showWhiteSampleColor(X, Y);
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                cursorImageView.setX(X);
                cursorImageView.setY(Y);
                showWhiteSampleColor(X, Y);
                break;
        }
        cursorImageView.invalidate();
        return true;
    }
    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void showWhiteSampleColor(int x, int y) {
        Bitmap bitmap = screenShot(contentRelativeLayout);
        if (bitmap.getWidth() > x && x >= 0 && bitmap.getHeight() > y && y >= 0) {
            int pixel = bitmap.getPixel(x, y);

            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            whiteBalancedColor = Color.rgb(red, green, blue);
            whiteSampleImageView.setImageDrawable(new ColorDrawable(whiteBalancedColor));
        }
        bitmap.recycle();
    }
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.selectImageButton: {
                showPictureActionSheet();
            }
            break;
            case R.id.detectFaceButton: {
                detectFaceFunc();
            }
            break;
            case R.id.tempButton: {
                //tempFunc();
                whiteBalanceFunc();
            }
            break;
            case R.id.averageButton: {
                averageFoundationLipStick();
            }
            break;
            default:
                break;
        }
    }
    public void whiteBalanceFunc() {
        if (whiteBalancedColor != 0) {
            int red = (whiteBalancedColor >> 16) & 0xFF; // Color.red
            int green = (whiteBalancedColor >> 8) & 0xFF; // Color.greed
            int blue = (whiteBalancedColor & 0xFF); // Color.blue
            float x = 1.54924f * green - 0.95641f * blue - 0.14282f * red;
            float y = 1.57837f * green - 0.73191f * blue - 0.32466f * red;
            float z = 0.77073f * green + 0.56332f * blue - 0.68202f * red;
            float chromaticityX = x / (x + y + z);
            float chromaticityY = y / (x + y + z);
            float featureVal = (chromaticityX - 0.3320f) / (0.1858f - chromaticityY);
            double cct = 449 * Math.pow(featureVal, 3) + 3525 * Math.pow(featureVal, 2) + 6823.3f * featureVal + 5520.33f;

            Bitmap bitmap = ((BitmapDrawable)photoImageView.getDrawable()).getBitmap();
            bitmap = applyWhiteBalance(bitmap, (int) cct);
            photoImageView.setImageBitmap(bitmap);

            showWhiteSampleColor((int)cursorImageView.getX(), (int)cursorImageView.getY());
        }
    }
    public Bitmap applyWhiteBalance(Bitmap src, int fromValue) {
        GPUImage gpuImage = new GPUImage(this);
        gpuImage.setFilter(new GPUImageWhiteBalanceFilter(fromValue, 0));
        return gpuImage.getBitmapWithFilterApplied(src);
    }
    public void averageFoundationLipStick() {
        if (landmarkPoints == null || landmarkPoints.size() == 0)
            return;
        int averageColor = showColors(foreHeadPoints);
        cheekSampleImageView.setImageDrawable(new ColorDrawable(averageColor));

        int topLipColor = showColors(lipStrongPoints);
        int bottomLipColor = showColors(lipLightPoints);
        lipStrongImageView.setImageDrawable(new ColorDrawable(topLipColor));
        lipLightImageView.setImageDrawable(new ColorDrawable(bottomLipColor));

        ProductInfo productInfo = Constants.getClosestProduct(Constants.products, averageColor);
        productNameTextView.setText(productInfo.name);
        productImageView.setImageDrawable(new ColorDrawable(productInfo.getColor()));

        showTransparencyMask(BitmapFactory.decodeResource(getResources(), R.drawable.blush3), productInfo.getColor());

        Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), conf);
        drawLipStick(new Canvas(bitmap), topLipArray, topLipColor);
        drawLipStick(new Canvas(bitmap), bottomLipArray, bottomLipColor);
        lipStickImageView.setImageBitmap(getBlurBitmap(bitmap));
        lipStickImageView.setAlpha(0.5f);

        drawBlush();

        Bitmap concealerBitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), conf);
        drawConcealer(new Canvas(concealerBitmap), leftConcealerArray, Color.argb(25, 255, 0, 0));
        drawConcealer(new Canvas(concealerBitmap), rightConcealerArray, Color.argb(25, 255, 0, 0));
        concealerImageView.setImageBitmap(getBlurBitmap(concealerBitmap));
        concealerImageView.setAlpha(0.5f);
        lipStickSeekBar.setProgress(50);
        selectedBitmap.recycle();
    }
    public Bitmap getBlurBitmap(Bitmap bitmap) {
        GPUImage gpuImage = new GPUImage(this);
        gpuImage.setFilter(new GPUImageBoxBlurFilter(1.0f));
        return gpuImage.getBitmapWithFilterApplied(bitmap);
    }
    public void drawLipStick(Canvas canvas, int[] pointArray, int lipColor) {
        ArrayList<Point> lipArray = new ArrayList<>();
        for (int i = 0; i < pointArray.length; i++) {
            VisionDetPoint item = landmarkPoints.get(pointArray[i]);
            lipArray.add(new Point(item.getX(), item.getY()));
        }

        if (lipArray.size() < 2) {
            return;
        }

        // paint
        Paint polyPaint = new Paint();
        polyPaint.setColor(lipColor);
        polyPaint.setStyle(Paint.Style.FILL);

        // path
        Path polyPath = new Path();
        polyPath.moveTo(lipArray.get(0).x, lipArray.get(0).y);
        int i, len;
        len = lipArray.size();
        for (i = 0; i < len; i++) {
            polyPath.lineTo(lipArray.get(i).x, lipArray.get(i).y);
        }
        polyPath.lineTo(lipArray.get(0).x, lipArray.get(0).y);

        // draw
        canvas.drawPath(polyPath, polyPaint);
    }
    public int showColors(int[] pointArray) {
        ArrayList<VisionDetPoint> cheekArray = new ArrayList<>();
        for (int i = 0; i < pointArray.length; i++) {
            cheekArray.add(landmarkPoints.get(pointArray[i]));
        }
        if (pointArray.length == 2 && pointArray[0] == 21 && pointArray[1] == 22) {
            cheekArray.add(new VisionDetPoint(landmarkPoints.get(21).getX(), landmarkPoints.get(21).getY() - landmarkPoints.get(29).getY() + landmarkPoints.get(27).getY()));
        }
        Rect cheekRect = Constants.getRectFromPoints(cheekArray);
        Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);
        Bitmap cheekBitmap = Constants.getBitmapWithRect(selectedBitmap, cheekRect);
        int color = Constants.getDominantColor(cheekBitmap);
        selectedBitmap.recycle();
        cheekBitmap.recycle();
        return color;
    }

    public void tempFunc() {
        showTransparencyMask(BitmapFactory.decodeResource(getResources(), R.drawable.blush31), Color.rgb(255, 0, 0));
    }
    public void drawBlush() {
        Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);

        //blushMask = Constants.filledImageFrom(blushMask, Color.rgb(0, 0, 255));
        Bitmap blankBitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), Config.ARGB_8888);

        // left cheek blush
        Bitmap blushLeftMask = BitmapFactory.decodeResource(getResources(), R.drawable.cheek_blush_left);
        Rect leftCheekBlushRect = new Rect(landmarkPoints.get(2).getX(), landmarkPoints.get(38).getY(), landmarkPoints.get(38).getX(), landmarkPoints.get(31).getY());
        Bitmap blushImage = Constants.addMaskToImage(blankBitmap, blushLeftMask, leftCheekBlushRect);

        // right cheek blush
        Bitmap blushRightMask = BitmapFactory.decodeResource(getResources(), R.drawable.cheek_blush_right);
        Rect rightCheekBlushRect = new Rect(landmarkPoints.get(43).getX(), landmarkPoints.get(43).getY(), landmarkPoints.get(14).getX(), landmarkPoints.get(35).getY());
        blushImage = Constants.addMaskToImage(blushImage, blushRightMask, rightCheekBlushRect);

        blushImageView.setImageBitmap(blushImage);

        selectedBitmap.recycle();
        blushRightMask.recycle();
        blushLeftMask.recycle();
    }
    public void drawConcealer(Canvas canvas, int[] pointArray, int concealerColor) {
        int width = Math.abs(landmarkPoints.get(pointArray[0]).getX() - landmarkPoints.get(pointArray[pointArray.length - 1]).getX());

        ArrayList<Point> lipArray = new ArrayList<>();
        for (int i = 0; i < pointArray.length; i++) {
            VisionDetPoint item = landmarkPoints.get(pointArray[i]);
            lipArray.add(new Point(item.getX(), item.getY()));
        }
        for (int i = pointArray.length - 1; i >= 0; i--) {
            VisionDetPoint item = landmarkPoints.get(pointArray[i]);
            if (i == 0 || i == pointArray.length - 1)
                lipArray.add(new Point(item.getX(), item.getY() + width / 4));
            else
                lipArray.add(new Point(item.getX(), item.getY() + width / 2));
        }
        if (lipArray.size() < 2) {
            return;
        }

        // paint
        Paint polyPaint = new Paint();
        polyPaint.setColor(concealerColor);
        polyPaint.setStyle(Paint.Style.FILL);

        // path
        Path polyPath = new Path();
        polyPath.moveTo(lipArray.get(0).x, lipArray.get(0).y);
        int i, len;
        len = lipArray.size();
        for (i = 0; i < len; i++) {
            polyPath.lineTo(lipArray.get(i).x, lipArray.get(i).y);
        }
        polyPath.lineTo(lipArray.get(0).x, lipArray.get(0).y);

        // draw
        canvas.drawPath(polyPath, polyPaint);
    }
    public void showTransparencyMask(Bitmap fullFaceImage, int tintColor) {
        if (landmarkPoints == null || landmarkPoints.size() == 0)
            return;
        Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);
        Point leftEyeBrowPoint = new Point(landmarkPoints.get(18).getX(), landmarkPoints.get(18).getY());
        Point noseTipPoint = new Point(landmarkPoints.get(30).getX(), landmarkPoints.get(30).getY());
        Point chinPoint = new Point(landmarkPoints.get(8).getX(), landmarkPoints.get(8).getY());
        Point leftContourPoint = new Point(landmarkPoints.get(0).getX(), landmarkPoints.get(0).getY());
        Point rightContourPoint = new Point(landmarkPoints.get(16).getX(), landmarkPoints.get(16).getY());
        Rect fullRect = new Rect(leftContourPoint.x, 2 * leftEyeBrowPoint.y - noseTipPoint.y, rightContourPoint.x, (int)(0.2f * (noseTipPoint.y - leftEyeBrowPoint.y) + chinPoint.y));
        fullFaceImage = Constants.filledImageFrom(fullFaceImage, tintColor);
        Bitmap blankBitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), Config.ARGB_8888);
        Bitmap blackBitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), Config.ARGB_8888);

        Bitmap maskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        Bitmap fullImage = Constants.addMaskToImage(blankBitmap, fullFaceImage, fullRect);

        // nose
        ArrayList<VisionDetPoint> noseArray = new ArrayList<>();
        for (int i = 0; i < nosePoints.length; i++) {
            noseArray.add(landmarkPoints.get(nosePoints[i]));
        }
        Rect noseRect = Constants.getRectFromPoints(noseArray);
        noseRect = Constants.increaseRect(noseRect, 0.3f, 0.3f);
        Bitmap entireBitmap = Constants.addMaskToImage(blackBitmap, maskBitmap, noseRect);

        // left eye
        ArrayList<VisionDetPoint> leftEyeArray = new ArrayList<>();
        for (int i = 0; i < leftEyePoints.length; i++) {
            leftEyeArray.add(landmarkPoints.get(leftEyePoints[i]));
        }
        Rect leftEyeRect = Constants.getRectFromPoints(leftEyeArray);
        leftEyeRect = Constants.increaseRect(leftEyeRect, 0.3f, 0.8f);
        entireBitmap = Constants.addMaskToImage(entireBitmap, maskBitmap, leftEyeRect);

        // right eye
        ArrayList<VisionDetPoint> rightEyeArray = new ArrayList<>();
        for (int i = 0; i < rightEyePoints.length; i++) {
            rightEyeArray.add(landmarkPoints.get(rightEyePoints[i]));
        }
        Rect rightEyeRect = Constants.getRectFromPoints(rightEyeArray);
        rightEyeRect = Constants.increaseRect(rightEyeRect, 0.3f, 0.8f);
        entireBitmap = Constants.addMaskToImage(entireBitmap, maskBitmap, rightEyeRect);


        // left eye brow
        ArrayList<VisionDetPoint> leftEyeBrowArray = new ArrayList<>();
        for (int i = 0; i < leftEyeBrowPoints.length; i++) {
            leftEyeBrowArray.add(landmarkPoints.get(leftEyeBrowPoints[i]));
        }
        Rect leftEyeBrowRect = Constants.getRectFromPoints(leftEyeBrowArray);
        leftEyeBrowRect = Constants.increaseRect(leftEyeBrowRect, 0.1f, 0.25f);
        entireBitmap = Constants.addMaskToImage(entireBitmap, maskBitmap, leftEyeBrowRect);

        // right eye brow
        ArrayList<VisionDetPoint> rightEyeBrowArray = new ArrayList<>();
        for (int i = 0; i < rightEyeBrowPoints.length; i++) {
            rightEyeBrowArray.add(landmarkPoints.get(rightEyeBrowPoints[i]));
        }
        Rect rightEyeBrowRect = Constants.getRectFromPoints(rightEyeBrowArray);
        rightEyeBrowRect = Constants.increaseRect(rightEyeBrowRect, 0.1f, 0.25f);
        entireBitmap = Constants.addMaskToImage(entireBitmap, maskBitmap, rightEyeBrowRect);

        // mouth
        ArrayList<VisionDetPoint> mouthArray = new ArrayList<>();
        for (int i = 0; i < mouthPoints.length; i++) {
            mouthArray.add(landmarkPoints.get(mouthPoints[i]));
        }
        Rect mouthRect = Constants.getRectFromPoints(mouthArray);
        mouthRect = Constants.increaseRect(mouthRect, 0.1f, 0.25f);
        entireBitmap = Constants.addMaskToImage(entireBitmap, maskBitmap, mouthRect);

        fullImage = Constants.maskBitmap(fullImage, entireBitmap);
        maskImageView.setImageBitmap(fullImage);

        // release bitmap
        selectedBitmap.recycle();
        maskBitmap.recycle();
    }
    public void detectFaceFunc() {
        if (filePath == null) {
            return;
        }
        DetectTask initTask = new DetectTask();
        initTask.execute();
    }
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private class DetectTask extends AsyncTask<String, Void, String> {
        String result = "No face";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            if (Constants.peopleDet == null)
                return "";
            try {
                landmarkPoints = Constants.peopleDet.detFace(filePath);
            } catch (Exception e) {
                return "";
            }

            if (landmarkPoints != null && landmarkPoints.size() > 0) {
                result = "Success";
            }
            return "";
        }
        @Override
        protected void onPostExecute(String rets) {
            super.onPostExecute(rets);
            dismissProgressDialog();
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            showLandmarks();
        }
    }
    public void showLandmarks() {
        if (landmarkPoints != null && landmarkPoints.size() > 0) {
            Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);
            Bitmap canvasBitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(canvasBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawBitmap(canvasBitmap, new Matrix(), null);

            for (int i = 0;i < landmarkPoints.size(); i++) {
                VisionDetPoint point = landmarkPoints.get(i);
                canvas.drawCircle(point.getX(), point.getY(), 3, paint);
            }
            maskImageView.setImageBitmap(canvasBitmap);
        }
    }

    public void showPictureActionSheet() {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setTitle("Select image from...")
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("Camera", "Image Gallery")
                .setCancelableOnTouchOutside(true).setListener(this).setTag("0").show();
    }
    public void showKeepActionSheet() {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setTitle(null)
                .setOtherButtonTitles("Keep")
                .setCancelButtonTitle("Retake")
                .setCancelableOnTouchOutside(true).setListener(this).setTag("1").show();
    }
    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        String tag = actionSheet.getTag();
        switch (index) {
            case 0: {
                if (tag != null && tag.equals("0")) {
                    File directory = new File(android.os.Environment.getExternalStorageDirectory(), "FacialRecognition");
                    if (!directory.isDirectory() || !directory.exists())
                        directory.mkdir();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Date date = new Date();
                    File f = new File(directory, date.getTime() + ".jpg");
                    filePath = f.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, Constants.REQUEST_CAMERA);
                } else {

                }

            }
            break;
            case 1: {
                if (tag != null && tag.equals("0")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.REQUEST_PICK);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
            break;
        }
    }
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancle) {

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CAMERA) {
                Matrix matrix = new Matrix();
                matrix.postRotate(getImageOrientation(filePath));
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                OutputStream fOut = null;
                File file = new File(filePath);
                try {
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                photoImageView.setImageURI(Uri.fromFile(file));

            } else if (requestCode == Constants.REQUEST_PICK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();

                photoImageView.setImageURI(selectedImage);

            }
            maskImageView.setImageBitmap(null);
            lipStickImageView.setImageBitmap(null);
            blushImageView.setImageBitmap(null);
            handler.sendEmptyMessage(0);
        }
    }
    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }
}
