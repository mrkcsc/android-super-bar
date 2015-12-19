package com.miguelgaeta.android_super_bar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.miguelgaeta.super_bar.SuperBar;

public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_activity);

        ((SuperBar)findViewById(R.id.super_bar)).getConfig().setOnSelectionMoved(new SuperBar.OnSelectionMoved() {

            @Override
            public void onSelectionMoved(float value, float maxValue, float minValue, SuperBar superBar) {

                Log.e("Super Bar", "value: " + value);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ((SuperBar)findViewById(R.id.super_bar)).getConfig().setBarValue(3000, 90, 0);
            }
        }, 5000);
    }
}
