package com.example.smart_container;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.graphics.drawable.shapes.RectShape;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


public class TrendsActivity extends AppCompatActivity {
    int total_height = 12;
    DatePickerDialog picker;
    EditText eText1;
    EditText eText2;
    Button btnGet;
    TextView tvw;
    private LineChart mChart;
    ArrayList<Entry> x;
    ArrayList<String> y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
        eText1 = (EditText) findViewById(R.id.editText1);
        eText2 = (EditText) findViewById(R.id.editText2);

        eText1.setInputType(InputType.TYPE_NULL);
        eText2.setInputType(InputType.TYPE_NULL);
        eText1.setGravity(Gravity.CENTER_HORIZONTAL);

        eText2.setGravity(Gravity.CENTER_HORIZONTAL);


        eText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(TrendsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                eText1.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        eText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(TrendsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dayOfMonth++;
                                eText2.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        btnGet = (Button) findViewById(R.id.get_date);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTodayData();
            }
        });
    }

    public void getTodayData() {
        tvw = findViewById(R.id.status_trend);
        tvw.setText("Status : Getting data..");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://smartcontainer-rest-api.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceholder jsonPlaceholder = retrofit.create(JsonPlaceholder.class);
        String formattedDate1 = eText1.getText().toString();
        String formattedDate2 = eText2.getText().toString();

        if (formattedDate1.equals("")) {
            setRedEditText();
            tvw.setText("Status : Could not fetch data for selected dates");

            return;
        }
        if (formattedDate2.equals("")) {
            setRedEditText();
            tvw.setText("Status : Could not fetch data for selected dates");

            return;
        }
        tvw.setText("Status : Getting data..");
        //Extract data from editDate
        Log.i("myApp", formattedDate1);
        Log.i("myApp", formattedDate2);

        Call<List<Post>> call = jsonPlaceholder.getPostsRanged(formattedDate1, formattedDate2);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                //NOW POSTS ARRAY WILL HAVE THE RANGED DATA LIST
                if (!response.isSuccessful()) {
//                    textView_result.setText("Code : " + response.code());
                    return;
                }
                setGreenEditText();
                List<Post> posts = response.body();
                Log.i("myApp", Integer.toString(posts.size()));
                if (posts.size() == 0) {
                    setRedEditText();
                    return;
                }
                int req_size = posts.size();
                String dates = posts.get(req_size - 1).getCreation_date();
                int height = posts.get(req_size - 1).getHeight();
                tvw.setText("Status : Data Fetched");

                Log.i("myApp", "Data Refreshed, total size :" + posts.size());
                x = new ArrayList<Entry>();
                y = new ArrayList<String>();
                int count = 0;
                for (int i = 0; i < posts.size(); i += 100) {
                    int heights = posts.get(i).getHeight();
                    int percentage = ((total_height - heights) * 100) / total_height;
                    String date = posts.get(i).getCreation_date();
//                    Log.i("myApp","p : " + percentage);
                    if (percentage > -1) {
                        x.add(new Entry(percentage, count++));
                        y.add(date);
                    }
                }


                mChart = (LineChart) findViewById(R.id.chart);
                mChart.setDrawGridBackground(false);
//                mChart.setDescription("");
                mChart.setTouchEnabled(true);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                mChart.setPinchZoom(true);
                mChart.getXAxis().setTextSize(12f);
                mChart.getAxisLeft().setTextSize(12f);
//        mChart.setMarkerView(mv);
                XAxis xl = mChart.getXAxis();
                xl.setAvoidFirstLastClipping(true);
                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setInverted(false);
                YAxis rightAxis = mChart.getAxisRight();
                rightAxis.setEnabled(false);
                Legend l = mChart.getLegend();
                l.setForm(Legend.LegendForm.LINE);

                LineDataSet set1 = new LineDataSet(x,"dates");
                set1.setColors(ColorTemplate.COLORFUL_COLORS);
                set1.setLineWidth(1.5f);
                set1.setCircleRadius(4f);
                LineData data = new LineData(y, set1);
                mChart.setData(data);
                mChart.invalidate();
            }


            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                tvw.setText("Status : Could not fetch data for selected dates");

                setRedEditText();
            }
        });

    }

    private void setGreenEditText() {
        eText1.setBackgroundResource(R.drawable.edittext_bg_success);
        eText2.setBackgroundResource(R.drawable.edittext_bg_success);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.GREEN);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);

        // Assign the created border to EditText widget
        eText1.setBackground(shape);
        eText2.setBackground(shape);
    }

    private void setRedEditText() {
        //on failure set editText to red
        eText1.setBackgroundResource(R.drawable.edittext_bg_fail);
        eText2.setBackgroundResource(R.drawable.edittext_bg_fail);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        // Assign the created border to EditText widget
        eText1.setBackground(shape);
        eText2.setBackground(shape);

    }
}