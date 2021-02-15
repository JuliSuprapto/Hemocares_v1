package com.example.hemocare_v1.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocare_v1.MainActivity;
import com.example.hemocare_v1.model.ModelAccess;
import com.example.hemocare_v1.R;
import com.example.hemocare_v1.server.BaseURL;
import com.example.hemocare_v1.utils.App;
import com.example.hemocare_v1.utils.GsonHelper;
import com.example.hemocare_v1.utils.Prefs;
import com.example.hemocare_v1.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    Button doLogin, bRegist;
    TextInputEditText bUsername, bPassword;
    ProgressDialog progressDialog;
    ModelAccess profile;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRequestQueue = Volley.newRequestQueue(this);

        bUsername = (TextInputEditText) findViewById(R.id.username);
        bPassword = (TextInputEditText) findViewById(R.id.password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        bRegist = (Button) findViewById(R.id.regist);
        doLogin = (Button) findViewById(R.id.do_login);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        if(Utils.isLoggedIn()){
            int dRoll = Integer.parseInt(profile.getRole());
            if (dRoll == 2){
                Intent i = new Intent(this , MainActivity.class);
                startActivity(i);
                finish();
            }
        }

        bRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registrasi.class));
                Animatoo.animateSlideUp(Login.this);
            }
        });

        doLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUsername = bUsername.getText().toString();
                String sPassword = bPassword.getText().toString();

                if (sUsername.isEmpty()) {
                    StyleableToast.makeText(Login.this, "Username tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sPassword.isEmpty()) {
                    StyleableToast.makeText(Login.this, "Password tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else {
                    login(sUsername, sPassword);
                }
            }
        });
    }

    public void login(String username, String password) {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("username", username);
        params.put("password", password);

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();

                        try {
                            String strMsg = response.getString("msg");
                            boolean statusMsg = response.getBoolean("error");
                            if (statusMsg == false) {
                                StyleableToast.makeText(Login.this, strMsg, R.style.toastStyleSuccess).show();

                                JSONObject user = response.getJSONObject("data");
                                String tRole = user.getString("role");
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(user.toString());

                                if (tRole.equals("2")) {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    Animatoo.animateSlideDown(Login.this);
                                }
                            } else {
                                StyleableToast.makeText(Login.this, strMsg, R.style.toastStyleWarning).show();
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
        startActivity(new Intent(Login.this, MainActivity.class));
        Animatoo.animateZoom(Login.this);
    }
}
