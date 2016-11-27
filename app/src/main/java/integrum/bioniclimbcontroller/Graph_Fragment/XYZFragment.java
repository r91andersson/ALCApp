package integrum.bioniclimbcontroller.Graph_Fragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import integrum.bioniclimbcontroller.Database.XyzDbHelper;
import integrum.bioniclimbcontroller.MainActivity;
import integrum.bioniclimbcontroller.R;
import integrum.bioniclimbcontroller.ThreadUtils;

/**
 * Created by Robin on 2016-09-29.
 */
public class XYZFragment extends Fragment {
    @Nullable
    private LineChart mChart;
    private LineChart mRollChart;
    private LineChart mPitchChart;
    private LineChart mYawChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private int nChannels=6;
    private int nDoF=3;
    private int rollCount=0;
    private int pitchCount=0;
    private int yawCount=0;
    private String threadName;
    ArrayList<String> xRollVals = new ArrayList<String>();
    ArrayList<Entry> yRollVals = new ArrayList<Entry>();
    ArrayList<String> xPitchVals = new ArrayList<String>();
    ArrayList<Entry> yPitchVals = new ArrayList<Entry>();
    ArrayList<String> xYawVals = new ArrayList<String>();
    ArrayList<Entry> yYawVals = new ArrayList<Entry>();


    // Initiate a new thread utils object
    ThreadUtils utilsThread = new ThreadUtils();



