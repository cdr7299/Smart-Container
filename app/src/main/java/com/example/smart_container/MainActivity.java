package com.example.smart_container;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //    private TextView textView_result;

    private ProgressBar pr;
    private TextView textView_result;
    int total_height = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getTodayData();
        Button button_trends;
        Button button_refresh;
//        startScheduler();
        button_trends = (Button) findViewById(R.id.trend);
        button_refresh = (Button) findViewById(R.id.button_refresh);

        button_trends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrendsActivity.class);
                startActivity(intent);
            }
        });

        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTodayData();
            }
        });
    }

    public void getTodayData() {
        textView_result = (TextView) findViewById(R.id.textView_result);
        textView_result.setGravity(Gravity.CENTER | Gravity.BOTTOM);

        textView_result.setText(R.string.string_refresh);
        pr = (ProgressBar) findViewById(R.id.simpleProgressBar);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://smartcontainer-rest-api.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceholder jsonPlaceholder = retrofit.create(JsonPlaceholder.class);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        Log.i("myApp", formattedDate);
        Call<List<Post>> call = jsonPlaceholder.getPosts(formattedDate);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    textView_result.setText("Error Code : " + response.code());
                    return;
                }
                List<Post> posts = response.body();
                Log.i("myApp", Integer.toString(posts.size()));

                if (posts.size() == 0) {
                    textView_result.setTextColor(Color.parseColor("#FF0000"));
                    textView_result.setText(R.string.warning_string_no_data);
                    return;
                }
                int req_size = posts.size();
                String dates = posts.get(req_size - 1).getCreation_date();
                int height = posts.get(req_size - 1).getHeight();
                Log.i("myApp", "Data Refreshed" + Integer.toString(height));

                if (total_height > height) {
                    int percentage = ((height) * 100) / total_height;
                    pr.setProgress(percentage);
                    textView_result.setTextColor(Color.parseColor("#00F400"));
                    textView_result.setText("Status : " + percentage + "% out of 100 " + "Used!");

                } else {
                    pr.setProgress(100);
                    textView_result.setTextColor(Color.parseColor("#FF0000"));
                    textView_result.setText("Status : " + 100 + "% Used");

                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textView_result.setText(t.getMessage());
            }
        });
    }
}