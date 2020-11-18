package com.example.smart_container;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
//    private TextView textView_result;
    private Button button;
    private ProgressBar pr;
    int total_height=400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getTodayData(); //THIS GENERATES TODAY DATE AND THEN GETS IN THE posts array
        //Whatever you want to implement, implement inside the onResponse method
    }

    public void getTodayData() {
        button=(Button)findViewById(R.id.trend);
        pr=(ProgressBar)findViewById(R.id.simpleProgressBar);
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

//                for (Post post : posts) {
//                    String content = "";
//                    content += "ID:  " + post.get_id() + "\n";
//                    content += "Sensor ID:  " + post.getSensor_id() + "\n";
//                    content += "Creation Date:  " + post.getCreation_date() + "\n";
//                    content += "Height:  " + post.getHeight() + "\n\n";
////                    textView_result.append(content);
//
//
//                }

                int req_size=posts.size();
                String dates=posts.get(req_size-1).getCreation_date();
                int height=posts.get(req_size-1).getHeight();
                if(total_height>height) {
                    int percentage = ((total_height - height) * 100) / total_height;
                    pr.setProgress(percentage);
                }





            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
//                textView_result.setText(t.getMessage());
            }
        });
    }
}