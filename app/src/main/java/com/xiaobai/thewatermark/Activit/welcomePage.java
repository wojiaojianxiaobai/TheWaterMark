package com.xiaobai.thewatermark.Activit;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiaobai.thewatermark.R;

import java.util.Timer;
import java.util.TimerTask;

public class welcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init(){
        Timer timer=new Timer();

        TimerTask task=new TimerTask(){
          @Override
          public void run(){
              Intent intent=new Intent(welcomePage.this,LoginActivity.class);
              startActivity(intent);
              welcomePage.this.finish();
          }
        };
        timer.schedule(task,3000);
    }
}
