package com.example.smart_container;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //    private TextView textView_result;
    private Button button;
    private ProgressBar pr;
    private TextView textView_result;
    int total_height = 12;
    private final ScheduledThreadPoolExecutor executor_ =
            new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> schedulerFuture;

    public void startScheduler() {
        schedulerFuture = executor_.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                //DO YOUR THINGS
                getTodayData();
            }
        }, 0L, 20 * 1000, TimeUnit.MILLISECONDS);
    }


    public void stopScheduler() {
        schedulerFuture.cancel(false);
        startScheduler();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getTodayData();
        startScheduler();

    }

    public void getTodayData() {
        textView_result = (TextView) findViewById(R.id.textView_result);
        button = (Button) findViewById(R.id.trend);
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
//                    textView_result.setText("Code : " + response.code());
                    return;
                }
                List<Post> posts = response.body();
                int req_size = posts.size();
                String dates = posts.get(req_size - 1).getCreation_date();
                int height = posts.get(req_size - 1).getHeight();
                Log.i("myApp", "Data Refreshed" + Integer.toString(height));
                if (total_height > height) {
                    int percentage = ((height) * 100) / total_height;
                    pr.setProgress(percentage);
                    textView_result.setText("Status : " + Integer.toString(percentage) + "/ 100");

                } else {
                    pr.setProgress(100);
                    textView_result.setText(Integer.toString(100));

                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
//                textView_result.setText(t.getMessage());
            }
        });
    }
}