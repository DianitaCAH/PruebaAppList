package com.prueba.diana.pruebaapplist.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.prueba.diana.pruebaapplist.Adapters.ApplicationsAdapter;
import com.prueba.diana.pruebaapplist.Models.Applications;
import com.prueba.diana.pruebaapplist.R;
import com.prueba.diana.pruebaapplist.SqlitedbHelper;

import java.util.ArrayList;

public class CatalogoActivity extends AppCompatActivity {

    public SqlitedbHelper dbHelper;
    public ListView listvApps;
    public ArrayList<Applications> appsArrayList;
    public ApplicationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        dbHelper = new SqlitedbHelper(getApplicationContext());
        listvApps = (ListView) findViewById(R.id.list_apps);
        appsArrayList = new ArrayList<>();
        adapter = new ApplicationsAdapter(this, appsArrayList);
        cargoSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Getting all the apps
        getAppsArrayList(false, "");
        loadApps();


    }

    private void loadApps() {
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
                Toast.makeText(this, "no hay categorias q listar", Toast.LENGTH_LONG).show();
                spinner.setVisibility(View.GONE);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

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
                Toast.makeText(this, "no hay app q listar", Toast.LENGTH_LONG).show();
            }

        } finally {
            cursor.close();
        }
        adapter.notifyDataSetChanged();
        return appsArrayList;
    }


}
