package com.clone.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import java.util.*;
import top.niunaijun.blackbox.BlackBoxCore;

public class MainActivity extends Activity {
    private final List<ApplicationInfo> apps = new ArrayList<>();

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        PackageManager pm = getPackageManager();
        for (ApplicationInfo ai : pm.getInstalledApplications(0)) {
            if (pm.getLaunchIntentForPackage(ai.packageName) != null && !ai.packageName.equals(getPackageName())) apps.add(ai);
        }
        Collections.sort(apps, (x, y) -> pm.getApplicationLabel(x).toString().compareToIgnoreCase(pm.getApplicationLabel(y).toString()));
        ListView lv = new ListView(this);
        lv.setAdapter(new BaseAdapter() {
            public int getCount() { return apps.size(); }
            public Object getItem(int i) { return apps.get(i); }
            public long getItemId(int i) { return i; }
            public View getView(int i, View v, ViewGroup p) {
                TextView t = new TextView(MainActivity.this);
                t.setPadding(32, 28, 32, 28); t.setTextSize(17);
                t.setCompoundDrawablePadding(24);
                ApplicationInfo ai = apps.get(i);
                Drawable d = ai.loadIcon(pm);
                d.setBounds(0, 0, 96, 96);
                t.setCompoundDrawables(d, null, null, null);
                t.setText(ai.loadLabel(pm));
                return t;
            }
        });
        lv.setOnItemClickListener((a, v, pos, id) -> askCount(apps.get(pos)));
        setContentView(lv);
        setTitle("Pilih aplikasi untuk di-clone");
    }

    private void askCount(ApplicationInfo ai) {
        String[] opts = new String[10];
        for (int i = 0; i < 10; i++) opts[i] = (i + 1) + " clone";
        new AlertDialog.Builder(this).setTitle("Jumlah clone (maks 10)")
            .setItems(opts, (d, which) -> cloneApp(ai, which + 1)).show();
    }

    private void cloneApp(ApplicationInfo ai, int count) {
        PackageManager pm = getPackageManager();
        String label = ai.loadLabel(pm).toString();
        Bitmap icon = toBitmap(ai.loadIcon(pm));
        int ok = 0;
        for (int uid = 0; uid < count; uid++) {
            try {
                BlackBoxCore.get().installPackageAsUser(ai.packageName, uid);
                Intent li = new Intent(this, LaunchActivity.class)
                    .setAction(Intent.ACTION_VIEW)
                    .putExtra("pkg", ai.packageName).putExtra("uid", uid);
                ShortcutInfoCompat sc = new ShortcutInfoCompat.Builder(this, ai.packageName + "#" + uid)
                    .setShortLabel(label + " " + (uid + 1)).setIcon(IconCompat.createWithBitmap(icon))
                    .setIntent(li).build();
                ShortcutManagerCompat.requestPinShortcut(this, sc, null);
                ok++;
            } catch (Throwable t) {
                Toast.makeText(this, "Gagal clone #" + (uid + 1) + ": " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(this, ok + " clone dibuat. Konfirmasi tiap shortcut yang muncul.", Toast.LENGTH_LONG).show();
    }

    private Bitmap toBitmap(Drawable d) {
        Bitmap bm = Bitmap.createBitmap(192, 192, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        d.setBounds(0, 0, 192, 192); d.draw(c);
        return bm;
    }
                                                                                                }
