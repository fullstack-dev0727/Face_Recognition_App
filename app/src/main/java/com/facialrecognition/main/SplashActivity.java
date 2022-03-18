package com.facialrecognition.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.facialrecognition.utils.Constants;
import com.tzutalin.dlib.PeopleDet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SplashActivity extends Activity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        Display dispDefault = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Constants.setWidth(this, dispDefault.getWidth());
        Constants.setHeight(this, dispDefault.getHeight());
        Constants.setDensity(this, metrics.density);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        InitTask initTask = new InitTask();
        initTask.execute();
    }

    private class InitTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            String filePath = copyAssetToSdcard();
            Constants.peopleDet = new PeopleDet(filePath);
            return null;
        }
        @Override
        protected void onPostExecute(String rets) {
            super.onPostExecute(rets);
            progressBar.setVisibility(View.GONE);
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
            SplashActivity.this.finish();
        }
    }
    public String copyAssetToSdcard() {
        String fileName = "shape_predictor_68_face_landmarks.dat";
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fileName);
            File outFile = new File(getExternalFilesDir(null), fileName);
            if (outFile.exists())
                return outFile.getAbsolutePath();
            out = new FileOutputStream(outFile);
            copyfile(in, out);
            return outFile.getAbsolutePath();
        } catch (Exception e) {}
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {}
            }
        }
        return null;
    }
    private void copyfile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
