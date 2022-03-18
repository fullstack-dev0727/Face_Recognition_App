package com.facialrecognition.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainMenuActivity extends Activity implements View.OnClickListener {

    private Button pickShadeButton;
    private Button howToUseButton;
    private Button aboutButton;
    private Button realWorldButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mainmenu);
        initView();
        setListener();
        initData();
    }

    public void initView() {
        pickShadeButton = (Button) findViewById(R.id.pickShadeButton);
        howToUseButton = (Button) findViewById(R.id.howToUseButton);
        aboutButton = (Button) findViewById(R.id.aboutButton);
        realWorldButton = (Button) findViewById(R.id.realWorldButton);
    }

    public void setListener() {
        pickShadeButton.setOnClickListener(this);
        howToUseButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        realWorldButton.setOnClickListener(this);
    }

    public void initData() {

    }

    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.pickShadeButton: {
                startActivity(new Intent(this, ChoosePhotoActivity.class));
                finish();
            }
            break;
            case R.id.howToUseButton: {

            }
            break;
            case R.id.aboutButton: {

            }
            break;
            case R.id.realWorldButton: {

            }
            break;
        }
    }
}
