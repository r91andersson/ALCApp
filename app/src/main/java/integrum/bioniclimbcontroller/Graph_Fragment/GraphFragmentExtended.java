package integrum.bioniclimbcontroller.Graph_Fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import integrum.bioniclimbcontroller.Database.EmgDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Home_Fragment.HomeFragment;
import integrum.bioniclimbcontroller.MainActivity;
import integrum.bioniclimbcontroller.R;
import integrum.bioniclimbcontroller.ThreadUtils;

/**
 * Created by Robin on 2016-10-27.
 */
public class GraphFragmentExtended extends Fragment{

    // Initiate a new thread utils object
    ThreadUtils utilsThread = new ThreadUtils();
    String threadName;
    EmgDbHelper emgdb;
    ViewPager viewPager;
    PagerAdapterGraphFragment adapter;
    RecyclerView rv;
    CardView cvch1;
    CardView cvch2;
    CardView cvch3;
    CardView cvch4;
    CardView cvch5;
    CardView cvch6;
    CardView cvch7;
    CardView cvch8;
    CheckBox ch1;
    CheckBox ch2;
    CheckBox ch3;
    CheckBox ch4;
    CheckBox ch5;
    CheckBox ch6;
    CheckBox ch7;
    CheckBox ch8;

    LineChart emgch1;
    LineChart emgch2;
    LineChart emgch3;
    LineChart emgch4;
    LineChart emgch5;
    LineChart emgch6;
    LineChart emgch7;
    LineChart emgch8;

    // Database for Settings
    SettingsDbHelper settingsdb;

    EMGThread EMGThread;

    private int emgCh1Count=0;
    private int emgCh2Count=0;
    private int emgCh3Count=0;
    private int emgCh4Count=0;
    private int emgCh5Count=0;
    private int emgCh6Count=0;
    private int emgCh7Count=0;
    private int emgCh8Count=0;

    ArrayList<String> xEmgCh1 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh1 = new ArrayList<Entry>();
    ArrayList<String> xEmgCh2 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh2 = new ArrayList<Entry>();
    ArrayList<String> xEmgCh3 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh3= new ArrayList<Entry>();
    ArrayList<String> xEmgCh4 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh4 = new ArrayList<Entry>();
    ArrayList<String> xEmgCh5 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh5 = new ArrayList<Entry>();
    ArrayList<String> xEmgCh6 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh6 = new ArrayList<Entry>();
    ArrayList<String> xEmgCh7 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh7 = new ArrayList<Entry>();
    ArrayList<String> xEmgCh8 = new ArrayList<String>();
    ArrayList<Entry> yEmgCh8 = new ArrayList<Entry>();

    private static int mExpandedPosition = -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String threadNameEMG = sharedPrefs.getString("EMGThreadName", null);
        ThreadUtils utilsThread = new ThreadUtils();

        if(threadNameEMG!=null) {
            Thread currentEMG = utilsThread.getThreadByName(threadNameEMG);
            if(currentEMG!=null) {
                // Thread have been started before -> check if it's still alive
                if (!currentEMG.isAlive()) {
                    // Thread isn't alive any longer, start a new fresh thread
                    EMGThread = new EMGThread();
                    // Only start thread if we are connected with the bluetooth
                    //  if(MainActivity.deviceConnected) {
                    EMGThread.setThreadIsRunning(true);
                    EMGThread.start();
                } else {
                    //Continue adding data
                }
            } else {
                // Thread hasn't been started before -> start a new thread
                EMGThread = new EMGThread();
                // Only start thread if we are connected with the bluetooth
                  if(MainActivity.deviceConnected) {
                      EMGThread.setThreadIsRunning(true);
                      EMGThread.start();
                  }
            }

        }
        else {
            // Thread hasn't been started before -> start a new thread
            EMGThread = new EMGThread();
            // Only start thread if we are connected with the bluetooth
            if (MainActivity.deviceConnected) {
                EMGThread.setThreadIsRunning(true);
                EMGThread.start();
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_graph_extended, container, false);

        // Get database for EMG
        emgdb = new EmgDbHelper(getActivity());

        // Get database for Settings
        settingsdb = new SettingsDbHelper(getActivity());


        rv = (RecyclerView) view.findViewById(R.id.rvHomeExtended);

        //rv.setVisibility(View.GONE);
        rv.setNestedScrollingEnabled(false);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        initializeData();

        MyAdapter adapter = new MyAdapter(items);
        rv.setAdapter(adapter);

        return view;
    }

