package com.nli.nathanweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView mDate,mCity,mTemp,mDescription,mTempFeel,mHumidity,mTempMax,mTempMin,mWind,mPressure,mWindDirection;
    ImageView imgIcon;
    String maVille="Toronto";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue= Volley.newRequestQueue(this);

        mDate=findViewById(R.id.mDate);
        mCity=findViewById(R.id.mCity);
        mTemp=findViewById(R.id.mTemp);
        mDescription=findViewById(R.id.mDescription);
        mTempFeel=findViewById(R.id.mTempFeel);
        mHumidity=findViewById(R.id.mHumidity);
        mTempMax=findViewById(R.id.mTempMax);
        mTempMin=findViewById(R.id.mTempMin);
        mWind=findViewById(R.id.mWind);
        mPressure=findViewById(R.id.mPressure);
        mWindDirection=findViewById(R.id.mWindDirection);
        afficher();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recherche, menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Écrire le nom de la ville");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                maVille=query;
                afficher();
                //gestion du clavier
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null)
                {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    public void afficher()
    {
        String url="http://api.openweathermap.org/data/2.5/weather?q="+ maVille+ "&appid=3a2a0a5329a6e90470e01d1e121f8864&units=metric";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object=response.getJSONObject("main");
                    JSONObject wind_object=response.getJSONObject("wind");
                    JSONArray array=response.getJSONArray("weather");
                    //Log.d("Tag","resultat = "+array.toString());
                    JSONObject object = array.getJSONObject(0);
                    //temperature
                    int tempC=(int)Math.round(main_object.getDouble("temp"));
                    String temp=String.valueOf(tempC);
                    //feels like
                    int tempF=(int)Math.round(main_object.getDouble("feels_like"));
                    String tempFeelsLike=String.valueOf(tempF);
                    //humidity
                    int hum=(int)Math.round(main_object.getDouble("humidity"));
                    String humidity=String.valueOf(hum);
                    int max=(int)Math.round(main_object.getDouble("temp_max"));
                    String maxTemp=String.valueOf(max);
                    int min=(int)Math.round(main_object.getDouble("temp_min"));
                    String minTemp=String.valueOf(min);
                    int hPa=(int)Math.round(main_object.getDouble("pressure"));
                    String pressure=String.valueOf(hPa);

                    //wind
                    int speed=(int)Math.round(wind_object.getDouble("speed"));
                    String windSpeed=String.valueOf(speed);
                    int direction=(int)Math.round(wind_object.getDouble("deg"));
                    String windDirection=String.valueOf(direction);

                    String description=object.getString("description");
                    String city=response.getString("name");
                    String icon=object.getString("icon");
                    //mettre valeurs dans les cjamps
                    mCity.setText(city);
                    mTemp.setText(temp);
                    mDescription.setText(description);
                    mTempFeel.setText(tempFeelsLike);
                    mHumidity.setText(humidity);
                    mTempMax.setText(maxTemp);
                    mTempMin.setText(minTemp);
                    mWind.setText(windSpeed);
                    mWindDirection.setText(windDirection);
                    mPressure.setText(pressure);

                    //formattage du temps
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEEE, MMMM dd");
                    String formatted_date=simpleDateFormat.format(calendar.getTime());

                    mDate.setText(formatted_date);
                    //gestion de l'image
                    String imageUri="http://openweathermap.org/img/w/"+ icon+ ".png";
                    imgIcon=findViewById(R.id.imgIcon);
                    Uri myUri= Uri.parse(imageUri);
                    Picasso.with(MainActivity.this).load(myUri).resize(200,200).into(imgIcon);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue=Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);


    }
}