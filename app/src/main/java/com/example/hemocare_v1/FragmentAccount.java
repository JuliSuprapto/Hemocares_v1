package com.example.hemocare_v1;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocare_v1.access.Login;
import com.example.hemocare_v1.access.Registrasi;
import com.example.hemocare_v1.model.ModelAccess;
import com.example.hemocare_v1.server.BaseURL;
import com.example.hemocare_v1.utils.App;
import com.example.hemocare_v1.utils.GsonHelper;
import com.example.hemocare_v1.utils.Prefs;
import com.example.hemocare_v1.utils.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentAccount extends Fragment {

    Button bLogin, bRegist;
    ConstraintLayout pageA, pageB;
    TextView dFullname, dNik, dBlood, dPhone, dEmail, dAddress, dBirthdate, dLengkapi, dEdit;
    LinearLayout bLogout, history, pageC;
    LottieAnimationView defaultPhotoUser;
    CircleImageView profilePhotoUser;
    ImageView backgroundProfile;
    ModelAccess profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        defaultPhotoUser = (LottieAnimationView)v.findViewById(R.id.defaultPhoto);
        profilePhotoUser = (CircleImageView)v.findViewById(R.id.photoprofileuser);
        backgroundProfile = (ImageView)v.findViewById(R.id.view1);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        pageA = (ConstraintLayout) v.findViewById(R.id.page_a);
        pageB = (ConstraintLayout) v.findViewById(R.id.page_b);
        pageC = (LinearLayout) v.findViewById(R.id.page_c);
        bLogout = (LinearLayout) v.findViewById(R.id.logout);

        dFullname = (TextView)v.findViewById(R.id.dfullname);
        dNik = (TextView)v.findViewById(R.id.dnik);
        dBlood = (TextView)v.findViewById(R.id.golongandarah);
        dPhone = (TextView)v.findViewById(R.id.phone);
        dAddress = (TextView)v.findViewById(R.id.address);
        dBirthdate = (TextView)v.findViewById(R.id.birthdate);
        dEmail = (TextView)v.findViewById(R.id.email);
        dLengkapi = (TextView)v.findViewById(R.id.lengkapi);
        dEdit = (TextView)v.findViewById(R.id.editData);

        if (Utils.isLoggedIn()) {
            pageA.setVisibility(View.GONE);
            pageB.setVisibility(View.VISIBLE);
            pageC.setVisibility(View.VISIBLE);

            dFullname.setText(profile.getFullname());
            dNik.setText(profile.getNik());
            dBlood.setText(profile.getBloodtype());
            dPhone.setText(profile.getPhone());
            if (profile.getAddress() == null && profile.getBirthdate() == null && profile.getEmail() == null){
                dAddress.setText("-");
                dBirthdate.setText("-");
                dEmail.setText("-");
            }else {
                dAddress.setText(profile.getAddress());
                dBirthdate.setText(profile.getBirthdate());
                dEmail.setText(profile.getEmail());
                String mail = profile.getEmail();
            }

            String dProfilePhoto = profile.getProfilephoto();

            if (dProfilePhoto == null || dAddress == null){
                backgroundProfile.setVisibility(View.VISIBLE);
                profilePhotoUser.setVisibility(View.GONE);
                dLengkapi.setVisibility(View.VISIBLE);
                dEdit.setVisibility(View.GONE);
            } else{
                dLengkapi.setVisibility(View.GONE);
                dEdit.setVisibility(View.VISIBLE);
                defaultPhotoUser.cancelAnimation();
                profilePhotoUser.setVisibility(View.VISIBLE);
                Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(profilePhotoUser);
                Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(backgroundProfile);
            }

        }else {
            pageA.setVisibility(View.VISIBLE);
            pageB.setVisibility(View.GONE);
            pageC.setVisibility(View.GONE);
        }

        bLogin = (Button) v.findViewById(R.id.login);
        bRegist = (Button) v.findViewById(R.id.regist);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                startActivity(new Intent(getActivity(), Login.class));
                Animatoo.animateSlideDown(getActivity());
            }
        });

        bRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                startActivity(new Intent(getActivity(), Registrasi.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        dLengkapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CompleteUser.class));
                Animatoo.animateSlideDown(getActivity());
            }
        });

        dEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateUser.class));
                Animatoo.animateSlideDown(getActivity());
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                startActivity(new Intent(getActivity(), MainActivity.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        return v;
    }
}
