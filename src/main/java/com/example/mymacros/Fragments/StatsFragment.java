package com.example.mymacros.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.text.HAlign;
import com.example.mymacros.Application;
import com.example.mymacros.Helpers.LocaleHelper;
import com.example.mymacros.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private AnyChartView anyChartView;
    private RadioGroup chartRG,timeRG;
    private FrameLayout chartLayout;

    private ProgressBar loadingPB;

    private Application myApp;

    private Context context;
    private DatabaseReference dRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        initializeComponents(view);
        chooseChartType();

        if(chartRG.getCheckedRadioButtonId() == R.id.overview_stats_SWT){
            createPieChart();
            timeRG.setVisibility(View.GONE);
        }else{
            createColumnChart();
            timeRG.setVisibility(View.VISIBLE);

        }
        return view;
    }

    private void initializeComponents(View view) {

        myApp = ((Application) requireActivity().getApplicationContext());

        context = getContext();

        dRef = myApp.getdRef();

        chartRG = view.findViewById(R.id.chart_stats_toggle);
        timeRG = view.findViewById(R.id.time_stats_toggle);
        chartLayout = view.findViewById(R.id.frame_stats_FLAY);
        loadingPB = view.findViewById(R.id.chartReady_stats_PB);

        loadingPB.setIndeterminate(true);



        resetChartView();


    }

    @Override
    public void onStop() {
        super.onStop();
        LocaleHelper.setLanguage(context,myApp.getmUser().getUid());

    }

    private void resetChartView() {
        chartLayout.removeView(anyChartView);
        anyChartView = new AnyChartView(getContext());
        anyChartView.setBackgroundColor(getResources().getColor(R.color.blue));
        anyChartView.setProgressBar(loadingPB);
        chartLayout.addView(anyChartView);
    }

    private void chooseChartType(){
        chartRG.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.overview_stats_SWT){
                resetChartView();
                timeRG.setVisibility(View.GONE);

                createPieChart();
            }else{
                resetChartView();
                timeRG.setVisibility(View.VISIBLE);

                createColumnChart();


            }
        });

        //region Change Period
        timeRG.setOnCheckedChangeListener((group, checkedId) -> {
            resetChartView();

            createColumnChart();

        });
        //endregion


    }

    private void createColumnChart() {

        //initialize Chart
        Cartesian cartesian = AnyChart.column();

        //region Texts
        String t1;
        String t2;
        String timeSTR;
        double difference;

        //1st part of title
        if(chartRG.getCheckedRadioButtonId() == R.id.calories_stats_SWT){
            t1 = context.getResources().getString(R.string.calories);
            t2 = "Calories";
        }else{
            t1 = context.getResources().getString(R.string.weight);
            t2 = "Weight";
        }

        //2nd part of title
        if(timeRG.getCheckedRadioButtonId() == R.id.week_stats_SWT){
            timeSTR = context.getResources().getString(R.string.week);
            difference = 7*86400000D;
        }else if(timeRG.getCheckedRadioButtonId() == R.id.month_stats_SWT){
            timeSTR = context.getResources().getString(R.string._1_month);
            difference = 30*86400000D;
        }else{
            timeSTR = context.getResources().getString(R.string._3_months);
            difference = 90* 86400000D;

        }

        //put in the titles
        cartesian.title(t1+" "+getString(R.string.per)+" "+timeSTR);
        cartesian.xAxis(0).title(timeSTR);
        cartesian.yAxis(0).title(t1);
        //endregion

        //region Chart Styling
        cartesian.background().fill("#25273E");
        cartesian.palette(new String[]{"green","red"});

        cartesian.title().fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(20);

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        // set the padding between columns
        cartesian.barsPadding(-0.5);

        cartesian.interactivity().hoverMode(HoverMode.SINGLE);

        cartesian.animation(true);

        cartesian.xAxis(0).title()
                .fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(20);

        cartesian.yAxis(0).title()
                .fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(20);

        //endregion

        dRef.child("Stats").child(myApp.getmUser().getUid()).child(t2).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //region Data

                Date lastDate = null;
                List<DataEntry> caloriesEaten = new ArrayList<>();
                List<DataEntry> caloriesBurnt = new ArrayList<>();
                List<DataEntry> weightMOS = new ArrayList<>();
                int cEaten =0;
                int cBurnt = 0;
                float weightMO = 0f;
                int weightDay = 0;
                Date d;
                int count = 0;
                SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");

                //region data calculations
                try {
                    for (DataSnapshot s :snapshot.getChildren()) {
                        String dtStart = s.getKey();
                        if (dtStart != null) {
                            d = format.parse(dtStart);

                            if (lastDate != null) {
                                //region If it's not the first date
                                assert d != null;

                                //region If difference between dates is smaller or equal than the selected one
                                if (d.getTime() - lastDate.getTime() <= difference) {

                                    //region If The Chart Selected Is "Calories"

                                    if(t1.equals(context.getResources().getString(R.string.calories))) {
                                        if (s.child("eaten").getValue(Integer.class) != null)
                                            cEaten += s.child("eaten").getValue(Integer.class);

                                        if (s.child("burnt").getValue(Integer.class) != null)
                                            cBurnt += s.child("burnt").getValue(Integer.class);

                                    }
                                    //endregion

                                    //region If The Chart Selected Is "Weight"
                                    else{
                                        if (s.getValue(Float.class) != null)
                                            weightMO = weightMO + s.getValue(Float.class);
                                        weightDay++;
                                    }
                                    //endregion
                                }
                                //endregion

                                //region If difference between dates is bigger than the selected one
                                else {
                                    //region If The Chart Selected Is "Calories"

                                    if(t1.equals(context.getResources().getString(R.string.calories))) {
                                        caloriesEaten.add(new ValueDataEntry(String.valueOf(count), cEaten));
                                        caloriesBurnt.add(new ValueDataEntry(String.valueOf(count), cBurnt));

                                        if (s.child("eaten").getValue(Integer.class) != null)
                                            cEaten = s.child("eaten").getValue(Integer.class);
                                        else
                                            cEaten = 0;

                                        if (s.child("burnt").getValue(Integer.class) != null)
                                            cBurnt = s.child("burnt").getValue(Integer.class);
                                        else
                                            cBurnt = 0;
                                    }
                                    //endregion

                                    //region If The Chart Selected Is "Weight"
                                    else{
                                        Float f = Float.parseFloat(String.format(Resources.getSystem().getConfiguration().locale,"%.1f",(weightMO/weightDay)));
                                        weightMOS.add(new ValueDataEntry(String.valueOf(count),f));
                                        weightDay = 1;
                                        if (s.getValue(Float.class) != null)
                                            weightMO = s.getValue(Float.class);
                                        else
                                            weightMO = 0f;
                                    }
                                    //endregion

                                    lastDate = format.parse(s.getKey());
                                    count++;
                                }
                                //endregion

                                //endregion

                            }
                            //region

                            //region If its the first date
                            else {
                                //region If The Chart Selected Is "Calories"

                                if(t1.equals(context.getResources().getString(R.string.calories))) {
                                    if (s.child("eaten").getValue(Integer.class) != null)
                                        cEaten = s.child("eaten").getValue(Integer.class);
                                    else
                                        cEaten = 0;

                                    if (s.child("burnt").getValue(Integer.class) != null)
                                        cBurnt = s.child("burnt").getValue(Integer.class);
                                    else
                                        cBurnt = 0;
                                }

                                //region If The Chart Selected Is "Weight"
                                else{
                                    weightDay = 1;
                                    if (s.getValue(Float.class) != null)
                                        weightMO = s.getValue(Float.class);
                                    else
                                        weightMO = 0f;
                                }
                                //endregion
                                lastDate = format.parse(s.getKey());

                            }
                            //endregion

                        }
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }

                //region If The Chart Selected Is "Calories"

                if(t1.equals(context.getResources().getString(R.string.calories))) {
                    caloriesEaten.add(new ValueDataEntry(count, cEaten));
                    caloriesBurnt.add(new ValueDataEntry(count, cBurnt));
                }

                //region If The Chart Selected Is "Weight"
                else{
                    Float f = Float.parseFloat(String.format(Resources.getSystem().getConfiguration().locale,"%.1f",(weightMO/weightDay)));
                    weightMOS.add(new ValueDataEntry(count, f));

                }
                //endregion
                //endregion



                Column column;
                Column column2 = null;
                //region If The Chart Selected Is "Calories"
                if(t1.equals(context.getResources().getString(R.string.calories))) {
                    column = cartesian.column(caloriesEaten);
                    column2 = cartesian.column(caloriesBurnt);
                }

                //region If The Chart Selected Is "Weight"
                else{
                    column = cartesian.column(weightMOS);
                }
                //endregion

                //region Column Styling
                //region If The Chart Selected Is "Calories"
                if(t1.equals(context.getResources().getString(R.string.calories))) {
                    column.tooltip()
                            .titleFormat("{%X}")
                            .title("Eaten")
                            .position(Position.CENTER_BOTTOM)
                            .anchor(Anchor.CENTER_BOTTOM)
                            .hAlign(HAlign.CENTER)
                            .offsetX(0d)
                            .offsetY(5d)
                            .format("{%Value}{groupsSeparator: }")
                            .fontColor("white")
                            .fontFamily("nunito")
                            .fontStyle("bold");

                    column2.tooltip()
                            .titleFormat("{%X}")
                            .title("Burnt")
                            .position(Position.CENTER_BOTTOM)
                            .anchor(Anchor.CENTER_BOTTOM)
                            .hAlign(HAlign.CENTER)
                            .offsetX(0d)
                            .offsetY(5d)
                            .format("{%Value}{groupsSeparator: }")
                            .fontColor("white")
                            .fontFamily("nunito")
                            .fontStyle("bold");
                }
                //region If The Chart Selected Is "Weight"
                else{
                    column.tooltip()
                            .titleFormat("{%X}")
                            .title("WeightAVG")
                            .position(Position.CENTER_BOTTOM)
                            .anchor(Anchor.CENTER_BOTTOM)
                            .hAlign(HAlign.CENTER)
                            .offsetX(0d)
                            .offsetY(5d)
                            .format("{%Value}{groupsSeparator: }")
                            .fontColor("white")
                            .fontFamily("nunito")
                            .fontStyle("bold");
                }
                //endregion

                //endregion

                anyChartView.setChart(cartesian);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //endregion

    }

    private void createPieChart() {
        Pie pie = AnyChart.pie();

        //region Data
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry(context.getResources().getString(R.string.protein), myApp.getReccomendedMacros("")[1]));
        data.add(new ValueDataEntry(context.getResources().getString(R.string.carbs), myApp.getReccomendedMacros("")[2]));
        data.add(new ValueDataEntry(context.getResources().getString(R.string.fat), myApp.getReccomendedMacros("")[3]));

        pie.data(data);
        //endregion

        //region Styling
        pie.background().fill("#25273E");

        pie.title().fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(20)
                .padding(0d,0d,10d,0d);

        pie.palette(new String[]{"green","red","yellow"});

        pie.labels().position("outside")
                .fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(14)
                .padding(0d,0d,4d,0d);

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Macros")
                .fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(17)
                .padding(0d,0d,1d,0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER)
                .fontColor("white")
                .fontFamily("nunito")
                .fontStyle("bold")
                .fontSize(14);

        pie.title(getString(R.string.macro_ratio));
        //endregion


        anyChartView.setChart(pie);

    }
}