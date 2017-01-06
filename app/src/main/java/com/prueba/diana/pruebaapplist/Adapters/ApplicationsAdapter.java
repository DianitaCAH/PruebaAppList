package com.prueba.diana.pruebaapplist.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prueba.diana.pruebaapplist.Models.Applications;
import com.prueba.diana.pruebaapplist.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dianaacosta on 11/17/16.
 */

public class ApplicationsAdapter extends BaseAdapter {

    private Activity activity;
    List<Applications> arrayitms;
    Map<String, Bitmap> imagesCache;
    Map<String, Boolean> descargado;

    public ApplicationsAdapter(Activity activity, List<Applications> listarray) {
        super();
        this.activity = activity;
        this.arrayitms = listarray;
        imagesCache = new HashMap<String, Bitmap>();
        descargado = new HashMap<String, Boolean>();
    }


    @Override
    public int getCount() {
        return arrayitms.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayitms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Applications item = arrayitms.get(position);
        //if (convertView == null) {
        convertView = inflater.inflate(R.layout.item_list, null);
        //}
        if (item != null) {

            TextView textView = (TextView) convertView.findViewById(R.id.Tittle);
            textView.setText(item.getTitle());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            Log.e("URL ", item.getPictureM());
            if (imagesCache.get(item.getPictureM()) == null ) {
                if (descargado.get(item.getPictureM()) == null) {
                    Log.e("DESCARGAR ", String.valueOf(position));
                    descargado.put(item.getPictureM(), true);
                    imageView.setImageBitmap(setCircleBit(
                            BitmapFactory.decodeResource(activity.getResources(),
                            R.drawable.app_icon))
                    );
                    new DownloadImage(imageView, item.getPictureM()).execute();
                }
            } else {
                Log.e("REUTILIZAR ", String.valueOf(position));
                imageView.setImageBitmap(imagesCache.get(item.getPictureM()));
            }

        }


        return convertView;
    }

    /**
     * Method that cuts the bitmap and make it circle
     * returns Bitmap
     * */
    private Bitmap setCircleBit(Bitmap imagen) {

        Bitmap circleBitmap = Bitmap.createBitmap(imagen.getWidth(), imagen.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(imagen,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(imagen.getWidth()/2, imagen.getHeight()/2, imagen.getWidth()/2, paint);

        return circleBitmap;
    }

    private class DownloadImage extends AsyncTask {

        ImageView imageView;
        String url;

        public DownloadImage(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            imageView.setImageBitmap(setCircleBit(BitmapFactory.decodeResource(activity.getResources(),
                                                    R.drawable.app_icon))
            );

        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{

                return setCircleBit(downloadImage(url));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            imageView.setImageBitmap((Bitmap) o);
            imagesCache.put(url, (Bitmap) o);
            notifyDataSetChanged();
            Log.e("Adapter", String.valueOf("HOLAAA"+imageView!=null));
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
            //Log.e("IMAGEN ", String.valueOf(bitmap));
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
