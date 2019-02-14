package com.leandro.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    EditText cityName;
    TextView resultTextView;
    TextView cityNameTextView;
    TextView tempTextView;

    public void findWeather (View view){


        Log.i("NOME DA CIDADE",cityName.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=62fb396e613a62b579b46cfd41126ac5");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Clima não encontrado", Toast.LENGTH_LONG);
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        cityNameTextView = findViewById(R.id.nameTextView);
        resultTextView = findViewById(R.id.resultsTextView);
        tempTextView = findViewById(R.id.tempTextView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Clima não encontrado", Toast.LENGTH_LONG);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message="";



                //Info de Descrição do Clima

                JSONObject jsonObject = new JSONObject(result);

                //Info de Temperatura

                JSONObject jsonObject1 = new JSONObject(result);

                jsonObject1 = jsonObject1.getJSONObject("main");

                double temp = jsonObject1.getDouble("temp");

                //Info nome da Cidade

                JSONObject jsonObject2 = new JSONObject(result);
                String nameCityInfo = jsonObject2.getString("name");

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);
                Log.i("NOME AQUIIII",nameCityInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";


                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");


                    if (main !="" && description !=""){

                        message += main + ": " + description + "\r\n";



                    }


                }

                if(message !=""){

                    cityNameTextView.setText(nameCityInfo);


                    tempTextView.setText(String.format("%.0f",(temp -273.15))+ "\u2103");


                    resultTextView.setText(message);

                } else {

                    Toast.makeText(getApplicationContext(), "Clima não encontrado", Toast.LENGTH_LONG);
                }


            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Clima não encontrado", Toast.LENGTH_LONG);
            }



        }
    }

}
