package com.prueba.diana.pruebaapplist;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.prueba.diana.pruebaapplist.Models.Applications;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class SqlitedbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NOMBRE = "PRUEBA_APP_LIST.db";
    public Context context;

    private static final String DB_CREATE_APPS =
    		"CREATE TABLE " + Applications.TABLE_NAME + " (" +
                    Applications.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                    Applications.COLUMN_NAME + " TEXT(50) NOT NULL  NOT NULL, " +
                    Applications.COLUMN_THUMBNAIL_URL_M + " TEXT NOT NULL, " +
                    Applications.COLUMN_THUMBNAIL_URL_B + " TEXT NOT NULL, " +
                    Applications.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                    Applications.COLUMN_PRICE + "  REAL NOT NULL," +
                    Applications.COLUMN_RIGHTS + " TEXT NOT NULL, " +
                    Applications.COLUMN_TITLE + " TEXT(50) NOT NULL, " +
                    Applications.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    Applications.COLUMN_REALESE_DATE + " TEXT NOT NULL, " +
                    Applications.COLUMN_ARTIST + " TEXT NOT NULL " +
    				");";


    /*
     * constructor de la clase
     */
    public SqlitedbHelper(Context context) {
        super(context, DB_NOMBRE, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(DB_CREATE_APPS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void destroyBD (SQLiteDatabase db, Context context){
        db.delete(Applications.TABLE_NAME, null, null);

        Log.e("destroyBD", "Delete");
        db.close();

    }


    /*
    * metodos para transacciones
    */
    public void abrir_transaccion(SQLiteDatabase mBaseDatos) {
        mBaseDatos.beginTransaction();
    }

    public void aplicar_transaccion(SQLiteDatabase mBaseDatos) {
        mBaseDatos.setTransactionSuccessful();
    }

    public void cerrar_transaccion(SQLiteDatabase mBaseDatos) {
        mBaseDatos.endTransaction();
    }

    public Cursor select(String query, SQLiteDatabase mBaseDatos) throws android.database.SQLException {
        return mBaseDatos.rawQuery(query, null);
    }

    public int update(String table, ContentValues values, String where, SQLiteDatabase db ) {
        return db.update(table, values, where, null);
    }

    /**
     * Query to get a list of all apps
     * */
    public Cursor getApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM applications " +
                "ORDER BY realese_date desc ", null);
        Log.e("apps", String.valueOf(data.getCount()));
        return data;
    }

    /**
     * Query to get all apps by category
     * */
    public Cursor getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery( "SELECT category FROM applications " +
                "GROUP BY category " +
                "ORDER BY category asc", null );
        Log.e("categorias", String.valueOf(data.getCount()));
        return data;
    }

    /**
     * Query to get all apps by category
     * */
    public Cursor getAppsFiltreded(String category) {
        Log.e("filtro", category);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery( "SELECT * FROM applications " +
                "WHERE category like '%" + category + "%' "+
                "ORDER BY realese_date desc", null );
        Log.e("searchResults", String.valueOf(data.getCount()));
        return data;
    }

    /**
     * Query to get specific app info
     * */
    public Cursor getAppResume(int id) {
        Log.e("resumen", String.valueOf(id));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery( "SELECT * FROM applications " +
                "WHERE id = " + id + " "+
                "ORDER BY realese_date desc", null );
        Log.e("resumenResults", String.valueOf(data.getCount()));
        return data;
    }


    /**
     * Functions to inset into DB the info from the server/url
     **/
    public boolean insertData (JsonArray app) {

        Log.e("ESTO ES LO Q HAY", app.toString());

        for (int i= 0; i < app.size(); i++) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Applications.COLUMN_ID, app.getAsJsonArray().get(i).getAsJsonObject().get("id")
                    .getAsJsonObject().get("attributes").getAsJsonObject().get("im:id").getAsString());
            contentValues.put(Applications.COLUMN_NAME, app.getAsJsonArray().get(i).getAsJsonObject().get("im:name")
                    .getAsJsonObject().get("label").getAsString());
            contentValues.put(Applications.COLUMN_TITLE, app.getAsJsonArray().get(i).getAsJsonObject().get("title")
                    .getAsJsonObject().get("label").getAsString());
            //Getting medium size image
            contentValues.put(Applications.COLUMN_THUMBNAIL_URL_M, app.getAsJsonArray().get(i).getAsJsonObject().get("im:image")
                    .getAsJsonArray().get(1).getAsJsonObject().get("label").getAsString());
            //Getting biggest size image
            contentValues.put(Applications.COLUMN_THUMBNAIL_URL_B, app.getAsJsonArray().get(i).getAsJsonObject().get("im:image")
                    .getAsJsonArray().get(2).getAsJsonObject().get("label").getAsString());
            contentValues.put(Applications.COLUMN_SUMMARY, app.getAsJsonArray().get(i).getAsJsonObject().get("summary")
                    .getAsJsonObject().get("label").getAsString());
            contentValues.put(Applications.COLUMN_PRICE, app.getAsJsonArray().get(i).getAsJsonObject().get("im:price")
                            .getAsJsonObject().get("attributes").getAsJsonObject().get("amount").getAsDouble());
            contentValues.put(Applications.COLUMN_RIGHTS, app.getAsJsonArray().get(i).getAsJsonObject().get("rights")
                    .getAsJsonObject().get("label").getAsString());
            contentValues.put(Applications.COLUMN_ARTIST, app.getAsJsonArray().get(i).getAsJsonObject().get("im:artist")
                    .getAsJsonObject().get("label").getAsString());
            contentValues.put(Applications.COLUMN_CATEGORY, app.getAsJsonArray().get(i).getAsJsonObject().get("category")
                            .getAsJsonObject().get("attributes").getAsJsonObject().get("label").getAsString());
            contentValues.put(Applications.COLUMN_REALESE_DATE, app.getAsJsonArray().get(i).getAsJsonObject().get("im:releaseDate")
                    .getAsJsonObject().get("attributes").getAsJsonObject().get("label").getAsString());

            db.insert(Applications.TABLE_NAME, null, contentValues);

        }
        return true;
    }
    /**
    * End of the insert functions
    * **/

    /**
     * Functions get the info from the API
     */
    public void getCatalogo(final FutureCallback <Void> callback) throws KeyStoreException, UnrecoverableKeyException,
            NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {

        if (isNetworkAvailable()) {
            //Para SSL
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            try {
                Ion.getDefault(context);
            } catch (Exception e) {
                Log.e("error getBaseContext1", e.getMessage());

                e.printStackTrace();
            }
            try {
                Ion.getDefault(context);
            } catch (Exception e) {
                Log.e("error getBaseContext2", e.getMessage());
                e.printStackTrace();
            }
            // FIN SSL
            Ion.with(context)
                    .load(context.getResources().getString(R.string.servicio_url))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            try {
                                if (result != null) {
                                    Log.e("Completado", result.toString());
                                    Log.e("apps", String.valueOf(result.getAsJsonObject("feed").getAsJsonArray("entry").size()));
                                    Log.e("apps", String.valueOf(result.getAsJsonObject("feed").getAsJsonArray("entry")));
                                    if (result.getAsJsonObject("feed").getAsJsonArray("entry").size() == 0) {
                                        Log.e("APPS", "No hay apps q descargar");
                                    } else {
                                        insertData(result.get("feed").getAsJsonObject().get("entry").getAsJsonArray());
                                        if (callback != null)
                                            callback.onCompleted(null, null);
                                    }
                                } else {
                                    Log.e("Error", "ERROR en onCOMPLETED " + e.getMessage());
                                }

                            } catch (Exception ee) {
                                Log.e("Error", ee.getMessage());
                                Log.e("Resultado", result.toString());
                            }

                        }
                    });
        } else {
           Log.e("No hay", "conexion a internet!");
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
