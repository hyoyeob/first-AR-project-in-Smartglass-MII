package com.supertester.PBV_MII.project_v2.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;

public class QRCodeActivity extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UI_control();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView title_view = new TextView(this);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        title_view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));

        title_view.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        imageView.setBackgroundColor(Color.parseColor("#000000"));
        title_view.setPadding(100, 100, 100, 30);
        title_view.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        title_view.setTextColor(Color.parseColor("#00cc00"));

        title_view.setTextSize(24);
        title_view.setText("Scan your login code.");

        this.addContentView(imageView, layoutParams);
        this.addContentView(title_view, layoutParams);
    }

    private void UI_control() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
