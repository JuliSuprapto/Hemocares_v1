package com.example.hemocare_v1.assets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.hemocare_v1.MainActivity;
import com.example.hemocare_v1.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroSlider extends AppCompatActivity {

    private ViewPager screenPager; //ini bagian pager nya fungsinya buat nempatin tampilan diatasnya, kalo kita slide slide pager itu nanti bakal nampilin halaman halaman laennya
    IntroViewPagerAdapter introViewPagerAdapter; //ini adapter buat ngaturnya
    TabLayout tabIndicator; //tab indicator ini nanti jadi penanda, misal kita punya 4 pager misal kita posisinya dipager 1 itu kita ubah aja warnanya buat nentuin kalo kita lagi dihalaman itu
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //ini buat request supaya tampilannya gak punya icon batre sinyal gitu, jadi full layar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //ini yang akses full layarnya

        if (restorePrefData()) { //ini buat ceknya, nanti ada pref yang ngisi, orang ini udah pernah teken button mulai belum, kalo udah pernah selama aplikasi terinstall, besok kalo mau buka langsung
                                 //diarahin kahalaman utama
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }

        setContentView(R.layout.activity_intro_slider);

        //ini sama kaya biasanya, inisial ID tampilan, bangun variable buat penanda aja
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        screenPager =findViewById(R.id.screen_viewpager);

        //ini array yang nampung nilainya nanti jadi teks sama gambarnya nanti diatur disini
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Selamat Datang.","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.icon_text_1024));
        mList.add(new ScreenItem("Temukan Pahlawan","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.ic_img_1));
        mList.add(new ScreenItem("Bagikan Kebaikan","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.ic_img_2));
        mList.add(new ScreenItem("Jadwalkan Sekarang","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.ic_img_3));

        //ini manggil pagernya lewat adapter, nanti tulisan diatas itu di lempar buat di posisiin sesuai sama urutannya
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disini di cek dulu get current item atau ambil item sebelumnya, kalo misal 0 maka ditambahin, karena 0 = selamat datang, jadi kalo teken btn next ditambahin 1
                position = screenPager.getCurrentItem();
                //disini cek nya, kalo item sebelumnya = 0 maka + 1, 1 itu temukan pahlawan
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                //disini buat cek baliknya, kan geser kanan kiri, otomatis next back, kalo back - 1, kalo next + 1
                if (position == mList.size()-1) { // when we rech to the last screen
                    // TODO : show the GETSTARTED Button and hide the indicator and the next button
                    loaddLastScreen();
                }
            }
        });

        //ini untuk indikator tadi, cara nentuinnya juga sama di liat dari itemnya, kalo 0 berarti tambahin 1
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
                //disini btn mulai, kalo ini di klik tampilan ini gak pernah muncul lagi selama aplikasi dibuka
                //makanya ada save pref, dia yang bakal ngambil data apakah udah pernah teken tombol mulai
                savePrefsData();
                finish();
            }
        });

        //tv skip juga btn, di langsung lewatin gitu aja dari 0-akhir, kalo tadi next back pake sifat + sama -, ini langsung ngambil bagian akhirnya
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });

    }

    private void loaddLastScreen() {
        //ini fungsi skip tadi, kalo udah sampe halaman belakang paling akhir btnNext, btnSkip, idicator bakal di hidden atau hilangin, terus btnMulai di tampilin
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }

    private void savePrefsData() {
        //ini nyimpen pref nya, kuncinya ada di myPrefs
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();
    }

    private boolean restorePrefData() {
        //ini pref nya, buat cek data yang udah pernah disimpen buat ngambil keputusan apakah mau ditampilin lagi atau nggak halaman ini
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }


}
