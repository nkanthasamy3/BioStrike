package com.example.biostrike;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PastActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_screen);

        // spinner for selecting body part
        final Spinner dataSpinner = (Spinner) findViewById(R.id.pastProgSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PastActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Parts));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataSpinner.setAdapter(spinnerAdapter);
        String selectedValue = dataSpinner.getSelectedItem().toString();

        // take data from csv file and put into dataSamples ArrayList
        readPastSessionData();

        // graph data
        int y, x;
        x = 0;
        final GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        for (int i = 0; i < dataSamples.size(); i++) {
            x = dataSamples.get(i).getSessionID();
            if (selectedValue.equals("Left Leg")) {
                y = dataSamples.get(i).getLeftLegStrike();
            } else if (selectedValue.equals("Right Leg")) {
                y = dataSamples.get(i).getRightLegStrike();
            } else if (selectedValue.equals("Right Hand")) {
                y = dataSamples.get(i).getRightHandStrike();
            } else {
                y = dataSamples.get(i).getLeftHandStrike();
            }
            series.appendData(new DataPoint(x, y), true, dataSamples.size());
        }
        graph.addSeries(series);
        // update graph data based on what was selected to be shown
        dataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                graph.removeAllSeries();
                int y, x;
                String selectedValue = dataSpinner.getSelectedItem().toString();
                series = new LineGraphSeries<DataPoint>();
                for (int i = 0; i < dataSamples.size(); i++) {
                    x = dataSamples.get(i).getSessionID();
                    if (selectedValue.equals("Left Leg")) {
                        y = dataSamples.get(i).getLeftLegStrike();
                    } else if (selectedValue.equals("Right Leg")) {
                        y = dataSamples.get(i).getRightLegStrike();
                    } else if (selectedValue.equals("Right Hand")) {
                        y = dataSamples.get(i).getRightHandStrike();
                    } else {
                        y = dataSamples.get(i).getLeftHandStrike();
                    }
                    series.appendData(new DataPoint(x, y), true, dataSamples.size());
                }
                graph.addSeries(series);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // bottom bar buttons
        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomescreenActivity.class);
                startActivity(intent);
            }
        });
        Button currButton = (Button) findViewById(R.id.currentButton);
        currButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CurrentActivity.class);
                startActivity(intent);
            }
        });

        Button goalsButton = (Button) findViewById(R.id.goalsButton);
        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),GoalsActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<SampleData> dataSamples = new ArrayList<>();
    private void readPastSessionData() {
        InputStream is = getResources().openRawResource(R.raw.past_session);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line;
        try {
            reader.readLine(); // skip headers
            while( (line = reader.readLine()) != null) {
                // split by commas
                String[] splits = line.split(",");
                // read data
                SampleData sample = new SampleData();
                sample.setSessionID(Integer.parseInt(splits[0]));
                sample.setSesssionDate(splits[1]);
                sample.setLeftHandStrike(Integer.parseInt(splits[2]));
                sample.setRightHandStrike(Integer.parseInt(splits[3]));
                sample.setLeftLegStrike(Integer.parseInt(splits[4]));
                sample.setRightLegStrike(Integer.parseInt(splits[5]));
                dataSamples.add(sample);
            }
        } catch (IOException e) {
            Log.wtf("PastActivity", "Error reading past session csv file", e);
            e.printStackTrace();
        }
    }
}
