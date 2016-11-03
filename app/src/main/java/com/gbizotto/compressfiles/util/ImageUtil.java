package com.gbizotto.compressfiles.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gabrielabizotto on 03/11/16.
 */

public class ImageUtil {

    /**
     * App's private storage for pictures.
     */
    public static final String PICTURE_DIRECTORY = "compressed_files";

    /**
     * Value that will be considered a parameter for the bitmap compression.
     */
    private static final int COMPRESSION_QUALITY = 50;

    private ImageUtil() { }

    /**
     * Decodes the bitmap from the given uri.
     * @param imageUri
     * @return
     */
    private static Bitmap getBitmapFromUri(Uri imageUri){
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageUri.getPath(), options);
    }

    /**
     * Looks for the image path accessible to other apps.
     * @param uri, the URI used by the gallery.
     * @param context
     * @return the public image's URI
     */
    public static String getPath(Uri uri, Context context) {
        String selectedImagePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
            cursor.close();
        }

        if (selectedImagePath == null) {
            selectedImagePath = uri.getPath();
        }
        return selectedImagePath;
    }

    private static String buildFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder builder = new StringBuilder()
                .append(timeStamp)
                .append(".jpg");
        return builder.toString();
    }

    /**
     * Compress and saves a file pointed by <param>uri</param> in the app's private storage.
     * @param context
     * @param uri
     * @return the uri to the file in the private storage.
     */
    public static Uri saveToInternalStorage(Context context,Uri uri){
        File destinationInternalImageFile = new File(getOutputInternalMediaFile(context).getPath());

        Bitmap bitmapImage = getBitmapFromUri(uri);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destinationInternalImageFile);
            // Use the compress method on the BitMap object to write image to the OutputStream

            bitmapImage.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(ImageUtil.class.getSimpleName(),e.getMessage(),e);
            }
        }
        return Uri.fromFile(destinationInternalImageFile);
    }

    private static boolean createMediaStorageDir(File mediaStorageDir) {
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    private static File getOutputInternalMediaFile(Context context) {
        File mediaStorageDir = new File(context.getFilesDir(), PICTURE_DIRECTORY);

        createMediaStorageDir(mediaStorageDir);

        return createFile(mediaStorageDir);
    }

    private static File createFile(File mediaStorageDir) {
        return new File(mediaStorageDir.getPath() + File.separator + buildFileName());
    }

    /**
     * Create a file Uri for saving an image or video through a camera intent.
     */
    public static Uri getOutputMediaFileUri(Context context) {
        return Uri.fromFile(getOutputMediaFile(context));
    }

    private static File getOutputMediaFile(Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PICTURE_DIRECTORY);

        // Create the storage directory if it does not exist
        if (!createMediaStorageDir(mediaStorageDir)) {
            return null;
        }

        return new File(mediaStorageDir.getPath() + File.separator + buildFileName());
    }
}
