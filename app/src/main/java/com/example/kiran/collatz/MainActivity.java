package com.example.kiran.collatz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.parseLong;


public class MainActivity extends AppCompatActivity
{

    String str_RESULT = "";   //  String to be displayed in results text box
    String str_SUM = "";      //  String to be displayed in summary text box

    int even_step = 1;   // 2^n that even number is divided by to produce odd number
    int even_count = 0;  // Count even number encountered
    int odd_count = 0;   // Count odd number encountered
    int total_count = 0;

    long even_max = 0;   // Largest even number
    long odd_max = 0;    // Largest odd number
    long over_flow = MAX_VALUE / 3;  //long int MAX_VALUE 9223372036854775807

    int line_color_odd = Color.BLUE;
    int line_color_even = Color.GREEN;
    int line_color_all = Color.RED;

    String line_label_odd = "Odd Run";
    String line_label_even = "Even Run";
    String line_label_all = "All";
    List<Entry> points_ODD = new ArrayList<Entry>();  //  odd graph data
    List<Entry> points_EVEN = new ArrayList<Entry>(); //  even graph data
    List<Entry> points_ALL = new ArrayList<Entry>();  //  even graph data

    List<Entry> series = new ArrayList<Entry>();      //  data to graph

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display_Init();  // set up GUI and wait for click
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  // Add Action Menu
    {
        getMenuInflater().inflate(R.menu.actionbar  _menu, menu);
        return true;
    }

    //
    // HAILSTONE ROUTINES
    //
    private void Calc_HailStones(long int_Input)
    {
        even_step = 1;  // 2^n that even number is divided by to produce odd number
        even_count = 0; // Count even number encountered
        odd_count = 0;  // Count odd number encountered
        even_max = 0;   // Largest even number
        odd_max = 0;    // Largest odd number
        total_count = 0;

        points_ODD.clear();  // Clear previous graph data
        points_EVEN.clear();
        points_ALL.clear();

        // Init display text views
        str_RESULT = "";
        str_SUM = "Seed: " + String.valueOf(int_Input) + '\n' + '\n';
        Display_Summary(str_SUM);
        Display_Summary(str_SUM);

        str_RESULT = str_RESULT + "  " + int_Input;  // add seed to result string

        //  Loop while number is greater than 1
        //  Loop exit when number reached one or
        //  in the event of a neg number
        while (int_Input > 1)
        {
            total_count++;
            if (int_Input % 2 == 1) // number is odd
            {
                odd_count++;
                if (odd_max < int_Input)  //  Update max odd number
                {
                    odd_max = int_Input;
                }
                str_RESULT = str_RESULT + " /" + even_step + '\n' + total_count + "  " + int_Input; //  Update result string, add 2^n
                //    from previous even run and new odd
                points_ODD.add(new Entry(total_count, int_Input));                      //  Add odd graph point
                points_ALL.add(new Entry(total_count, int_Input));                    //  Add point to graph
                if (int_Input > over_flow)  // if max int size is reached
                {
                    int_Input = (int_Input * 3) + 1;                //  Update odd number -> 3x+1
                    str_RESULT = str_RESULT + " -> " + int_Input;   //  Add new even to result string

                    // Update display with error and return
                    str_RESULT = str_RESULT + '\n' + " -> Error: Overflow <-";
                    str_SUM = str_SUM + '\n' + " -> Error: Overflow <-";
                    Display_Results(str_RESULT);
                    Display_Summary(str_SUM);
                    return;
                }
                int_Input = (int_Input * 3) + 1;                //  Update odd number -> 3x+1
                str_RESULT = str_RESULT + " -> " + int_Input;   //  Add new even to result string
                Display_Results(str_RESULT);                    //  Update screen
                even_step = 1;                                  //  New even number so reset even divisor
                //points_EVEN.add(new Entry(odd_count, int_Input));   //  Add even graph point
            }
            else  // number is even
            {
                even_count++;
                if (even_max < int_Input)  //  Update max even number
                {
                    even_max = int_Input;
                }
                points_EVEN.add(new Entry(total_count, int_Input));   //  Add even graph point
                points_ALL.add(new Entry(total_count, int_Input));
                int_Input = int_Input / 2;  // Update even number -> x/2
                even_step = even_step * 2;  // Update even divisor (2^n counter)
            }

        }
        // Process the final result of "1"
        odd_count++;
        total_count++;
        str_RESULT = str_RESULT + " /" + even_step + '\n' + total_count + "  " + int_Input;
        points_ODD.add(new Entry(total_count, int_Input));          //  Add odd graph point
        points_EVEN.add(new Entry(total_count, int_Input));   //  Add even graph point
        points_ALL.add(new Entry(total_count, int_Input));

        str_SUM = str_SUM + "Odd Count: " + odd_count + '\n' + "Even Count: " + even_count + '\n';
        str_SUM = str_SUM + "Total: " + (odd_count + even_count) + '\n' + '\n';
        str_SUM = str_SUM + "Odd Max: " + odd_max + '\n' + "Even Max: " + even_max + '\n';
        Display_Results(str_RESULT);
        Display_Summary(str_SUM);
        Display_Graph();
    }

