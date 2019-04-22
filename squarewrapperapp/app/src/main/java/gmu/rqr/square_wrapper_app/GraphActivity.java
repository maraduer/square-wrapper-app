package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.Description;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.*;
import java.util.TimeZone;


public class GraphActivity extends AppCompatActivity implements DateRangeDialog.SaveDateListener {
    private ProductDataSource ds;
    public Context context;

    private boolean settingStartDate;
    private boolean settingEndDate;
    private Calendar queryStartDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
    private Calendar queryEndDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
    private int revenueLineID = R.id.lineChartRevenue;
    private int quantityLineID = R.id.lineChartQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ds = new ProductDataSource(this);
        queryStartDate.add(Calendar.DAY_OF_MONTH, -7);
        initPieChart();
        initLineChart(true, revenueLineID);
        initLineChart(false, quantityLineID);
        initStartDateButton();
        initEndDateButton();
        initRedrawButton();
    }

    @Override
    public void didFinishDateRangeDialog(Calendar selectedTime) {
        if(settingStartDate){
            queryStartDate = selectedTime;
            TextView startDateBtn = (TextView) findViewById(R.id.selectStartDate);
            startDateBtn.setText(DateFormat.format("MM/dd/yyyy", selectedTime.getTimeInMillis()).toString());
            settingStartDate = false;
        }
        if(settingEndDate){
            queryEndDate = selectedTime;
            TextView endDateBtn = (TextView) findViewById(R.id.selectEndDate);
            endDateBtn.setText(DateFormat.format("MM/dd/yyyy", selectedTime.getTimeInMillis()).toString());
            settingEndDate = false;
        }
    }

    private void initStartDateButton(){
        //TextView startDateBtn = (TextView) findViewById(R.id.selectStartDate);
        //startDateBtn.setText(DateFormat.format("MM/dd/yyyy", queryStartDate.getTimeInMillis()).toString());
        settingStartDate = false;
        Button startDateBtn = (Button) findViewById(R.id.selectStartDate);
        startDateBtn.setText(DateFormat.format("MM/dd/yyyy", queryStartDate.getTimeInMillis()).toString());
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DateRangeDialog dpd = new DateRangeDialog();
                settingStartDate = true;
                dpd.show(fm, "Select Start Date");
            }
        });
    }

    private void initEndDateButton(){
        //TextView endDateBtn = (TextView) findViewById(R.id.selectEndDate);
        //endDateBtn.setText(DateFormat.format("MM/dd/yyyy", queryEndDate.getTimeInMillis()).toString());
        settingEndDate = false;
        Button endDateButton = (Button) findViewById(R.id.selectEndDate);
        endDateButton.setText(DateFormat.format("MM/dd/yyyy", queryEndDate.getTimeInMillis()).toString());
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingEndDate = true;
                FragmentManager fm = getFragmentManager();
                DateRangeDialog dpd = new DateRangeDialog();
                dpd.show(fm, "Select End Date");
            }
        });
    }

    private void initRedrawButton(){
        Button redrawCharts = (Button) findViewById(R.id.redrawCharts);
        redrawCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPieChart();
                initLineChart(true, revenueLineID);
                initLineChart(false, quantityLineID);
            }
        });
    }

    private void initPieChart() {
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        ds.open();
        List<PieEntry> chartData = ds.getPieChartData(queryStartDate, queryEndDate);
        ds.close();
        PieDataSet dataSet = new PieDataSet(chartData, "Categories");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        Description description = new Description();
        description.setText("Revenue by Category");
        description.setTextColor(Color.BLUE);
        description.setTextSize(20);
        pieChart.setDescription(description);
        pieChart.invalidate();
    }



        private void initLineChart(boolean isProfit, int viewID){
        LineChart lineChart = (LineChart) findViewById(viewID);
        context = getApplicationContext();
        ds.open();
        LineData lineData = new LineData(ds.getCategoryLineChartData(context, queryStartDate, queryEndDate, isProfit));
        ds.close();
        lineChart.setData(lineData);
        lineChart.animateY(1000);

        // add description of chart
        Description description = new Description();
        if(isProfit){
            description.setText("Revenue ($)");
        }else{
            description.setText("Quantity (lbs)");
        }
        description.setTextColor(Color.BLUE);
        description.setTextSize(20);
        lineChart.setDescription(description);


        //set xaxis to bottom of graph and have chart lines on an interval of 1.
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);


        SimpleDateFormat testFormat = new SimpleDateFormat("MM/dd/yy");
        List<String> mResults = new ArrayList();
        ds.open();
        mResults = ds.getDates(queryStartDate, queryEndDate);
        ds.close();

        try {

            Date[] datesArray = new Date[mResults.size()];

            for (int i = 0; i < datesArray.length; i++) {
                Date prodDates = testFormat.parse(mResults.get(i));
                datesArray[i] = prodDates;
            }
            final String[] xaxisnames = getDateStrings(datesArray);
            XAxis xval = lineChart.getXAxis();
            xval.setDrawLabels(true);
            xval.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    if(xaxisnames.length > (int) value) {
                        return xaxisnames[(int) value];
                    } else return "";
                }

            });
        } catch (ParseException e) {
            e.printStackTrace();
        }


        lineChart.notifyDataSetChanged();
        //redraw chart
        lineChart.invalidate();
    }


    private String[] getDateStrings(Date[] dates){
        String[] dateString = new String[dates.length];
        for(int i = 0; i < dates.length; i++){
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
            String formatDate = formatter.format(dates[i]);
            dateString[i] = formatDate;
        }
        return dateString;
    }
}


