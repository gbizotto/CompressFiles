package com.gbizotto.compressfiles;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gbizotto.compressfiles.util.CompressionUtil;
import com.gbizotto.compressfiles.util.ImageUtil;
import com.gbizotto.compressfiles.util.PermissionsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    /**
     * Constants to access return from intents
     */
    private static final int INTENT_CAMERA = 2;
    private static final int INTENT_GALLERY = 3;

    /**
     * Constants to access variables saved before app's death
     */
    private static final String RESTORE_INSTANCE_IMAGE_LIST = "restore_instance_image_list";

    @BindView(R.id.camera)
    Button mBtnCamera;
    @BindView(R.id.gallery)
    Button mBtnGallery;
    @BindView(R.id.zip)
    Button mBtnZip;
    @BindView(R.id.txtTotalImagesAdded)
    TextView mTxtTotalImagesAdded;
    @BindView(R.id.txtFileDir)
    TextView mTxtFileDir;

    private Context mContext;
    private Uri mFileUri;
    private List<Uri> mImagesUriList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        ButterKnife.bind(this);
    }

    @OnClick(R.id.camera)
    public void onCameraClick(){
        if (PermissionsUtil.canUseCamera(mContext,this)) {
            startActivityForResult(fillIntentCamera(), INTENT_CAMERA);
        }
    }

    @OnClick(R.id.gallery)
    public void onGalleryClick(){
        if (PermissionsUtil.canAccessGallery(mContext,this)) {
            startActivityForResult(fillIntentGallery(), INTENT_GALLERY);
        }
    }

    @OnClick(R.id.zip)
    public void onZipClick(){
        if (mImagesUriList == null || mImagesUriList.isEmpty()){
           Toast.makeText(mContext, R.string.empty_list, Toast.LENGTH_LONG).show();
        } else{
            Uri zipUri = CompressionUtil.zip(mImagesUriList);
            mTxtFileDir.setText(zipUri.getPath());
            mTxtFileDir.setVisibility(View.VISIBLE);

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case PermissionsUtil.PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty. 
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(fillIntentCamera(), INTENT_CAMERA);
                } else {
                    Toast.makeText(mContext, R.string.camera_no_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case PermissionsUtil.PERMISSION_GALLERY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(fillIntentGallery(), INTENT_GALLERY);
                } else {
                    Toast.makeText(mContext, R.string.gallery_no_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private Intent fillIntentCamera() {
        mFileUri = ImageUtil.getOutputMediaFileUri(mContext); // create a file to save the image

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri); // set the image file name
        return cameraIntent;
    }

    private Intent fillIntentGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return photoPickerIntent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri pathToInternallyStoredImage = null;

            if (requestCode == INTENT_CAMERA) {
                if (mFileUri == null) {
                    Toast.makeText(mContext, R.string.camera_failed, Toast.LENGTH_LONG).show();
                } else {
                    pathToInternallyStoredImage = ImageUtil.saveToInternalStorage(mContext, mFileUri);
                }
            }else if (requestCode == INTENT_GALLERY){
                pathToInternallyStoredImage = ImageUtil.saveToInternalStorage(mContext, Uri.parse(ImageUtil.getPath(data.getData(), mContext)));
            }

            addImageToList(pathToInternallyStoredImage);

        } else if (resultCode != RESULT_CANCELED) {
            if (requestCode == INTENT_CAMERA) {
                Toast.makeText(mContext, R.string.camera_failed, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mContext, R.string.gallery_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addImageToList(Uri pathToInternallyStoredImage){
        if(mImagesUriList == null){
            mImagesUriList = new ArrayList<>();

            mBtnZip.setVisibility(View.VISIBLE);
            mTxtTotalImagesAdded.setVisibility(View.VISIBLE);
        }

        mImagesUriList.add(pathToInternallyStoredImage);

        mTxtTotalImagesAdded.setText(getResources().getQuantityString(R.plurals.number_images_added, mImagesUriList.size(), mImagesUriList.size()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mImagesUriList!= null) {
            outState.putParcelableArrayList(RESTORE_INSTANCE_IMAGE_LIST, (ArrayList<Uri>) mImagesUriList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(RESTORE_INSTANCE_IMAGE_LIST)){
            mImagesUriList = (ArrayList<Uri>) savedInstanceState.get(RESTORE_INSTANCE_IMAGE_LIST);

            mBtnZip.setVisibility(View.VISIBLE);
            mTxtTotalImagesAdded.setVisibility(View.VISIBLE);
        }
    }
}
