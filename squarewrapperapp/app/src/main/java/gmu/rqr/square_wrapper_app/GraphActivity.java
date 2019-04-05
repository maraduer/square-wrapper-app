package gmu.rqr.square_wrapper_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPieChart();
    }

    private void initPieChart() {
        setContentView(R.layout.activity_graph);
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        List<PieEntry> chartData = new ArrayList<PieEntry>();
        chartData.add(new PieEntry(33, "Chicken"));
        chartData.add(new PieEntry(45, "Pork"));
        chartData.add(new PieEntry(12, "Beef"));
        PieDataSet dataSet = new PieDataSet(chartData, "Categories");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
