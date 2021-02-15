package com.example.hemocare_v1.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocare_v1.R;
import com.example.hemocare_v1.server.BaseURL;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Registrasi extends AppCompatActivity {

    Button doRegist, bLogin;
    TextInputEditText bNik, bFullname, bUsername, bPassword, bBlood, bPhone;
    ProgressDialog progressDialog;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        mRequestQueue = Volley.newRequestQueue(this);

        bNik = (TextInputEditText) findViewById(R.id.nik);
        bFullname = (TextInputEditText) findViewById(R.id.fullname);
        bFullname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        bUsername = (TextInputEditText) findViewById(R.id.username);
        bPassword = (TextInputEditText) findViewById(R.id.password);
        bBlood = (TextInputEditText) findViewById(R.id.blood);
        bBlood.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        bPhone = (TextInputEditText) findViewById(R.id.phone);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        bLogin = (Button) findViewById(R.id.back_login);
        doRegist = (Button) findViewById(R.id.do_regist);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registrasi.this, Login.class));
                Animatoo.animateSlideDown(Registrasi.this);
            }
        });

        doRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sNik = bNik.getText().toString();
                String sFullname = bFullname.getText().toString();
                String sUsername = bUsername.getText().toString();
                String sPassword = bPassword.getText().toString();
                String sBloodtype = bBlood.getText().toString();
                String sPhone = bPhone.getText().toString();
                String sAddress = null;
                String sBirthDate = null;
                String sEmail = null;
                String sProfilePhoto = null;

                if (sNik.isEmpty()) {
                    StyleableToast.makeText(Registrasi.this, "NIK tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (sFullname.isEmpty()) {
                    StyleableToast.makeText(Registrasi.this, "Nama lengkap tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sUsername.isEmpty()) {
                    StyleableToast.makeText(Registrasi.this, "Username tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sPassword.isEmpty()) {
                    StyleableToast.makeText(Registrasi.this, "Password tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sBloodtype.isEmpty()) {
                    StyleableToast.makeText(Registrasi.this, "Golongan darah tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sPhone.isEmpty()) {
                    StyleableToast.makeText(Registrasi.this, "Nomor telepon tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else {
                    registrasi(sNik, sFullname, sUsername, sPassword, sBloodtype, sPhone, sAddress, sBirthDate, sEmail, sProfilePhoto);
                }
            }
        });
    }

    public void registrasi(String nik, String fullname, String username, String password, String bloodtype, String phone, String address, String birthdate, String email, String profilephoto) {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("nik", nik);
        params.put("fullname", fullname);
        params.put("username", username);
        params.put("password", password);
        params.put("bloodtype", bloodtype);
        params.put("phone", phone);
        params.put("address", address);
        params.put("birthdate", birthdate);
        params.put("email", email);
        params.put("profilephoto", profilephoto);
        params.put("role", "2");

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.register, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("msg");
                            boolean statusMsg = response.getBoolean("error");

                            if (statusMsg == false) {
                                StyleableToast.makeText(Registrasi.this, strMsg, R.style.toastStyleSuccess).show();
                                startActivity(new Intent(Registrasi.this, Login.class));
                                Animatoo.animateSlideDown(Registrasi.this);
                            } else {
                                StyleableToast.makeText(Registrasi.this, strMsg, R.style.toastStyleWarning).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });
        mRequestQueue.add(req);
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
        startActivity(new Intent(Registrasi.this, Login.class));
        Animatoo.animateSlideDown(Registrasi.this);
    }
}
