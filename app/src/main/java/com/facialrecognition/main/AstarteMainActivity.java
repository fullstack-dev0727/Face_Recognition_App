package com.facialrecognition.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facialrecognition.utils.Constants;
import com.tzutalin.dlib.VisionDetPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class AstarteMainActivity extends Activity implements View.OnClickListener {

    private Button                      menuButton;
    private Button                      cartButton;
    private Button                      buyButton;
    private Button                      shareButton;
    private Button                      wearingButton;
    private Button                      foundationsButton;
    private Button                      concealersButton;
    private Button                      lipsticksButton;
    private Button                      blushButton;
    private Button                      howToUseButton;
    private Button                      retakePhotoButton;
    private Button                      galleryButton;
    private Button                      profileButton;
    private ImageView                   photoImageView;
    private ImageView                   maskImageView;
    private ImageView                   lipStickImageView;
    private ImageView                   blushImageView;
    private ImageView                   concealerImageView;
    private RelativeLayout              rootRelativeLayout;

    private boolean                     isCameraChoose = false;
    private String                      filePath;
    private ProgressDialog              progressDialog;
    private ArrayList<VisionDetPoint>   landmarkPoints;
    private LinearLayout                leftMenuLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_astartemain);

        initView();
        setListener();
        initData();
    }

    public void initView() {
        menuButton = (Button) findViewById(R.id.menuButton);
        cartButton = (Button) findViewById(R.id.cartButton);
        buyButton = (Button) findViewById(R.id.buyButton);
        shareButton = (Button) findViewById(R.id.shareButton);
        wearingButton = (Button) findViewById(R.id.wearingButton);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        maskImageView = (ImageView) findViewById(R.id.maskImageView);
        lipStickImageView = (ImageView) findViewById(R.id.lipStickImageView);
        blushImageView = (ImageView) findViewById(R.id.blushImageView);
        concealerImageView = (ImageView) findViewById(R.id.concealerImageView);
        rootRelativeLayout = (RelativeLayout) findViewById(R.id.rootRelativeLayout);

        LayoutInflater inflater = LayoutInflater.from(this);
        leftMenuLinearLayout = (LinearLayout) inflater.inflate(R.layout.leftmenu, null);
        rootRelativeLayout.addView(leftMenuLinearLayout);
        RelativeLayout.LayoutParams leftMenuParam = (RelativeLayout.LayoutParams) leftMenuLinearLayout.getLayoutParams();
        leftMenuParam.setMargins(-(int)(200 * Constants.getDensity(AstarteMainActivity.this)), (int)(60 * Constants.getDensity(this)), 0, 0);
        leftMenuLinearLayout.setLayoutParams(leftMenuParam);

        foundationsButton = (Button) leftMenuLinearLayout.findViewById(R.id.foundationsButton);
        concealersButton = (Button) leftMenuLinearLayout.findViewById(R.id.concealersButton);
        lipsticksButton = (Button) leftMenuLinearLayout.findViewById(R.id.lipsticksButton);
        blushButton = (Button) leftMenuLinearLayout.findViewById(R.id.blushButton);
        howToUseButton = (Button) leftMenuLinearLayout.findViewById(R.id.howToUseButton);
        retakePhotoButton = (Button) leftMenuLinearLayout.findViewById(R.id.retakePhotoButton);
        galleryButton = (Button) leftMenuLinearLayout.findViewById(R.id.galleryButton);
        profileButton = (Button) leftMenuLinearLayout.findViewById(R.id.profileButton);
    }

    public void setListener() {
        menuButton.setOnClickListener(this);
        cartButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        wearingButton.setOnClickListener(this);
        foundationsButton.setOnClickListener(this);
        concealersButton.setOnClickListener(this);
        lipsticksButton.setOnClickListener(this);
        blushButton.setOnClickListener(this);
        howToUseButton.setOnClickListener(this);
        retakePhotoButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
    }

    public void initData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        isCameraChoose = getIntent().getBooleanExtra("isCameraChoose", false);
        if (isCameraChoose) {
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
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.REQUEST_PICK);
        }
    }
    public void showLeftMenu() {
        TranslateAnimation anim = new TranslateAnimation(0, (int)(210 * Constants.getDensity(this)), 0, 0);
        anim.setDuration(500);
        anim.setFillAfter(false);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                leftMenuLinearLayout.clearAnimation();
                RelativeLayout.LayoutParams leftMenuParam = (RelativeLayout.LayoutParams) leftMenuLinearLayout.getLayoutParams();
                leftMenuParam.setMargins((int)(10 * Constants.getDensity(AstarteMainActivity.this)), (int)(60 * Constants.getDensity(AstarteMainActivity.this)), 0, 0);
                leftMenuLinearLayout.setLayoutParams(leftMenuParam);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        leftMenuLinearLayout.startAnimation(anim);
    }
    public void hideLeftMenu() {
        TranslateAnimation anim = new TranslateAnimation(0, -(int)(210 * Constants.getDensity(this)), 0, 0);
        anim.setDuration(500);
        anim.setFillAfter(false);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                leftMenuLinearLayout.clearAnimation();
                RelativeLayout.LayoutParams leftMenuParam = (RelativeLayout.LayoutParams) leftMenuLinearLayout.getLayoutParams();
                leftMenuParam.setMargins(-(int)(200 * Constants.getDensity(AstarteMainActivity.this)), (int)(60 * Constants.getDensity(AstarteMainActivity.this)), 0, 0);
                leftMenuLinearLayout.setLayoutParams(leftMenuParam);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        leftMenuLinearLayout.startAnimation(anim);
    }
    public boolean isShownLeftMenu() {
        RelativeLayout.LayoutParams leftMenuParam = (RelativeLayout.LayoutParams) leftMenuLinearLayout.getLayoutParams();
        if (leftMenuParam.leftMargin > 0)
            return true;
        return false;
    }
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.menuButton: {
                if (isShownLeftMenu())
                    hideLeftMenu();
                else
                    showLeftMenu();
            }
            break;
            case R.id.cartButton: {

            }
            break;
            case R.id.buyButton: {

            }
            break;
            case R.id.shareButton: {

            }
            break;
            case R.id.wearingButton: {

            }
            break;
            case R.id.foundationsButton: {

            }
            break;
            case R.id.concealersButton: {

            }
            break;
            case R.id.lipsticksButton: {

            }
            break;
            case R.id.blushButton: {

            }
            break;
            case R.id.retakePhotoButton: {

            }
            break;
            case R.id.galleryButton: {

            }
            break;
            case R.id.profileButton: {

            }
            break;
        }
    }

    public void detectFaceFunc() {
        if (filePath == null) {
            return;
        }
        DetectTask initTask = new DetectTask();
        initTask.execute();
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
            showLandmarks();
        }
    }
    public void showLandmarks() {
        if (landmarkPoints != null && landmarkPoints.size() > 0) {
            Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);
            Bitmap canvasBitmap = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
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
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
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
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
                photoImageView.setImageURI(selectedImage);
            }
            maskImageView.setImageBitmap(null);
            lipStickImageView.setImageBitmap(null);
            blushImageView.setImageBitmap(null);
            detectFaceFunc();
        }
    }
    public int getImageOrientation(String imagePath){
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
