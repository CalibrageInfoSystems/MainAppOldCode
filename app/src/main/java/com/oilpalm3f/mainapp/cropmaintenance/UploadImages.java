package com.oilpalm3f.mainapp.cropmaintenance;

import static com.oilpalm3f.mainapp.common.CommonUtils.isFromConversion;
import static com.oilpalm3f.mainapp.common.CommonUtils.isFromCropMaintenance;
import static com.oilpalm3f.mainapp.common.CommonUtils.isFromFollowUp;
import static com.oilpalm3f.mainapp.common.CommonUtils.isFromImagesUploading;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oilpalm3f.mainapp.BuildConfig;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Palm3FoilDatabase;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.ExistingFarmerData;
import com.oilpalm3f.mainapp.dbmodels.Farmer;
import com.oilpalm3f.mainapp.dbmodels.FarmerBank;
import com.oilpalm3f.mainapp.dbmodels.FarmerBankdetailsforImageUploading;
import com.oilpalm3f.mainapp.dbmodels.FarmersDataforImageUploading;
import com.oilpalm3f.mainapp.dbmodels.IdentityProof;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;
import com.oilpalm3f.mainapp.utils.ImageUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UploadImages extends OilPalmBaseActivity {

    private DataAccessHandler dataAccessHandler;
    TextView farmercode,farmername,fathername,mobilenumber,farmermandal,farmerviallge,aadharnumber,norecordsforidproofs,norecordsforbank,accountnumber,accountname,ifsccode,bankname;
    LinearLayout idproofslayout,bankdetailslayout;
    Button submitBtn;
    ArrayList<FarmersDataforImageUploading> farmersdata;
    String selectedfarmercode;
    ImageView farmerimage,idproofimage,bankpassbookimage;
    private static final int CAMERA_REQUEST = 1888;
    private String mCurrentPhotoPath;
    private boolean isImage = false;
    private byte[] bytes = null;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int REQUEST_CAM_PERMISSIONS = 1;

    private LinkedHashMap<String, String> idProofsData;
    private List<IdentityProof> identityProofsList;

    private FarmerBankdetailsforImageUploading farmerBank = null;

    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.activity_upload_images, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dataAccessHandler = new DataAccessHandler(UploadImages.this);
        initView();
        setViews();
        setTile("Upload Images Screen");
    }

    public void initView(){

        farmercode = findViewById(R.id.farmercode);
        farmername = findViewById(R.id.farmername);
        fathername = findViewById(R.id.fathername);
        mobilenumber = findViewById(R.id.mobilenumber);
        farmermandal = findViewById(R.id.farmermandal);
        farmerviallge = findViewById(R.id.farmerviallge);
        farmerimage = findViewById(R.id.farmerimage);

        aadharnumber = findViewById(R.id.aadharnumber);
        idproofimage = findViewById(R.id.idproofimage);
        idproofslayout = findViewById(R.id.idproofslayout);
        norecordsforidproofs = findViewById(R.id.norecordsforidproofs);

        bankname = findViewById(R.id.bankname);
        accountnumber = findViewById(R.id.accountnumber);
        accountname = findViewById(R.id.accountname);
        ifsccode = findViewById(R.id.ifsccode);
        norecordsforbank = findViewById(R.id.norecordsforbank);
        bankdetailslayout = findViewById(R.id.bankdetailslayout);
        bankpassbookimage = findViewById(R.id.bankpassbookimage);

        submitBtn = findViewById(R.id.submitBtn);

    }

    public void setViews(){

        selectedfarmercode = CommonConstants.FARMER_CODE;
        Log.d("selectedfarmercode",selectedfarmercode + "");

        farmersdata = dataAccessHandler.getFarmerDetailsforImageUploading(Queries.getInstance().getfarmerdetailsforimageuploading(selectedfarmercode));

        String fullname = "", middleName = "";


        if (!TextUtils.isEmpty(farmersdata.get(0).getMiddleName()) && !
                farmersdata.get(0).getMiddleName().equalsIgnoreCase("null")) {
            middleName = farmersdata.get(0).getMiddleName();
        }

        fullname = farmersdata.get(0).getFirstName().trim() + " " + middleName + " " + farmersdata.get(0).getLastName().trim();

        farmercode.setText(": " + farmersdata.get(0).getCode());
        farmername.setText(": " + fullname);
        fathername.setText(": " + farmersdata.get(0).getGuardianName());
        mobilenumber.setText(": " + farmersdata.get(0).getContactNumber());
        farmermandal.setText(": " + farmersdata.get(0).getMandalName());
        farmerviallge.setText(": " + farmersdata.get(0).getVillageName());


        idProofsData  = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("12"));

        identityProofsList = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);

        if (identityProofsList == null && (isFromImagesUploading())) {
            identityProofsList = (List<IdentityProof>) dataAccessHandler.getSelectedIdProofsData(Queries.getInstance().getFarmerIdentityProof(CommonConstants.FARMER_CODE), 1);
        }

        Log.d("identityProofsList", identityProofsList.size() + "");

        if (identityProofsList.size() == 0) {
            Log.d("NoIDProofs", "IDProofs not available");
            norecordsforidproofs.setVisibility(View.VISIBLE);
            idproofslayout.setVisibility(View.GONE);
        } else {
            Log.d("IDProofs", "IDProofs available");
            norecordsforidproofs.setVisibility(View.GONE);
            idproofslayout.setVisibility(View.VISIBLE);
            aadharnumber.setText(": " + identityProofsList.get(0).getIdproofnumber());
        }

        farmerBank = (FarmerBankdetailsforImageUploading) DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS);
        if (farmerBank == null && (isFromImagesUploading())) {
            farmerBank = (FarmerBankdetailsforImageUploading) dataAccessHandler.getSelectedFarmerBankDataforImageUploading(Queries.getInstance().getFarmerBankDataforImageUploading(CommonConstants.FARMER_CODE), 0);
        }

        if (null == farmerBank) {
            Log.d("NoBankDetails", "BankDetails not available");
            norecordsforbank.setVisibility(View.VISIBLE);
            bankdetailslayout.setVisibility(View.GONE);
        }else{
            Log.d("BankDetails", "BankDetails available");
            norecordsforbank.setVisibility(View.GONE);
            bankdetailslayout.setVisibility(View.VISIBLE);
            bankname.setText(": " +farmerBank.getDesc());
            accountnumber.setText(": " +farmerBank.getAccountNumber());
            accountname.setText(": " +farmerBank.getAccountHolderName());
            ifsccode.setText(": " +farmerBank.getIFSCCode());

        }

        farmerimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(UploadImages.this, Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(
                            UploadImages.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_CAM_PERMISSIONS
                    );
                } else {
                    dispatchTakePictureIntent(CAMERA_REQUEST);
                }

            }
        });

    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case CAMERA_REQUEST:
                File f = null;
                mCurrentPhotoPath = null;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(UploadImages.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() {
        File pictureDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_Pictures/" + "UploadedFarmerPhotos");
        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }

        File finalFile = new File(pictureDirectory, CommonConstants.FARMER_CODE + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
                        handleBigCameraPhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    mCurrentPhotoPath = null;
                }
                break;
            }


        } // switch
    }
    private void handleBigCameraPhoto() throws Exception {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
        }

    }

    private void setPic() throws Exception {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = farmerimage.getWidth();
        int targetH = farmerimage.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        getBytesFromBitmap(bitmap);
        bitmap = ImageUtility.rotatePicture(90, bitmap);
        farmerimage.setImageBitmap(bitmap);

        farmerimage.setVisibility(View.VISIBLE);
        isImage = true;
        farmerimage.invalidate();
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        bytes = stream.toByteArray();
        return stream.toByteArray();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        UploadImages.this.sendBroadcast(mediaScanIntent);
    }

}