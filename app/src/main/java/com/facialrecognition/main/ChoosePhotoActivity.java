package com.facialrecognition.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ChoosePhotoActivity extends Activity implements View.OnClickListener {

    private Button cameraButton;
    private Button galleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choosephoto);
        initView();
        setListener();
        initData();
    }

    public void initView() {
        cameraButton = (Button) findViewById(R.id.cameraButton);
        galleryButton = (Button) findViewById(R.id.galleryButton);
    }

    public void setListener() {
        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
    }

    public void initData() {

    }

    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.cameraButton: {
                Intent intent = new Intent(this, AstarteMainActivity.class);
                intent.putExtra("isCameraChoose", true);
                startActivity(intent);
                finish();
            }
            break;
            case R.id.galleryButton: {
                Intent intent = new Intent(this, AstarteMainActivity.class);
                intent.putExtra("isCameraChoose", false);
                startActivity(intent);
                finish();
            }
            break;
        }
    }
}
