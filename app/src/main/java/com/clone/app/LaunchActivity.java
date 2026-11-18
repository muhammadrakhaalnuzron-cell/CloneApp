package com.clone.app;

import android.app.Activity;
import android.os.Bundle;
import top.niunaijun.blackbox.BlackBoxCore;

public class LaunchActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        String pkg = getIntent().getStringExtra("pkg");
        int uid = getIntent().getIntExtra("uid", 0);
        if (pkg != null) BlackBoxCore.get().launchApk(pkg, uid);
        finish();
    }
}
