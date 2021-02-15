package com.example.hemocare_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocare_v1.model.ModelAccess;
import com.example.hemocare_v1.server.BaseURL;
import com.example.hemocare_v1.server.VolleyMultipart;
import com.example.hemocare_v1.utils.App;
import com.example.hemocare_v1.utils.GsonHelper;
import com.example.hemocare_v1.utils.Prefs;
import com.example.hemocare_v1.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateUser extends AppCompatActivity {

    private final int CameraR_PP = 1;
    private RequestQueue mRequestQueue;

    TextView dFullname, dNik;
    TextInputEditText dFullnameUser, dPhone, dEmail, dAddressUser;
    ImageView dProfilephoto;
    LinearLayout takePhoto, dPhotoresult;
    Button doUpdate;
    LottieAnimationView defaultPhoto;
    CircleImageView profilePhotoUser;
    ImageView backgroundProfile;
    ProgressDialog progressDialog;

    Bitmap bitmap;
    String mCurrentPhotoPath, _id;
    ModelAccess profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        mRequestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        defaultPhoto = (LottieAnimationView) findViewById(R.id.images);
        profilePhotoUser = (CircleImageView) findViewById(R.id.photoprofileuser);
        backgroundProfile = (ImageView) findViewById(R.id.view1);

        String dProfilePhoto = profile.getProfilephoto();

        if (dProfilePhoto == null) {
            backgroundProfile.setVisibility(View.GONE);
            profilePhotoUser.setVisibility(View.GONE);
        } else {
            defaultPhoto.cancelAnimation();
            profilePhotoUser.setVisibility(View.VISIBLE);
            Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(profilePhotoUser);
            Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(backgroundProfile);
        }

        dFullname = (TextView) findViewById(R.id.dfullname);
        dNik = (TextView) findViewById(R.id.dnik);
        dProfilephoto = (ImageView) findViewById(R.id.profilephotouser);
        takePhoto = (LinearLayout) findViewById(R.id.takephoto);
        doUpdate = (Button) findViewById(R.id.do_update);
        dPhotoresult = (LinearLayout) findViewById(R.id.photoResult);

        dFullnameUser = (TextInputEditText) findViewById(R.id.fullname);
        dFullnameUser.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dPhone = (TextInputEditText) findViewById(R.id.phone);
        dEmail = (TextInputEditText) findViewById(R.id.email);
        dAddressUser = (TextInputEditText) findViewById(R.id.adressuser);
        dAddressUser.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        dFullname.setText(profile.getFullname());
        dNik.setText(profile.getNik());
        _id = profile.get_id();

        dFullnameUser.setText(profile.getFullname());
        dPhone.setText(profile.getPhone());
        dEmail.setText(profile.getEmail());
        dAddressUser.setText(profile.getAddress());

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoNow();
            }
        });

        doUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData(bitmap);
            }
        });
    }

    private void updateUserData(final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.PUT, BaseURL.updateUser + _id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        mRequestQueue.getCache().clear();
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("msg");
                            boolean status = jsonObject.getBoolean("error");
                            if (status == false) {
                                JSONObject user = jsonObject.getJSONObject("result");
                                Utils.storeProfile(user.toString());
                                startActivity(new Intent(UpdateUser.this, MainActivity.class));
                                Animatoo.animateSlideDown(UpdateUser.this);
                            } else {
                                StyleableToast.makeText(getApplicationContext(), strMsg, R.style.toastStyleWarning).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        StyleableToast.makeText(UpdateUser.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", dFullnameUser.getText().toString());
                params.put("phone", dPhone.getText().toString());
                params.put("email", dEmail.getText().toString());
                params.put("address", dAddressUser.getText().toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("profilephoto", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue = Volley.newRequestQueue(UpdateUser.this);
        mRequestQueue.add(volleyMultipartRequest);
    }

    private void takePhotoNow() {
        addPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(UpdateUser.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("Tags", "IOException");
            }
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CameraR_PP);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameUser = profile.getFullname();
        String imageFileName = "JPEG_" + timeStamp + "_" + nameUser + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CompleteUser.RESULT_CANCELED) {
            return;
        }

        if (requestCode == CameraR_PP) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(UpdateUser.this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                dProfilephoto.setImageBitmap(bitmap);
                if (dProfilephoto.getDrawable() != null) {
                    dProfilephoto.requestLayout();
                    dProfilephoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) dProfilephoto.getLayoutParams();
                    dPhotoresult.setVisibility(View.VISIBLE);
                    takePhoto.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                StyleableToast.makeText(UpdateUser.this, "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public void addPermission() {
        Dexter.withActivity(UpdateUser.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(UpdateUser.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UpdateUser.this, MainActivity.class));
        Animatoo.animateSlideDown(UpdateUser.this);
    }
}
