package com.prueba.diana.pruebaapplist.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.prueba.diana.pruebaapplist.Adapters.ApplicationsAdapter;
import com.prueba.diana.pruebaapplist.Models.Applications;
import com.prueba.diana.pruebaapplist.R;
import com.prueba.diana.pruebaapplist.SqlitedbHelper;

import java.util.ArrayList;

public class CatalogoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    public SqlitedbHelper dbHelper;
    public ListView listvApps;
    public GridView gridvApps;
    public ArrayList<Applications> appsArrayList;
    public ApplicationsAdapter adapter;
    public  boolean isLargeLayout;
    public String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //Setting screen orientation
        isLargeLayout = getResources().getBoolean(R.bool.portrait_only);
        if (isLargeLayout) { //landscape for tablets
            // Tablet Mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            gridvApps = (GridView) findViewById(R.id.list_apps);
        } else { //portrair for phones and small devices
            // Handset Mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            listvApps = (ListView) findViewById(R.id.list_apps);
        }
        //Checking internet conection
        if (!isNetworkAvailable()){
           dialog();
        }
        dbHelper = new SqlitedbHelper(getApplicationContext());
        appsArrayList = new ArrayList<>();
        adapter = new ApplicationsAdapter(this, appsArrayList);
        //Loading spinner
        cargoSpinner();

        swipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                new UpdateAppssInfo(adapter).execute();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Getting all the apps
        getAppsArrayList(false, "");
        if(isLargeLayout) {
            // Tablet Mode
            loadAppsTablets();
        } else {
            // Handset Mode
            loadAppsPhones();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        swipeRefreshLayout.setRefreshing(false);
        Log.e("pase por", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(false);
        Log.e("pase por", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
        new UpdateAppssInfo(adapter).cancel(true);
        Log.e("pase por", "onStop");
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        try {
            new UpdateAppssInfo(adapter).execute();
            cargoSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setRefreshing(false);
        Log.e("pase por", "onRefresh");
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
     * Dialog to show internet message error
     * */
    public void dialog() {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_no_internet))
                .setNeutralButton(getResources().getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();
    }

    /**
     * Method to load listView adapter
     * */
    private void loadAppsPhones() {
        listvApps.setAdapter(adapter);
        listvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Applications app = (Applications) listvApps.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(), ResumenAppActivity.class);
                intent.putExtra("app", app);
                startActivity(intent);
            }
        });
    }

    /**
     * Method to load gridView adapter
     * */
    private void loadAppsTablets() {
        gridvApps.setAdapter(adapter);
        gridvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Applications app = (Applications) gridvApps.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(), ResumenAppActivity.class);
                intent.putExtra("app", app);
                startActivity(intent);
            }
        });
    }

    /**
     * Method to load spinner
     * */
    private void cargoSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerCategories);
        Cursor cursor = dbHelper.getCategories();
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Categorías");
        Log.e("categories size", String.valueOf(cursor.getCount()));
        try {
            if (cursor != null && cursor.getCount() > 0) {
                // move cursor to first row
                if (cursor.moveToFirst()) {
                    do {
                        categories.add(cursor.getString(0));

                    } while (cursor.moveToNext());
                }
            } else {
                Toast.makeText(this, getString(R.string.msg_no_categories), Toast.LENGTH_LONG).show();
                LinearLayout filtro = (LinearLayout) findViewById(R.id.layout_filtro);
                filtro.setVisibility(View.GONE);
            }

        } finally {
            cursor.close();
        }
        ArrayAdapter<String> adapterSpin = new ArrayAdapter<String>(this, R.layout.item_spinner, categories);
        adapterSpin.setDropDownViewResource(R.layout.item_dropdown_spinner);
        spinner.setAdapter(adapterSpin);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("spinner", spinner.getItemAtPosition(position).toString());
                String category = spinner.getItemAtPosition(position).toString();
                if (!category.equalsIgnoreCase("Categorías"))
                    getAppsArrayList(true, category);
                else
                    getAppsArrayList(false, "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getAppsArrayList(false, "");
            }
        });
    }

    /**
     * Method to get information from the DB
     * */
    public ArrayList<Applications> getAppsArrayList(boolean filtreded, String category) {
        Cursor cursor;
        if (!filtreded)
            cursor = dbHelper.getApps();
        else
            cursor = dbHelper.getAppsFiltreded(category);

        appsArrayList.clear();
        Log.e("apps size2", String.valueOf(cursor.getCount()));
        try {
            if (cursor != null && cursor.getCount() > 0) {
                // move cursor to first row
                if (cursor.moveToFirst()) {
                    do {
                        Applications s = new Applications();
                        s.setApp_id(cursor.getInt(0));
                        s.setName(cursor.getString(1));
                        s.setPictureM(cursor.getString(2));  //thumbnail_url medium size
                        s.setPictureB(cursor.getString(3));
                        s.setSummary(cursor.getString(4));
                        s.setPrice(cursor.getDouble(5));
                        s.setRights(cursor.getString(6));
                        s.setTitle(cursor.getString(7));
                        s.setCategory(cursor.getString(8));
                        s.setRealese_date(cursor.getString(9));
                        s.setArtist(cursor.getString(10));

                        appsArrayList.add(s);
                    } while (cursor.moveToNext());
                }
            } else {
                Toast.makeText(this, getString(R.string.msg_no_info), Toast.LENGTH_LONG).show();
            }

        } finally {
            cursor.close();
        }
        adapter.notifyDataSetChanged();
        return appsArrayList;
    }


    class UpdateAppssInfo extends AsyncTask {

        public SqlitedbHelper dbHelper;
        public ApplicationsAdapter adapter;

        public UpdateAppssInfo(ApplicationsAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                dbHelper = new SqlitedbHelper(getApplicationContext());
                swipeRefreshLayout.setRefreshing(true);
                dbHelper.getCatalogo(null);
                getAppsArrayList(false, "");



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);

            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_newinfo), Toast.LENGTH_LONG).show();
        }

    }

}
