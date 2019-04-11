package gmu.rqr.square_wrapper_app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    private ProductDataSource ds;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ds = new ProductDataSource(this);
        initPieChart();
        //initBarChart();
        initLineChart();
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

    private void initLineChart(){
        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        context = getApplicationContext();
        ds.open();
        LineData lineData = new LineData(ds.getCategoryLineChartData(context,7));
        ds.close();
        lineChart.setData(lineData);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

}