    XyzDbHelper xyzdb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_xyz, container, false);
        setupRollChart(view);
        setupPitchChart(view);
        setupYawChart(view);
        Button generateData = (Button) view.findViewById(R.id.generateData);

        generateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

                if (!memoryInfo.lowMemory) {
                    generateDataForAllCharts();
                }

            }
        });

        xyzdb = new XyzDbHelper(getActivity());
        // mChart = (LineChart) view.findViewById(R.id.orientationChart);

       // ListView lv = (ListView) view.findViewById(R.id.listView1);
        XYZThread XYZThread = new XYZThread();
       // ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        //list.add(new LineChartItem(generateDataLine(0), view.getContext()));
        //list.add(new LineChartItem(generateDataLine(1), view.getContext()));
        //list.add(new LineChartItem(generateDataLine(2), view.getContext()));

        //ChartDataAdapter cda = new ChartDataAdapter(view.getContext(), list);
        //lv.setAdapter(cda);
       // generateDataForAllCharts();
        // Only start thread if we are connected with the bluetooth
        if(MainActivity.deviceConnected) {

            XYZThread.setThreadIsRunning(true);
            XYZThread.start();
        }
        return view;
    }

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public void generateDataForAllCharts(){
        for (int i=0;i<100;i++){
            addEntry(mRollChart, generateRandomData(-180, 180));
            addEntry(mPitchChart, generateRandomData(-180, 180));
            addEntry(mYawChart, generateRandomData(-180, 180));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void setupRollChart(View view){
        mRollChart = (LineChart) view.findViewById(R.id.rollChart);
        mRollChart.setId(R.id.rollChart);
        mRollChart.setTouchEnabled(true);
        mRollChart.setDragEnabled(true);
        mRollChart.setScaleEnabled(true);
        mRollChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mRollChart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        mRollChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mRollChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = mRollChart.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mRollChart.getAxisLeft();
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(185f);
        leftAxis.setAxisMinValue(-185f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mRollChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        mRollChart.setData(data2);
        mRollChart.setDescription("");
    }

    public void setupPitchChart(View view){
        mPitchChart = (LineChart) view.findViewById(R.id.pitchChart);
        mPitchChart.setId(R.id.pitchChart);
        mPitchChart.setTouchEnabled(true);
        mPitchChart.setDragEnabled(true);
        mPitchChart.setScaleEnabled(true);
        mPitchChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mPitchChart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.GREEN);

        // add empty data
        mPitchChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mPitchChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = mPitchChart.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mPitchChart.getAxisLeft();
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(185f);
        leftAxis.setAxisMinValue(-185f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mPitchChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        mPitchChart.setData(data2);
        mPitchChart.setDescription("");
    }

    public void setupYawChart(View view) {
        mYawChart = (LineChart) view.findViewById(R.id.yawChart);
        mYawChart.setId(R.id.yawChart);
        mYawChart.setTouchEnabled(true);
        mYawChart.setDragEnabled(true);
        mYawChart.setScaleEnabled(true);
        mYawChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mYawChart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mYawChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mYawChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = mYawChart.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mYawChart.getAxisLeft();
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(185f);
        leftAxis.setAxisMinValue(-185f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mYawChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        mYawChart.setData(data2);
        mYawChart.setDescription("");
    }

    protected void addEntry(LineChart mTmpChart, float value) {

        Calendar c = Calendar.getInstance();
        LineData data = mTmpChart.getData();
       // ILineDataSet set = data.getDataSetByIndex(0);

        int seconds = c.get(Calendar.SECOND);
        mTmpChart.getId();

        int id = mTmpChart.getId();

        switch (id){
            case R.id.rollChart:
                xRollVals.add(String.valueOf(seconds));
                yRollVals.add(new Entry(value, rollCount));
                rollCount++;
                LineDataSet setRoll;

                // create a dataset and give it a type
                setRoll = new LineDataSet(yRollVals, "Roll");
                setRoll.setDrawValues(false);
               // setRoll.setFillColor(Color.BLACK);
                setRoll.setLineWidth(2f);
                setRoll.setCircleRadius(0f);
                setRoll.setDrawFilled(false);

                //setRoll.setFillColor(Color.BLACK);
                int colRoll = Color.rgb(0, 0, 255);
                setRoll.setColor(colRoll, 100);
                // create a data object with the dataset
                data = new LineData(xRollVals, setRoll);

                // set data
                mTmpChart.setData(data);
                // let the chart know it's data has changed
                mTmpChart.notifyDataSetChanged();

                // limit the number of visible entries
                mTmpChart.setVisibleXRangeMaximum(120);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                mTmpChart.moveViewToX(data.getXValCount() - 121);
                break;
            case R.id.pitchChart:
                xPitchVals.add(String.valueOf(seconds));
                yPitchVals.add(new Entry(value, pitchCount));
                pitchCount++;
                LineDataSet setPitch;

                // create a dataset and give it a type
                setPitch = new LineDataSet(yPitchVals, "Pitch");
                setPitch.setDrawValues(false);
                //setPitch.setFillColor(Color.BLACK);
                setPitch.setLineWidth(2f);
                setPitch.setCircleRadius(0f);
                setPitch.setDrawFilled(false);

               // setPitch.setFillColor(Color.BLACK);
                int colPitch = Color.rgb(0, 255, 0);
                setPitch.setColor(colPitch, 100);
                // create a data object with the dataset
                data = new LineData(xPitchVals, setPitch);

                // set data
                mTmpChart.setData(data);
                // let the chart know it's data has changed
                mTmpChart.notifyDataSetChanged();

                // limit the number of visible entries
                mTmpChart.setVisibleXRangeMaximum(120);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                mTmpChart.moveViewToX(data.getXValCount() - 121);
                break;
            case R.id.yawChart:
                xYawVals.add(String.valueOf(seconds));
                yYawVals.add(new Entry(value, yawCount));
                yawCount++;
                LineDataSet setYaw;

                // create a dataset and give it a type
                setYaw = new LineDataSet(yYawVals, "Yaw");
                setYaw.setDrawValues(false);
                setYaw.setFillColor(Color.BLACK);
                setYaw.setLineWidth(2f);
                setYaw.setCircleRadius(0f);
                setYaw.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int colYaw = Color.rgb(255, 0, 0);
                setYaw.setColor(colYaw, 100);
                // create a data object with the dataset
                data = new LineData(xYawVals, setYaw);

                // set data
                mTmpChart.setData(data);
                // let the chart know it's data has changed
                mTmpChart.notifyDataSetChanged();

                // limit the number of visible entries
                mTmpChart.setVisibleXRangeMaximum(120);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                mTmpChart.moveViewToX(data.getXValCount() - 121);
                break;
        }



    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    public int generateRandomData(int n1, int n2){
        Random r = new Random();
        return r.nextInt(n2 - n1) + n1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ThreadUtils utilsThread = new ThreadUtils();
        Thread current=utilsThread.getThreadByName(threadName);

        if(current!=null) {
            while (current.isAlive()) {
                current.interrupt();
            }
        }

    }

    public class XYZThread extends Thread {

        private boolean threadIsRunning = false;


        public void setThreadIsRunning(boolean running) {
            threadIsRunning = running;
            int verify=1;
        }

        public void stopThread () {
            setThreadIsRunning(false);
        }

        public void startThread() {
            setThreadIsRunning(true);
        }



        @Override
        public void run() {

            // Get the thread name
            threadName=utilsThread.getThreadName();

            // Previous ID for database entry
            String prevId="null";

            // Store the thread name inside shared preferences_basic
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("XYZThreadName", threadName);
            editor.apply();

            while (threadIsRunning) {
                try {
                    sleep(50);
                    String info= utilsThread.getThreadSignature();

                    // Only proceed if we are connected to the device
                    if(MainActivity.deviceConnected) {
                        String xyzSingleData[] = xyzdb.getLatestData();

                        if (xyzSingleData != null) {
                            String currentId = xyzSingleData[0];
                            if (!prevId.equals(currentId)) {
                                prevId = xyzSingleData[0];
                                // Add data to the graph
                                addEntry(mRollChart,Integer.valueOf(xyzSingleData[2]));
                                addEntry(mPitchChart,Integer.valueOf(xyzSingleData[3]));
                                addEntry(mYawChart,Integer.valueOf(xyzSingleData[4]));
                            }
                        } else {
                            System.out.println("Error retrieving data from database");
                        }
                    }



                    //addEntry(mRollChart,generateRandomData(-180, 180));
                    //addEntry(mPitchChart,generateRandomData(-180, 180));
                    //addEntry(mYawChart,generateRandomData(-180, 180));
                    //System.out.println("In thread: " + info);
                } catch (InterruptedException e) {
                    threadIsRunning=false;
                }
            }
        }

        public int generateRandomData(int n1, int n2){
            Random r = new Random();
            return r.nextInt(n2 - n1) + n1;
        }
    }

}


