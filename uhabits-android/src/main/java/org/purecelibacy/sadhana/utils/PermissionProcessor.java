package org.purecelibacy.sadhana.utils;

/**
 * Created by Kamlesh on 10-09-2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import org.purecelibacy.sadhana.R;


/**
 * Created by ferid.cafer on 3/22/2016.
 */
public class PermissionProcessor {
    private Activity activity;
    private View view;
    private PermissionGrantListener permissionGrantListener;
    public static final int REQUEST_EXTERNAL_STORAGE = 101;

    public PermissionProcessor(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    public void setPermissionGrantListener(PermissionGrantListener permissionGrantListener) {
        this.permissionGrantListener = permissionGrantListener;
    }

    /**
     * Ask for read-write external storage permission
     */
    public void askForPermissionExternalStorage() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { //permission yet to be granted

            getPermissionExternalStorage();
        } else { //permission already granted
            if (permissionGrantListener != null) {
                permissionGrantListener.OnGranted();
            }
        }
    }

    /**
     * Request and get the permission for external storage
     */
    public void getPermissionExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

/*           Snackbar snackbar = Snackbar.make(view, R.string.grantPermission,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_EXTERNAL_STORAGE);
                            //snackbar.dismiss();
                        }
                    });
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(activity, org.purecelibacy.androidbase.R.color.white));
            snackbar.show();*/

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                   REQUEST_EXTERNAL_STORAGE);
        }
    }

}
