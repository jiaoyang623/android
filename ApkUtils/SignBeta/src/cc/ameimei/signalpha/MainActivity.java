package cc.ameimei.signalpha;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Object data = getMeta(getApplicationContext(), "ALPHA");
        if (data != null && data instanceof String) {
            ((TextView) findViewById(R.id.txt)).setText((String) data);
        }
    }

    public static Object getMeta(Context context, String name) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                return applicationInfo.metaData.get(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
