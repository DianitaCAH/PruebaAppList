package com.prueba.diana.pruebaapplist.Activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.prueba.diana.pruebaapplist.Activities.CatalogoActivity;
import com.prueba.diana.pruebaapplist.R;
import com.prueba.diana.pruebaapplist.SqlitedbHelper;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    public SqlitedbHelper dbHelper;
    public static SQLiteDatabase mBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        //Create Database
        dbHelper = new SqlitedbHelper(this);
        mBaseDatos = dbHelper.getWritableDatabase();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Create an Intent that will start the Menu-Activity.
                Intent intent = new Intent(getApplicationContext(), CatalogoActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHelper = new SqlitedbHelper(getApplicationContext());

        new LoadInfo().execute();
    }

    class LoadInfo extends AsyncTask {

        public SqlitedbHelper dbHelper;

        public LoadInfo() {
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                dbHelper = new SqlitedbHelper(getApplicationContext());
                dbHelper.getCatalogo(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
