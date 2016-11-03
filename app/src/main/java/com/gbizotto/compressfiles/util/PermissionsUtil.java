package com.gbizotto.compressfiles.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by gabrielabizotto on 03/11/16.
 */

public final class PermissionsUtil {

    /**
     * Constants to access return from permissions
     */
    public static final int PERMISSION_CAMERA = 1;
    public static final int PERMISSION_GALLERY = 2;

    private PermissionsUtil() {
    }

    public static boolean canAccessGallery(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            // if app doesn't have permission to access the gallery, asks for it. 
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_GALLERY);
            return false;
        }

        return true;
    }

    public static boolean canUseCamera(Context context, Activity activity) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // If app doesn't have permission to access the camera, asks for it. 
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return false;
        }
        return true;
    }
}
