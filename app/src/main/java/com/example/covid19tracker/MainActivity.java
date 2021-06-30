package com.example.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covid19tracker.api.ApiUtilities;
import com.example.covid19tracker.api.CountryData;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalConfirm, totalActive, totalRecovred, totalDeath, totalTest;
    private TextView todayConfirm, todayRecovered, todayDeath, tvDate;
    private PieChart pieChart;
    private ProgressBar progressBar;

    private List<CountryData> list;

    String country = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        if (getIntent().getStringExtra("country") !=  null){
            country = getIntent().getStringExtra("country");
        }


        init();

         TextView cName = findViewById(R.id.cName);
         cName.setText(country);

        findViewById(R.id.cName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CountryActivity.class));
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        ApiUtilities.getApiInteface().getCountryData().enqueue(new Callback<List<CountryData>>() {
            @Override
            public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {

                list.addAll(response.body());



                progressBar.setVisibility(View.GONE);

                for (int i = 0; i<list.size(); i++){
                    if (list.get(i).getCountry().equals(country)){
                        int confirm = Integer.parseInt(list.get(i).getCases());
                        int active = Integer.parseInt(list.get(i).getActive());
                        int recovered = Integer.parseInt(list.get(i).getRecovered());
                        int death = Integer.parseInt(list.get(i).getDeaths());

                        totalConfirm.setText(NumberFormat.getInstance().format(confirm));
                        totalActive.setText(NumberFormat.getInstance().format(active));
                        totalRecovred.setText(NumberFormat.getInstance().format(recovered));
                        totalDeath.setText(NumberFormat.getInstance().format(death));

                        todayDeath.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())));
                        todayConfirm.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())));
                        todayRecovered.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())));
                        totalTest.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));

                        setText(list.get(i).getUpdated());

                        pieChart.addPieSlice(new PieModel("Confirm", confirm, getResources().getColor(R.color.yellow)));
                        pieChart.addPieSlice(new PieModel("Active", active, getResources().getColor(R.color.blue_pie)));
                        pieChart.addPieSlice(new PieModel("Recovered", recovered, getResources().getColor(R.color.green_pie)));
                        pieChart.addPieSlice(new PieModel("Death", death, getResources().getColor(R.color.red_pie)));

                        pieChart.startAnimation();

                    }
                }


            }


            @Override
            public void onFailure(Call<List<CountryData>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setText(String updated) {

        DateFormat format = new SimpleDateFormat("MM dd, yyyy");

        long millliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millliseconds);

        tvDate.setText("Updated at " + format.format(calendar.getTime()));

    }

    private void init(){

        totalConfirm = findViewById(R.id.totalConfimed);
        totalActive = findViewById(R.id.totalActive);
        totalRecovred = findViewById(R.id.totalRecovered);
        totalDeath = findViewById(R.id.totalDeath);
        totalTest = findViewById(R.id.totalTest);

        todayConfirm = findViewById(R.id.todayConfirmed);
        todayDeath= findViewById(R.id.todayDeath);
        todayRecovered = findViewById(R.id.todayRecovered);

        pieChart = (PieChart) findViewById(R.id.piechart);

        tvDate = findViewById(R.id.date);


        progressBar = new ProgressBar(this);

    }
}