package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    TextView editText;
    TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        mainText = findViewById(R.id.mainTextView);

    }

    protected void lookupWeather(View view){
        try{
            DownloadWeatherData task = new DownloadWeatherData();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName +
                    "&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT).show();
        }
    }

    protected class DownloadWeatherData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url = null;
            HttpURLConnection connection = null;

            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inbound = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inbound);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                String message = "";
                for(int i=0; i<arr.length(); i++){

                    JSONObject partInfo =arr.getJSONObject(i);

                    String main = partInfo.getString("main");
                    String description = partInfo.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message += main + " : " + description +"\r\n";
                    }
                }

                if(!message.equals("")){
                    mainText.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
