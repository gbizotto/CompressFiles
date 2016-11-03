package com.gbizotto.compressfiles.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by gabrielabizotto on 03/11/16.
 */

public final class CompressionUtil {

    private static final int BUFFER = 1024;


    private CompressionUtil() {
    }

    /**
     * Receives a list of uris and adds all the files to a zip file.
     * @param uriList
     * @return uri of the zipped file
     */
    public static Uri zip(List<Uri> uriList) {
        File destinationInternalImageFile = getOutputExternalMediaFile();

        ZipOutputStream zos = null;
        try  {
            BufferedInputStream origin;

            FileOutputStream dest = new FileOutputStream(destinationInternalImageFile);

            zos = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for(Uri uri:uriList){

                Log.v(CompressionUtil.class.getSimpleName(), "Adding: " + uri.getPath());

                File file = new File(uri.getPath());

                FileInputStream fi = new FileInputStream(file);

                origin = new BufferedInputStream(fi, BUFFER);

                zos.putNextEntry(new ZipEntry(file.getName()));
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    zos.write(data, 0, count);
                }
                origin.close();
            }

            zos.close();

            Log.v(CompressionUtil.class.getSimpleName(), "All files added to the zip file");

        } catch(Exception e) {
            Log.e(CompressionUtil.class.getSimpleName(),e.getMessage(),e);
        }finally {
            try{
                if(zos != null){
                    zos.close();
                }
            }catch (IOException ioe){
                Log.e(CompressionUtil.class.getSimpleName(),ioe.getMessage(),ioe);
            }
        }

        return Uri.fromFile(destinationInternalImageFile);
    }

    private static File getOutputExternalMediaFile(){
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                ImageUtil.PICTURE_DIRECTORY
        );

        createMediaStorageDir(mediaStorageDir);

        return createFile(mediaStorageDir);
    }

    private static File createFile(File mediaStorageDir) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir .getPath() + File.separator + "ZIP-" + "-"+ timeStamp + ".zip");
        return mediaFile;
    }

    private static boolean createMediaStorageDir(File mediaStorageDir) {
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return false;
            }
        }
        return true;
    }
}