    class Item {
        String name;
        String age;
        int photoId;

        Item(String name, String age, int photoId) {
            this.name = name;
            this.age = age;
            this.photoId = photoId;
        }
    }

    private List<Item> items;

    // This method creates an ArrayList that has three Person objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeData(){
        items = new ArrayList<>();
        items.add(new Item("Emma Wilson", "23 years old", R.drawable.icon59));
        items.add(new Item("Lavery Maiss", "25 years old", R.drawable.icon60));
        items.add(new Item("Lillie Watts", "35 years old", R.drawable.house));
    }


    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Item> items;

        public MyAdapter(List<Item> items) {
            this.items=items;
        }

        class ViewHolder0 extends RecyclerView.ViewHolder {
            LinearLayout details;
            ImageView arrow;

            public ViewHolder0(View inflate) {
                super(inflate);
                details = (LinearLayout) itemView.findViewById(R.id.settings_details);
                arrow = (ImageView) itemView.findViewById(R.id.arrowSettings);
            }
        }

        class ViewHolder2 extends RecyclerView.ViewHolder {

            public ViewHolder2(View itemView) {
                super(itemView);

            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            return position;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            String emgCh1On[]={"0","0","0"};
            String emgCh2On[]={"0","0","0"};
            String emgCh3On[]={"0","0","0"};
            String emgCh4On[]={"0","0","0"};
            String emgCh5On[]={"0","0","0"};
            String emgCh6On[]={"0","0","0"};
            String emgCh7On[]={"0","0","0"};
            String emgCh8On[]={"0","0","0"};
            try {
                emgCh1On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH1_GRAPH_ACTIVATED);
                emgCh2On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH2_GRAPH_ACTIVATED);
                emgCh3On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH3_GRAPH_ACTIVATED);
                emgCh4On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH4_GRAPH_ACTIVATED);
                emgCh5On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH5_GRAPH_ACTIVATED);
                emgCh6On= settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH6_GRAPH_ACTIVATED);
                emgCh7On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH7_GRAPH_ACTIVATED);
                emgCh8On = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH8_GRAPH_ACTIVATED);
            } catch (Exception e){
                Log.d("GraphFragmentExtended:","Database not created");
                System.out.println("Database not created");
            }

            switch (viewType) {
                case 0:
                    View vSetting = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_emg_settings_cardview, parent, false);
                    ch1 = (CheckBox) vSetting.findViewById(R.id.checkBox1);
                    if(emgCh1On!=null)
                        if(Boolean.valueOf(emgCh1On[3])) ch1.setChecked(true);

                    ch2 = (CheckBox) vSetting.findViewById(R.id.checkBox2);
                    if(emgCh2On!=null)
                        if(Boolean.valueOf(emgCh2On[3])) ch2.setChecked(true);

                    ch3 = (CheckBox) vSetting.findViewById(R.id.checkBox3);
                    if(emgCh3On!=null)
                        if(Boolean.valueOf(emgCh3On[3])) ch3.setChecked(true);

                    ch4 = (CheckBox) vSetting.findViewById(R.id.checkBox4);
                    if(emgCh4On!=null)
                        if(Boolean.valueOf(emgCh4On[3])) ch4.setChecked(true);

                    ch5 = (CheckBox) vSetting.findViewById(R.id.checkBox5);
                    if(emgCh5On!=null)
                        if(Boolean.valueOf(emgCh5On[3])) ch5.setChecked(true);

                    ch6 = (CheckBox) vSetting.findViewById(R.id.checkBox6);
                    if(emgCh6On!=null)
                        if(Boolean.valueOf(emgCh6On[3])) ch6.setChecked(true);

                    ch7 = (CheckBox) vSetting.findViewById(R.id.checkBox7);
                    if(emgCh7On!=null)
                        if(Boolean.valueOf(emgCh7On[3])) ch7.setChecked(true);

                    ch8 = (CheckBox) vSetting.findViewById(R.id.checkBox8);
                    if(emgCh8On!=null)
                        if(Boolean.valueOf(emgCh8On[3])) ch8.setChecked(true);

                    return new ViewHolder0(vSetting);
                default:
                    View vDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_graph_extended_cardview, parent, false);
                    cvch1 = (CardView) vDefault.findViewById(R.id.cvch1);

                    if(emgCh1On!=null)
                        if(Boolean.valueOf(emgCh1On[3])) cvch1.setVisibility(View.VISIBLE);
                        else cvch1.setVisibility(View.GONE);

                    cvch2 = (CardView) vDefault.findViewById(R.id.cvch2);
                    if(emgCh2On!=null)
                        if(Boolean.valueOf(emgCh2On[3])) cvch2.setVisibility(View.VISIBLE);
                        else cvch2.setVisibility(View.GONE);

                    cvch3 = (CardView) vDefault.findViewById(R.id.cvch3);
                    if(emgCh3On!=null)
                        if(Boolean.valueOf(emgCh3On[3])) cvch3.setVisibility(View.VISIBLE);
                        else cvch3.setVisibility(View.GONE);

                    cvch4 = (CardView) vDefault.findViewById(R.id.cvch4);
                    if(emgCh4On!=null)
                        if(Boolean.valueOf(emgCh4On[3])) cvch4.setVisibility(View.VISIBLE);
                        else cvch4.setVisibility(View.GONE);

                    cvch5 = (CardView) vDefault.findViewById(R.id.cvch5);
                    if(emgCh5On!=null)
                        if(Boolean.valueOf(emgCh5On[3])) cvch5.setVisibility(View.VISIBLE);
                        else cvch5.setVisibility(View.GONE);

                    cvch6 = (CardView) vDefault.findViewById(R.id.cvch6);
                    if(emgCh6On!=null)
                        if(Boolean.valueOf(emgCh6On[3])) cvch6.setVisibility(View.VISIBLE);
                        else cvch6.setVisibility(View.GONE);

                    cvch7 = (CardView) vDefault.findViewById(R.id.cvch7);
                    if(emgCh7On!=null)
                        if(Boolean.valueOf(emgCh7On[3])) cvch7.setVisibility(View.VISIBLE);
                        else cvch7.setVisibility(View.GONE);

                    cvch8 = (CardView) vDefault.findViewById(R.id.cvch8);
                    if(emgCh8On!=null)
                        if(Boolean.valueOf(emgCh8On[3])) cvch8.setVisibility(View.VISIBLE);
                        else cvch8.setVisibility(View.GONE);

                    setupEMGCh1Chart(vDefault);
                    setupEMGCh2Chart(vDefault);
                    setupEMGCh3Chart(vDefault);
                    setupEMGCh4Chart(vDefault);
                    setupEMGCh5Chart(vDefault);
                    setupEMGCh6Chart(vDefault);
                    setupEMGCh7Chart(vDefault);
                    setupEMGCh8Chart(vDefault);

                    return new ViewHolder2(vDefault);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            int i=holder.getItemViewType();
            if(i==0){
                final boolean isExpanded = i == mExpandedPosition;
                if(isExpanded){
                    ((ViewHolder0) holder).arrow.setImageResource(R.drawable.button_up_20_20);
                } else {
                    ((ViewHolder0) holder).arrow.setImageResource(R.drawable.button_down_20_20);
                }
                ((ViewHolder0) holder).details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ((ViewHolder0) holder).itemView.setActivated(isExpanded);
                ((ViewHolder0) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mExpandedPosition = isExpanded ? -1 : position;
                        notifyDataSetChanged();
                    }
                });

                ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH1_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch1.setVisibility(View.VISIBLE);
                        } else {
                            cvch1.setVisibility(View.GONE);
                            cvch1.getVisibility();
                        }
                    }
                });

                ch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH2_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch2.setVisibility(View.VISIBLE);
                        } else {
                            cvch2.setVisibility(View.GONE);
                        }
                    }
                });

                ch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH3_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch3.setVisibility(View.VISIBLE);
                        } else {
                            cvch3.setVisibility(View.GONE);
                        }
                    }
                });

                ch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH4_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch4.setVisibility(View.VISIBLE);
                        } else {
                            cvch4.setVisibility(View.GONE);
                        }
                    }
                });

                ch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH5_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch5.setVisibility(View.VISIBLE);
                        } else {
                            cvch5.setVisibility(View.GONE);
                        }
                    }
                });

                ch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH6_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch6.setVisibility(View.VISIBLE);
                        } else {
                            cvch6.setVisibility(View.GONE);
                        }
                    }
                });

                ch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH7_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch7.setVisibility(View.VISIBLE);
                        } else {
                            cvch7.setVisibility(View.GONE);
                        }
                    }
                });

                ch8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_CH8_GRAPH_ACTIVATED,String.valueOf(b));
                        if (b) {
                            cvch8.setVisibility(View.VISIBLE);
                        } else {
                            cvch8.setVisibility(View.GONE);
                        }
                    }
                });
            } else {

            }

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

    }

    public void setupEMGCh1Chart(View view){
        emgch1 = (LineChart) view.findViewById(R.id.emgChart1);
        emgch1.setId(R.id.emgChart1);
        emgch1.setTouchEnabled(true);
        emgch1.setDragEnabled(true);
        emgch1.setScaleEnabled(true);
        emgch1.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch1.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch1.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch1.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch1.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch1.getAxisLeft();

        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info=null;// settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1);

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
        leftAxis.setAxisMaxValue(0.00001f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = emgch1.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch1.setData(data2);
        emgch1.setDescription("");
    }

    public void setupEMGCh2Chart(View view){
        emgch2 = (LineChart) view.findViewById(R.id.emgChart2);
        emgch2.setId(R.id.emgChart2);
        emgch2.setTouchEnabled(true);
        emgch2.setDragEnabled(true);
        emgch2.setScaleEnabled(true);
        emgch2.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch2.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch2.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch2.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch2.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch2.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info= null;//settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_2);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh2 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh2, "Threshold Channel 2");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            leftAxis.addLimitLine(ll1);
        }
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(0.00001f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = emgch2.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch2.setData(data2);
        emgch2.setDescription("");
    }

    public void setupEMGCh3Chart(View view){
        emgch3 = (LineChart) view.findViewById(R.id.emgChart3);
        emgch3.setId(R.id.emgChart3);
        emgch3.setTouchEnabled(true);
        emgch3.setDragEnabled(true);
        emgch3.setScaleEnabled(true);
        emgch3.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch3.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch3.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch3.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch3.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch3.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info=null;// settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_3);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh2 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh2, "Threshold Channel 3");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            leftAxis.addLimitLine(ll1);
        }
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(0.00001f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = emgch3.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch3.setData(data2);
        emgch3.setDescription("");
    }

    public void setupEMGCh4Chart(View view){
        emgch4 = (LineChart) view.findViewById(R.id.emgChart4);
        emgch4.setId(R.id.emgChart4);
        emgch4.setTouchEnabled(true);
        emgch4.setDragEnabled(true);
        emgch4.setScaleEnabled(true);
        emgch4.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch4.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch4.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch4.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch4.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch4.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info=null;// settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_4);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh2 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh2, "Threshold Channel 4");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            leftAxis.addLimitLine(ll1);
        }
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(0.00001f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = emgch4.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch4.setData(data2);
        emgch4.setDescription("");
    }

    public void setupEMGCh5Chart(View view){
        emgch5 = (LineChart) view.findViewById(R.id.emgChart5);
        emgch5.setId(R.id.emgChart5);
        emgch5.setTouchEnabled(true);
        emgch5.setDragEnabled(true);
        emgch5.setScaleEnabled(true);
        emgch5.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch5.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch5.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch5.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch5.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch5.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info= null;//settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_5);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh2 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh2, "Threshold Channel 5");
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

        YAxis rightAxis = emgch5.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch5.setData(data2);
        emgch5.setDescription("");
    }

    public void setupEMGCh6Chart(View view){
        emgch6 = (LineChart) view.findViewById(R.id.emgChart6);
        emgch6.setId(R.id.emgChart6);
        emgch6.setTouchEnabled(true);
        emgch6.setDragEnabled(true);
        emgch6.setScaleEnabled(true);
        emgch6.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch6.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch6.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch6.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch6.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch6.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info=null;// settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_6);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh6 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh6, "Threshold Channel 2");
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

        YAxis rightAxis = emgch6.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch6.setData(data2);
        emgch6.setDescription("");
    }

    public void setupEMGCh7Chart(View view){
        emgch7 = (LineChart) view.findViewById(R.id.emgChart7);
        emgch7.setId(R.id.emgChart7);
        emgch7.setTouchEnabled(true);
        emgch7.setDragEnabled(true);
        emgch7.setScaleEnabled(true);
        emgch7.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch7.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch7.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch7.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch7.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch7.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info=null;// settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_7);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh7 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh7, "Threshold Channel 7");
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

        YAxis rightAxis = emgch7.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch7.setData(data2);
        emgch7.setDescription("");
    }

    public void setupEMGCh8Chart(View view){
        emgch8 = (LineChart) view.findViewById(R.id.emgChart8);
        emgch8.setId(R.id.emgChart8);
        emgch8.setTouchEnabled(true);
        emgch8.setDragEnabled(true);
        emgch8.setScaleEnabled(true);
        emgch8.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        emgch8.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        emgch8.setData(data);

        // get the legend (only possible after setting data)
        Legend l = emgch8.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = emgch8.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = emgch8.getAxisLeft();
        // Add threshold
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info= null;//settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_8);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh2 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh2, "Threshold Channel 8");
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

        YAxis rightAxis = emgch8.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        emgch8.setData(data2);
        emgch8.setDescription("");
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
                    String emgSync[] =settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH);
                    Boolean emgOn=false;
                    if(emgSync!=null){
                        emgOn=Boolean.valueOf(emgSync[3]);
                    }
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
                    if(MainActivity.deviceConnected) {
                        if (emgOn) {
                            String emgSingleData[] = emgdb.getLatestData();
                            if (emgSingleData != null) {
                                String currentId = emgSingleData[0];
                                if (!prevId.equals(currentId)) {
                                    prevId = emgSingleData[0];
                                    // Add data to the graph
                                    if (emgch1 != null && emgch2 != null && emgch3 != null && emgch4 != null && emgch5 != null && emgch6 != null && emgch7 != null && emgch8 != null) {
                                        if (cvch1.getVisibility() == View.VISIBLE) {
                                            float tmabsCh1=Float.valueOf(emgSingleData[2]);
                                            addEntry(emgch1, tmabsCh1);
                                        }
                                        if (cvch2.getVisibility() == View.VISIBLE) {
                                            float tmabsCh2=Float.valueOf(emgSingleData[3]);
                                            addEntry(emgch2, tmabsCh2);
                                        }
                                        if (cvch3.getVisibility() == View.VISIBLE) {
                                            float tmabsCh3=Float.valueOf(emgSingleData[4]);
                                            addEntry(emgch3, tmabsCh3);
                                        }
                                        if (cvch4.getVisibility() == View.VISIBLE) {
                                            float tmabsCh4=Float.valueOf(emgSingleData[5]);
                                            addEntry(emgch4, tmabsCh4);
                                        }

                                       /*
                                        if (cvch5.getVisibility() == View.VISIBLE) {
                                            float tmabs=Float.valueOf(emgSingleData[2]);
                                            addEntry(emgch5, Math.round(Double.valueOf(emgSingleData[6])));
                                        }
                                        if (cvch6.getVisibility() == View.VISIBLE) {
                                            float tmabs=Float.valueOf(emgSingleData[2]);
                                            addEntry(emgch6, Math.round(Double.valueOf(emgSingleData[7])));
                                        }
                                        if (cvch7.getVisibility() == View.VISIBLE) {
                                            float tmabs=Float.valueOf(emgSingleData[2]);
                                            addEntry(emgch7, Math.round(Double.valueOf(emgSingleData[8])));
                                        }
                                        if (cvch8.getVisibility() == View.VISIBLE) {
                                            float tmabs=Float.valueOf(emgSingleData[2]);
                                            addEntry(emgch8, Math.round(Double.valueOf(emgSingleData[9])));
                                        }
                                        */
                                    }
                                }
                                }
                            }
                        }
                        // -----------------------------------//
                        //   }
                        //  } else {
                        //      System.out.println("Error retrieving data from database");
                        //  }
                        //  }

                        //addEntry(generateRandomData(40, 80));
                     //   System.out.println("In thread: " + info);
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

    protected void addEntry(LineChart mTmpChart, float value) {

        Calendar c = Calendar.getInstance();
        LineData data = mTmpChart.getData();
        // ILineDataSet set = data.getDataSetByIndex(0);

        int seconds = c.get(Calendar.SECOND);
        mTmpChart.getId();

        int id = mTmpChart.getId();

        switch (id){
            case R.id.emgChart1:
                xEmgCh1.add(String.valueOf(seconds));
                yEmgCh1.add(new Entry(value, emgCh1Count));
                emgCh1Count++;
                LineDataSet emgCh1LineData;

                // create a dataset and give it a type
                emgCh1LineData = new LineDataSet(yEmgCh1, "Ch.1");
                emgCh1LineData.setDrawValues(false);
                // setRoll.setFillColor(Color.BLACK);
                emgCh1LineData.setLineWidth(2f);
                emgCh1LineData.setCircleRadius(0f);
                emgCh1LineData.setDrawFilled(false);

                //setRoll.setFillColor(Color.BLACK);
                int emgCh1Color = Color.rgb(0, 0, 255);
                emgCh1LineData.setColor(emgCh1Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh1, emgCh1LineData);

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
            case R.id.emgChart2:
                xEmgCh2.add(String.valueOf(seconds));
                yEmgCh2.add(new Entry(value, emgCh2Count));
                emgCh2Count++;
                LineDataSet emgCh2LineData;

                // create a dataset and give it a type
                emgCh2LineData = new LineDataSet(yEmgCh2, "Ch.2");
                emgCh2LineData.setDrawValues(false);
                //setPitch.setFillColor(Color.BLACK);
                emgCh2LineData.setLineWidth(2f);
                emgCh2LineData.setCircleRadius(0f);
                emgCh2LineData.setDrawFilled(false);

                // setPitch.setFillColor(Color.BLACK);
                int emgCh2Color = Color.rgb(0, 255, 0);
                emgCh2LineData.setColor(emgCh2Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh2, emgCh2LineData);

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
            case R.id.emgChart3:
                xEmgCh3.add(String.valueOf(seconds));
                yEmgCh3.add(new Entry(value, emgCh3Count));
                emgCh3Count++;
                LineDataSet emgCh3LineData;

                // create a dataset and give it a type
                emgCh3LineData = new LineDataSet(yEmgCh3, "Ch.3");
                emgCh3LineData.setDrawValues(false);
                emgCh3LineData.setFillColor(Color.BLACK);
                emgCh3LineData.setLineWidth(2f);
                emgCh3LineData.setCircleRadius(0f);
                emgCh3LineData.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int emgCh3Color = Color.rgb(255, 0, 0);
                emgCh3LineData.setColor(emgCh3Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh3, emgCh3LineData);

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
            case R.id.emgChart4:
                xEmgCh4.add(String.valueOf(seconds));
                yEmgCh4.add(new Entry(value, emgCh4Count));
                emgCh4Count++;
                LineDataSet emgCh4LineData;

                // create a dataset and give it a type
                emgCh4LineData = new LineDataSet(yEmgCh4, "Ch.4");
                emgCh4LineData.setDrawValues(false);
                emgCh4LineData.setFillColor(Color.BLACK);
                emgCh4LineData.setLineWidth(2f);
                emgCh4LineData.setCircleRadius(0f);
                emgCh4LineData.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int emgCh4Color = Color.rgb(255, 0, 0);
                emgCh4LineData.setColor(emgCh4Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh4, emgCh4LineData);

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
            case R.id.emgChart5:
                xEmgCh5.add(String.valueOf(seconds));
                yEmgCh5.add(new Entry(value, emgCh5Count));
                emgCh5Count++;
                LineDataSet emgCh5LineData;

                // create a dataset and give it a type
                emgCh5LineData = new LineDataSet(yEmgCh5, "Ch.5");
                emgCh5LineData.setDrawValues(false);
                emgCh5LineData.setFillColor(Color.BLACK);
                emgCh5LineData.setLineWidth(2f);
                emgCh5LineData.setCircleRadius(0f);
                emgCh5LineData.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int emgCh5Color = Color.rgb(255, 0, 0);
                emgCh5LineData.setColor(emgCh5Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh5, emgCh5LineData);

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
            case R.id.emgChart6:
                xEmgCh6.add(String.valueOf(seconds));
                yEmgCh6.add(new Entry(value, emgCh6Count));
                emgCh6Count++;
                LineDataSet emgCh6LineData;

                // create a dataset and give it a type
                emgCh6LineData = new LineDataSet(yEmgCh6, "Ch.6");
                emgCh6LineData.setDrawValues(false);
                emgCh6LineData.setFillColor(Color.BLACK);
                emgCh6LineData.setLineWidth(2f);
                emgCh6LineData.setCircleRadius(0f);
                emgCh6LineData.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int emgCh6Color = Color.rgb(255, 0, 0);
                emgCh6LineData.setColor(emgCh6Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh6, emgCh6LineData);

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
            case R.id.emgChart7:
                xEmgCh7.add(String.valueOf(seconds));
                yEmgCh7.add(new Entry(value, emgCh7Count));
                emgCh7Count++;
                LineDataSet emgCh7LineData;

                // create a dataset and give it a type
                emgCh7LineData = new LineDataSet(yEmgCh7, "Ch.7");
                emgCh7LineData.setDrawValues(false);
                emgCh7LineData.setFillColor(Color.BLACK);
                emgCh7LineData.setLineWidth(2f);
                emgCh7LineData.setCircleRadius(0f);
                emgCh7LineData.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int emgCh7Color = Color.rgb(255, 0, 0);
                emgCh7LineData.setColor(emgCh7Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh7, emgCh7LineData);

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
            case R.id.emgChart8:
                xEmgCh8.add(String.valueOf(seconds));
                yEmgCh8.add(new Entry(value, emgCh8Count));
                emgCh8Count++;
                LineDataSet emgCh8LineData;

                // create a dataset and give it a type
                emgCh8LineData = new LineDataSet(yEmgCh8, "Ch.8");
                emgCh8LineData.setDrawValues(false);
                emgCh8LineData.setFillColor(Color.BLACK);
                emgCh8LineData.setLineWidth(2f);
                emgCh8LineData.setCircleRadius(0f);
                emgCh8LineData.setDrawFilled(false);

                //setYaw.setFillColor(Color.BLACK);
                int emgCh8Color = Color.rgb(255, 0, 0);
                emgCh8LineData.setColor(emgCh8Color, 100);
                // create a data object with the dataset
                data = new LineData(xEmgCh8, emgCh8LineData);

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


}
