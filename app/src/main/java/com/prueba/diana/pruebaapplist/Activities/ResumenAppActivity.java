package com.prueba.diana.pruebaapplist.Activities;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prueba.diana.pruebaapplist.Models.Applications;
import com.prueba.diana.pruebaapplist.R;
import com.prueba.diana.pruebaapplist.SqlitedbHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ResumenAppActivity extends AppCompatActivity {

    public Applications app;
    public boolean isLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_app);

        //Setting screen orientation
        isLargeLayout = getResources().getBoolean(R.bool.portrait_only);
        if(isLargeLayout) { //landscape for tablets
            // Tablet Mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else { //portrair for phones and small devices
            // Handset Mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Bundle data = getIntent().getExtras();
        app = data.getParcelable("app");

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Loading info
        setAppInfo();
    }

    /**
     * Method to set info of the app selected
     * */
    public void setAppInfo() {

        TextView textView = (TextView) findViewById(R.id.TxtName);
        textView.setText(app.getName());

        textView = (TextView) findViewById(R.id.TxtMakeBy);
        textView.setText(app.getArtist());

        textView = (TextView) findViewById(R.id.TxtFecha);
        textView.setText(app.getRealese_date());

        textView = (TextView) findViewById(R.id.txtSummary);
        textView.setText(app.getSummary());

        textView = (TextView) findViewById(R.id.TxtPrice);
        textView.setText(String.valueOf(app.getPrice())+"$");

        textView = (TextView) findViewById(R.id.TxtCategory);
        textView.setText(String.valueOf(app.getCategory()));

        if(isNetworkAvailable()) {
            ImageView imageView = (ImageView) findViewById(R.id.AppLogo);
            new DownloadImage(imageView).execute(app.getPictureB());
        } else {
            Toast.makeText(this, getString(R.string.msg_no_internet_image), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method to check if there is internet connection
     * */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Class to load the app image in background
     * */
    private class DownloadImage extends AsyncTask<String, Integer, Bitmap> {

        ImageView imageView;

        public DownloadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String[] params) {

            return downloadImage(params[0]);
        }


        @Override
        protected void onPostExecute(Bitmap o) {
            super.onPostExecute(o);
            imageView.setImageBitmap(o);

        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                InputStream stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Log.e("IMAGEN ", String.valueOf(bitmap));
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if ( httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

    }


}