    //
    //  onClick ROUTINES
    //
    public boolean onClick_Calcx(View view)
    {
        long int_Input = 0; // store input string as int
        Display_Init();     // New input so clear display

        TextView txt_INPUT = findViewById(R.id.num_INPUT); //attach text view to GUI object
        txt_INPUT.onEditorAction(IME_ACTION_DONE);         //Close soft keyboard

        //catch input string to int conversion failure
        try
        {
            int_Input = parseLong(txt_INPUT.getText().toString());  // Get the string from textview  and conv to int
        }
        catch (NumberFormatException e)
        {
            str_RESULT = str_RESULT + '\n' + "Error " + '\n' + e.getMessage();   //  Update results message
            Display_Results(str_RESULT);   //  Display results message
            return false;
        }
        Calc_HailStones(int_Input);  //  Process the input
        return true;
    }


    public void onClick_Graph(View view)  // Change series being graphed
    {
        Display_Graph();
        return;
    }

    public void onClick_Menu_Exit(MenuItem menuItem)
    { finish(); }

    public void onClick_Menu1(MenuItem menuItem)
    {
        Display_Graph_All();
        return;
    }

    //
    //  DISPLAY ROUTINES
    //
    public void Display_Init()
    {
        str_SUM = "Rules:" + '\n';
        str_SUM = str_SUM + "ODD NUMBER: " + '\n' + "   multiply by 3 add 1" + '\n';
        str_SUM = str_SUM + "EVEN NUMBER:" + '\n' + "   divide by 2" + '\n';
        str_SUM = str_SUM + "RESULT = 1" + '\n' + '\n';
        str_RESULT = "Limits:" + '\n' + "odd number < " + (MAX_VALUE / 3) + '\n' + "even number < " + MAX_VALUE;

        points_ODD.clear();
        points_EVEN.clear();
        points_ALL.clear();
        points_ODD.add(new Entry(0, 0));
        points_EVEN.add(new Entry(0, 0));
        points_ALL.add(new Entry(0, 0));

        Display_Graph();
        Display_Summary(str_SUM);
        Display_Results(str_RESULT);
        return;
    }

    private void Display_Graph_All()
    {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);  // select series to graph

        LineChart chart = (LineChart) findViewById(R.id.chart);   // Create chart and attach GUI object
        chart.getAxisRight().setDrawLabels(false);                // Turn off right lables
        chart.getAxisLeft().setDrawLabels(false);
        chart.invalidate();        //  Update chart
        chart.getAxisLeft().setDrawLabels(true);
        chart.getAxisLeft().setGranularity(1f);                   // Set axis to whole numbers
        chart.getXAxis().setGranularity(1f);
        chart.fitScreen();
        chart.getDescription().setEnabled(false);

