package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.*;


public class GraphActivity extends AppCompatActivity {
    private ProductDataSource ds;
    public Context context;
    //calls to database
    ProductDBHelper mDBHlpr;
    String mResults;
    String mQuantity;
    //Date[] Results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ds = new ProductDataSource(this);
        initPieChart();
        //initBarChart();

        int revenueLineID = R.id.lineChartRevenue;
        int quantityLineID = R.id.lineChartQuantity;
        initLineChart(7, true, revenueLineID);
        initLineChart(7, false, quantityLineID);


        mDBHlpr = new ProductDBHelper(this);
        //mResults = new String[256];

    }

    private void initPieChart() {
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        ds.open();
        List<PieEntry> chartData = ds.getPieChartData();
        ds.close();
        PieDataSet dataSet = new PieDataSet(chartData, "Categories");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }


//    private void initBarChart(){
//        BarChart barChart = (BarChart) findViewById(R.id.barChart);
//        ds.open();
//        List<BarEntry> chartData = ds.getBarChartData(30);
//        ds.close();
//        BarDataSet dataSet = new BarDataSet(chartData, "Categories");
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        BarData barData = new BarData(dataSet);
//        barChart.setData(barData);
//        barChart.animateY(1000);
//        barChart.invalidate();
//    }

    //    private void initLineChart(){
//        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
//        context = getApplicationContext();
//        ds.open();
//        LineData lineData = new LineData(ds.getCategoryLineChartData(context,7));
//        ds.close();
//        lineChart.setData(lineData);
//        lineChart.animateY(1000);
//        lineChart.invalidate();
//    }


    private void initLineChart(int numberOfDays, boolean isProfit, int viewID){

        LineChart lineChart = (LineChart) findViewById(viewID);
        context = getApplicationContext();
        ds.open();
        LineData lineData = new LineData(ds.getCategoryLineChartData(context, numberOfDays, isProfit));
        ds.close();
        lineChart.setData(lineData);
        lineChart.animateY(1000);

        // add description of chart
        Description description = new Description();
        if(isProfit){
            description.setText("Revenue");
        }else{
            description.setText("Quantity");
        }
        description.setTextColor(Color.BLUE);
        description.setTextSize(20);
        lineChart.setDescription(description);


        //set xaxis to bottom of graph and have chart lines on an interval of 1.
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);


        SimpleDateFormat testFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<String> mResults = new ArrayList();
        ds.open();
        mResults = ds.getDates(context, numberOfDays);
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
                    return xaxisnames[(int) value];
                }

            });
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //lineChart.setViewPortOffsets(60, 0, 50, 60);

        //redraw chart
        lineChart.invalidate();


    }

    private String[] getDateStrings(Date[] dates){
        String[] dateString = new String[dates.length];
        for(int i = 0; i < dates.length; i++){
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            String formatDate = formatter.format(dates[i]);
            dateString[i] = formatDate;
        }
        return dateString;
    }
}


