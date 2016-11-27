package integrum.bioniclimbcontroller.Graph_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import integrum.bioniclimbcontroller.Bluetooth.BluetoothMessageService;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.EmgDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Database.XyzDbHelper;
import integrum.bioniclimbcontroller.MainActivity;
import integrum.bioniclimbcontroller.R;
import integrum.bioniclimbcontroller.ThreadUtils;

/**
 * Created by Robin on 2016-09-29.
 */
public class EMGFragment extends Fragment {
    private LineChart mChart;
    Drawable drawable;
    public int count=0;
    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<Entry> yVals = new ArrayList<Entry>();
    Thread thread;
    EMGThread EMGThread = new EMGThread();
    String threadName;
    ViewGroup emgContainer;
    // Initiate a new thread utils object
    ThreadUtils utilsThread = new ThreadUtils();
    EMGFragmentListener mCallback;

    // Database for EMG data
    EmgDbHelper emgdb;

    // Database for Settings
    SettingsDbHelper settingsdb;

    //Database for XYZ
    XyzDbHelper xyzdb;


    // Container Activity must implement this interface
    public interface EMGFragmentListener {
        public int getRollValue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int recoilFaded = 0x00535353;
        final View view = inflater.inflate(R.layout.fragment_emg, container, false);
        EMGThread EMGThread = new EMGThread();
        // Get database for EMG
        emgdb = new EmgDbHelper(getActivity());

        // Get database for Settings
        settingsdb = new SettingsDbHelper(getActivity());

        // Get database for XYZ
        xyzdb= new XyzDbHelper(getActivity());

        Button fileSize = (Button) view.findViewById(R.id.fileSize);
        final TextView sizeFile = (TextView) view.findViewById(R.id.size);
        final TextView entries = (TextView) view.findViewById(R.id.entries);

        fileSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long settingsMb=settingsdb.getDataBaseSize()/1000000;
                long emgMb=emgdb.getDataBaseSize()/1000000;
                long xyzMb=xyzdb.getDataBaseSize()/1000000;
                long total = settingsMb + emgMb+ xyzMb;
                sizeFile.setText(String.valueOf(total));

                long rowsEmg = emgdb.numberOfRows();
                long rowsXyz = xyzdb.numberOfRows();
                long rowsSettings= settingsdb.numberOfRows();
                long totalRows= rowsEmg+rowsXyz+rowsSettings;
                entries.setText(String.valueOf(totalRows));

            }
        });

        emgContainer=container;
        mChart = (LineChart) view.findViewById(R.id.emgChart);


        //mChart.setOnChartValueSelectedListener(this);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(recoilFaded);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        //Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);


        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info= settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1);


        YAxis leftAxis = mChart.getAxisLeft();

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh1 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh1, "Threshold Channel 1");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            leftAxis.addLimitLine(ll1);
        }
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(120f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data2);

        // Only start thread if we are connected with the bluetooth
      //  if(MainActivity.deviceConnected) {
            EMGThread.setThreadIsRunning(true);
            EMGThread.start();
      //  }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (EMGFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HomeFragmentListener");
        }
    }


    /*
    sendThreadID threadNr;
    interface sendThreadID
    {
        public void thID(String id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            threadNr = (sendThreadID) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


*/
    public void stopEMGThread(){
        EMGThread.stopThread();
    }


    protected void addEntry(float temperatureVal) {

        Calendar c = Calendar.getInstance();
        LineData data = mChart.getData();
        ILineDataSet set = data.getDataSetByIndex(0);
        final int recoilFaded   = 0x50535353;


        int seconds = c.get(Calendar.SECOND);

        xVals.add(String.valueOf(seconds));
        yVals.add(new Entry(temperatureVal, count));
        count++;
        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "EMG - Channel 1");
        set1.setDrawValues(false);
        set1.setFillColor(Color.BLACK);
        set1.setColor(recoilFaded);
        set1.setLineWidth(2f);
        set1.setCircleRadius(0f);
        set1.setDrawFilled(true);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            if(drawable==null) {
                try {
                    drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_color_graph);
                } catch (Exception e){
                    Log.d("Graph","Error in receiving drawable for graph");
                }
            }
            set1.setFillDrawable(drawable);
        }
        else {
            set1.setFillColor(Color.BLACK);
        }

        // create a data object with the dataset
        data = new LineData(xVals, set1);

        // set data
        mChart.setData(data);
        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        // limit the number of visible entries
        mChart.setVisibleXRangeMaximum(120);
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        mChart.moveViewToX(data.getXValCount() - 121);
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    protected int generateRandomData(int n1, int n2){
        Random r = new Random();
        return r.nextInt(n2 - n1) + n1;
    }

    public class EMGThread extends Thread {

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
            String prevTimeStamp="null";
            String prevId="null";
            //Set tag on the container equal to the thread ID to be able
            //to stop the thread in the parent fragment
            // emgContainer.setTag(threadName);

            // Store the thread name inside shared preferences_basic
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("EMGThreadName", threadName);
            editor.apply();

            while (threadIsRunning) {
                try {
                    sleep(50);
                    String info= utilsThread.getThreadSignature();

                    // Only proceed if we are connected to the device
                   // if(MainActivity.deviceConnected) {
                   //     String emgSingleData[] = emgdb.getLatestData();

                   //     if (emgSingleData != null) {
                   //         String currentId = emgSingleData[0];
                   //         if (!prevId.equals(currentId)) {
                   //             prevId = emgSingleData[0];
                                // Add data to the graph
                                // addEntry(Integer.valueOf(emgSingleData[3]));

                                // ------- Previous approach -------- //
                                // Add data to the graph
                                addEntry(generateRandomData(10,80));
                                // -----------------------------------//
                         //   }
                      //  } else {
                      //      System.out.println("Error retrieving data from database");
                      //  }
                  //  }

                    //addEntry(generateRandomData(40, 80));
                 System.out.println("In thread: " + info);
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