        // configure line data for each series
        LineDataSet linedataset_all = new LineDataSet(points_ALL, line_label_all);  // create series and attach graph points
        linedataset_all.setAxisDependency(YAxis.AxisDependency.LEFT);  //  Series yaxis config
        linedataset_all.setDrawValues(false);                          //  No labels on data points
        linedataset_all.setColor(line_color_all);                         //  Set line color
        linedataset_all.setCircleColor(line_color_all);
        linedataset_all.setCircleRadius(2f);
        linedataset_all.setDrawCircleHole(false);

        LineDataSet linedataset_even = new LineDataSet(points_EVEN, line_label_even);  // create series and attach graph points
        linedataset_even.setAxisDependency(YAxis.AxisDependency.LEFT);  //  Series yaxis config
        linedataset_even.setDrawValues(false);                          //  No labels on data points
        linedataset_even.setColor(line_color_even);                          //  Set line color
        linedataset_even.setCircleColor(line_color_even);
        linedataset_even.setCircleRadius(2f);
        linedataset_even.setDrawCircleHole(false);

        LineDataSet linedataset_odd = new LineDataSet(points_ODD, line_label_odd);  // create series and attach graph points
        linedataset_odd.setAxisDependency(YAxis.AxisDependency.LEFT);  //  Series yaxis config
        linedataset_odd.setDrawValues(false);                          //  No labels on data points
        linedataset_odd.setColor(line_color_odd);                          //  Set line color
        linedataset_odd.setCircleColor(line_color_odd);
        linedataset_odd.setCircleRadius(2f);
        linedataset_odd.setDrawCircleHole(false);

        //  group line data into one array
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(linedataset_even);
        dataSets.add(linedataset_odd);
        dataSets.add(linedataset_all);

        // Create chart lines from group of line data
        LineData data = new LineData(dataSets);

        chart.setData(data);   //  Graph chart lines to chart
        chart.invalidate();    //  Update chart
        return;
    }

    private void Display_Graph()
    {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);  // select series to graph

        String label = line_label_odd;  // Default graph is odd data
        series = points_ODD;
        int line_color = line_color_odd;
        String line_label = line_label_odd;
        if (toggle.isChecked())  // if checked Graph even data
        {
            series = points_EVEN;
            label = line_label_even;
            line_color = line_color_even;

        }

        LineChart chart = (LineChart) findViewById(R.id.chart);   // Create chart and attach GUI object
        chart.getAxisRight().setDrawLabels(false);                // Turn off right lables
        chart.getAxisLeft().setDrawLabels(false);
        chart.invalidate();        //  Update chart
        chart.getAxisLeft().setDrawLabels(true);

        chart.getAxisLeft().setMinWidth(1f);
        chart.getAxisLeft().setGranularity(1f);                   // Set axis to whole numbers
        chart.getXAxis().setGranularity(1f);
        chart.fitScreen();
        chart.getDescription().setEnabled(false);

        LineDataSet dataSet = new LineDataSet(series, label);  // create series and attach graph points
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);  //  Series yaxis config
        dataSet.setDrawValues(false);                          //  No labels on data points
        dataSet.setColor(line_color);                          //  Set line color
        dataSet.setCircleColor(line_color);
        dataSet.setCircleRadius(2f);
        dataSet.setDrawCircleHole(false);

        LineData lineData = new LineData(dataSet);  // create chartline and attach series

        chart.setData(lineData);   //  Add chartlines to chart
        chart.invalidate();        //  Update chart
        return;
    }

    public void Display_Results(String txt_DISPLAY)
    {
        TextView textView = findViewById(R.id.txtResults);
        textView.setText(txt_DISPLAY);
        return;
    }

    public void Display_Summary(String txt_DISPLAY)
    {
        TextView textView = findViewById(R.id.txtSum);
        textView.setText(txt_DISPLAY);
        return;
    }
}
