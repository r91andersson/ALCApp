package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

import integrum.bioniclimbcontroller.Bluetooth.CommunicationProtocol;
import integrum.bioniclimbcontroller.Bluetooth.MessageStruct;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.DcDbHelper;
import integrum.bioniclimbcontroller.Database.EmgDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Graph_Fragment.GraphFragmentExtended;
import integrum.bioniclimbcontroller.MainActivity;
import integrum.bioniclimbcontroller.R;
import integrum.bioniclimbcontroller.ThreadUtils;
import okhttp3.internal.Util;

/**
 * Created by Robin on 2016-11-04.
 */
public class ParameterFragmentV2 extends Fragment {
    // Initiate a new thread utils object
    ThreadUtils utilsThread = new ThreadUtils();
    String threadName;
    RecyclerView rv;
    private static final int ADD_MOVEMENT = 1;
    private int totalAddMovementCards =1;
    private int totalMovementCards;
    private int totalCards = totalAddMovementCards + totalMovementCards;
    ParameterAdapter adapterP;
    public static String movementCode = "Movement";
    public static String gainCode = "Gain";
    public static String thresholdCode = "Threshold";
    public static String channelCode = "Channel";
    public static String enableCode = "Enable";
    public static String movementNrCode="number";
    public int channel;
    private static int mExpandedPosition = -1;
    private static int mExpandedPosition2 = -1;
    private Handler mHandler = new Handler();
    private  List<Movement> movementList = new ArrayList<>();
    LinearLayoutManager llm;
    SettingsDbHelper settingsdb;
    DcDbHelper dcDbHelper;
    EmgDbHelper emgdb;

    DirectControlThread dcThread;

    int pos=0;
    ParameterV2MethodListener mCallback;
            /*
            Defined movements on embedded system
            #define REST 			  		0
            #define OPENHAND 			  	1
            #define CLOSEHAND			  	2
            #define SWITCH1       	     	3
            #define PRONATION            	4
            #define SUPINATION		        5
            #define COCONTRACTION           6
            */

    public static String [] movementNames = new String[] {"Open Hand",
            "Close Hand",
            "Switch",
            "Pronation",
            "Supination",
            "Cocontraction",
            "Flex Hand",
            "Extend hand",
            "Side Grip",
            "Fine Grip",
            "Agree",
            "Pointer",
            "Thumb Extend",
            "Thumb Flex",
            "Tumb Abduc 1",
            "Thumb Abduc 2",
            "Flex Elbow",
            "Extend Elbow",
            "Index Flex",
            "Index Extend",
            "Middle Flex",
            "Middle Extend",
            "Ring Flex",
            "Ring Extend",
            "Little Flex",
            "Little Extend"};

    public interface ParameterV2MethodListener{
        public void sendParameterMessage(MessageStruct msg);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ParameterFragmentV2.ParameterV2MethodListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ParameterV2MethodListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_direct_control_v2, container, false);
        settingsdb = new SettingsDbHelper(getActivity());
        emgdb = new EmgDbHelper(getActivity());
        // settingsdb.deleteDataBase(getActivity());
        dcDbHelper = new DcDbHelper(getActivity());
        //dcDbHelper.deleteDataBase(getActivity());
        totalMovementCards = 0;
        rv = (RecyclerView) view.findViewById(R.id.rvDirectControl);
        //rv.setVisibility(View.GONE);
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(null);

        //Disable the effects when recreating the cards in the adapter
        // ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);

        movementList = new ArrayList<>();
        adapterP = new ParameterAdapter();
        rv.setAdapter(adapterP);
        loadMovementsFromDataBaseV2();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String threadNameDirectControl = sharedPrefs.getString("DirectControlThread", null);
        ThreadUtils utilsThread = new ThreadUtils();

        // Check if direct control thread is already running or not.
        if(threadNameDirectControl!=null) {
            Thread currentDC = utilsThread.getThreadByName(threadNameDirectControl);
            if (currentDC != null) {
                // Thread have been started before -> check if it's still alive
                if (!currentDC.isAlive()) {
                    startNewDcThread();
                }
            } else {
                startNewDcThread();
            }
        } else {
            // Thread hasn't been started before -> start a new thread
            startNewDcThread();
        }

        return view;
    }

    public void startNewDcThread(){
        dcThread = new ParameterFragmentV2.DirectControlThread();
        // Only start thread if we are connected with the bluetooth
        //if(MainActivity.deviceConnected) {
        dcThread.setThreadIsRunning(true);
        dcThread.start();
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  addData2GraphThread addData = new addData2GraphThread();
        //  addData.setThreadRunState(true);
        //  addData.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK) {
            switch (requestCode) {
                case ADD_MOVEMENT:
                    String movement = data.getStringExtra(movementCode);
                    String threshold = data.getStringExtra(thresholdCode);

                    channel=data.getIntExtra(channelCode,channel);
                    String gain = data.getStringExtra(gainCode);
                    String enable = data.getStringExtra(enableCode);
                    pos = data.getIntExtra(movementNrCode,pos);

                    //Update the movement threshold corresponding to the channel and the movement name
                    if(!settingsdb.updateSetting(SettingsDbHelper.THRESHOLD_STRING[channel],threshold,movementNames[pos])){
                        settingsdb.insertSetting(SettingsDbHelper.THRESHOLD_STRING[channel],threshold,movementNames[pos]);
                    }

                    // Initialize mvc to zero each time a new movement is added
                    if(!settingsdb.updateSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[channel],"0.0"))
                        settingsdb.insertSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[channel],"0.0");

                    int chInc=channel+1;
                    String id = settingsdb.getRowId(SettingsDbHelper.THRESHOLD_STRING[pos]);

                    //Create a movement object add finally add it to the list
                    Movement m = new Movement(movement,String.valueOf(chInc),threshold,gain,enable,id,movementNames[pos]);
                    movementList.add(m);
                    adapterP.notifyDataSetChanged();
                    break;
            }
        }

    }


    public void loadMovementsFromDataBaseV2() {
        int nChannels = SettingsDbHelper.THRESHOLD_STRING.length;
        for (int i = 0; i < nChannels; i++) {
            String value[] = settingsdb.getLatestSetting(SettingsDbHelper.THRESHOLD_STRING[i]);
            String mvcStr[] = settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[i]);
            if (value != null && mvcStr !=null) {
                try {
                    if (value[3].matches(".*\\d+.*")) {
                        String movement = value[4];
                        String channel = value[2];
                        int index = -1;

                        for (int j = 0; i < SettingsDbHelper.THRESHOLD_STRING.length; j++) {
                            if (SettingsDbHelper.THRESHOLD_STRING[j].equals(channel)) {
                                index = j;
                                break;
                            }
                        }
                        //String was found in database array, insert the movement into local list.
                        if (index > -1) {
                            index = index + 1;
                            float th=Float.valueOf(value[3]);
                            float mvc = Float.valueOf(mvcStr[3]);
                            float mappedThresold=th*100/(mvc);
                            int iThreshold = (int)mappedThresold;
                            String threshold = String.valueOf(iThreshold);
                            Movement m = new Movement(movement, String.valueOf(index), threshold, "", "true", "id", movement);
                            movementList.add(m);
                        }
                    }
                } catch (Exception e){

                }
            }
        }
    }


    public void loadMovementsFromDatabase(){
        // Format: ID | Timestamp | Movement name | Channel | Threshold | Value | Enabled
        String[][]jStr=dcDbHelper.getAllPatRecMov();
        int nRows = jStr.length;
        for (int i=0;i<nRows;i++) {
            if(Boolean.parseBoolean(jStr[i][6])){
                String movement= jStr[i][2];
                String channel = jStr[i][3];
                String threshold = jStr[i][4];
                String value = jStr[i][5];
                String enabled=jStr[i][6];
                Movement m = new Movement(movement,channel,threshold,value,"true",enabled,movement);
                movementList.add(m);
            }
        }
        adapterP.notifyDataSetChanged();
    }


    public class Movement {
        private String movement;
        private String channel;
        private String threshold;
        private String gain;
        private String enable;
        private String id;
        private String settingsType;
        private ArrayList<String> xData = new ArrayList<String>();
        private ArrayList<Entry> yData = new ArrayList<Entry>();
        private int entries;
        private LineChart lineCh;
        private ProgressBar progressBar;
        private boolean cardCreated;

        public boolean getcardCreated() {
            return cardCreated;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        public void setCardCreated(boolean cardCreated) {
            this.cardCreated = cardCreated;
        }

        public Movement(String movement, String channel, String threshold, String gain, String enable,String idn, String setting ){
            this.movement=movement;
            this.channel=channel;
            this.threshold=threshold;
            this.gain=gain;
            this.enable=enable;
            this.id=idn;
            this.settingsType=setting;
            this.entries=0;
            this.cardCreated=false;
        }
        public String getId(){
            return id;
        }

        public LineChart getLineCh() {
            return lineCh;
        }

        public void setLineCh(LineChart lineCh) {
            this.lineCh = lineCh;
        }

        public String getSettingsType(){
            return settingsType;
        }
        public String getMovement() {
            return movement;
        }

        public void setMovement(String movement) {
            this.movement = movement;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getThreshold() {
            return threshold;
        }

        public void setThreshold(String threshold) {
            this.threshold = threshold;
        }

        public String getGain() {
            return gain;
        }

        public void setGain(String gain) {
            this.gain = gain;
        }

        public String getEnable() {
            return enable;
        }

        public void setEnable(String enable) {
            this.enable = enable;
        }

        public int getEntries() {
            return entries;
        }

        public void setEntries(int entries) {
            this.entries = entries;
        }

        public ArrayList<String> getxData(){
            return xData;

        }

        public ArrayList<Entry> getyData() {
            return yData;
        }

        public void setyData(ArrayList<Entry> yData) {
            this.yData = yData;
        }

        public void setxData(ArrayList<String> xData) {
            this.xData = xData;
        }
    }

    public void setupLineChart(Movement m,LineChart linechart,String id){
        linechart.setId(getId());
        linechart.setTouchEnabled(true);
        linechart.setDragEnabled(true);
        linechart.setScaleEnabled(true);
        linechart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        linechart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        linechart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = linechart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = linechart.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = linechart.getAxisLeft();

/*
        // Add threshold
        int chnl=Integer.valueOf(m.getChannel())-1;
        String[] threshold_info= settingsdb.getLatestSetting(SettingsDbHelper.THRESHOLD_STRING[chnl]);

        //Add threshold if there is any parameter in the database
        if(threshold_info!=null) {
            float thresholdCh2 = Float.valueOf(threshold_info[3]);
            LimitLine ll1 = new LimitLine(thresholdCh2, "Threshold");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            leftAxis.addLimitLine(ll1);
        }
*/
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(0.00001f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = linechart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        linechart.setData(data2);
        linechart.setDescription("");

        //save linechart inside object
        m.setLineCh(linechart);
    }

    public void setupLineChart(Movement m,View view,String id){
        LineChart linechart = (LineChart) view.findViewById(R.id.linechartCard);
        linechart.setId(Integer.valueOf(id));
        linechart.setTouchEnabled(true);
        linechart.setDragEnabled(true);
        linechart.setScaleEnabled(true);
        linechart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        linechart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        // add empty data
        linechart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = linechart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //  l.setTypeface(tf);
        l.setTextColor(Color.BLACK);

        XAxis xl = linechart.getXAxis();
        //   xl.setTypeface(tf);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = linechart.getAxisLeft();


        // Add threshold
        /*
        settingsdb = new SettingsDbHelper(getActivity());
        String[] threshold_info= settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_4);

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
        */
        // leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(120f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = linechart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.BLACK);

        // add empty data
        linechart.setData(data2);
        linechart.setDescription("");

        //save linechart inside object
        m.setLineCh(linechart);
    }


    public void addEntry (Movement move, LineChart mTmpChart, float value) {
        Calendar c = Calendar.getInstance();
        LineData data = move.getLineCh().getLineData();
        // ILineDataSet set = data.getDataSetByIndex(0);

        ArrayList<String> x = move.getxData();
        ArrayList<Entry> y = move.getyData();
        int nEntries= move.getEntries();

        int seconds = c.get(Calendar.SECOND);
        mTmpChart.getId();

        int id = mTmpChart.getId();

        x.add(String.valueOf(seconds));
        y.add(new Entry(value, nEntries));
        nEntries++;

        move.setxData(x);
        move.setyData(y);
        move.setEntries(nEntries);

        LineDataSet emgCh1LineData;

        // create a dataset and give it a type
        emgCh1LineData = new LineDataSet(y, move.getMovement());
        emgCh1LineData.setDrawValues(false);
        // setRoll.setFillColor(Color.BLACK);
        emgCh1LineData.setLineWidth(2f);
        emgCh1LineData.setCircleRadius(0f);
        emgCh1LineData.setDrawFilled(false);

        //setRoll.setFillColor(Color.BLACK);
        int emgCh1Color = Color.rgb(0, 0, 255);
        emgCh1LineData.setColor(emgCh1Color, 100);
        // create a data object with the dataset
        data = new LineData(x, emgCh1LineData);

        // set data
        mTmpChart.setData(data);
        // let the chart know it's data has changed
        mTmpChart.notifyDataSetChanged();

        // limit the number of visible entries
        mTmpChart.setVisibleXRangeMaximum(120);
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        mTmpChart.moveViewToX(data.getXValCount() - 121);
    }

    public class ParameterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Movement> movements;

        public ParameterAdapter() {
            this.movements=movementList;
        }

        class AddCard extends RecyclerView.ViewHolder {
            public AddCard(View inflate) {
                super(inflate);
            }
        }

        class Card extends RecyclerView.ViewHolder {
            TextView movement;
            TextView channel;
            TextView remove;
            TextView updateMovement;
            ProgressBar strength;
            TextView graph;
            LinearLayout expandedView;
            LineChart lineChart;
            Movement mov;
            SeekBar threshold;
            public Card(View itemView, Movement m) {
                super(itemView);
                movement = (TextView) itemView.findViewById(R.id.movementText);
                channel = (TextView) itemView.findViewById(R.id.channelNameText);
                remove = (TextView) itemView.findViewById(R.id.removeCard);
                strength = (ProgressBar) itemView.findViewById(R.id.progressBarStrength);
                graph = (TextView) itemView.findViewById(R.id.graphTextButton);
                updateMovement = (TextView) itemView.findViewById(R.id.saveThreshold);
                expandedView = (LinearLayout) itemView.findViewById(R.id.detailsNr2);
                lineChart = (LineChart) itemView.findViewById(R.id.linechartCard);
                threshold = (SeekBar) itemView.findViewById(R.id.seekBar);
                mov=m;
                channel.setText(m.getChannel());
                movement.setText(m.getMovement());
                threshold.setProgress(Integer.valueOf(m.getThreshold()));
                mov.setProgressBar(strength);
                //Setup real time graph
                setupLineChart(mov, lineChart, mov.getId());
                // Start adding random data to the graph
                //startAddingRandomData();
            }

            public void startAddingRandomData() {
                new Thread(
                        new Runnable() {
                            public void run() {
                                while (true) {
                                    int generatedData;

                                    // Depending on what type of movement, add slightly different generated random data
                                    // (to be sure that the views are correctly updated when removed for instance)
                                    if(mov.getSettingsType().equals(SettingsDbHelper.MOVEMENTS_STRING[0])){
                                        // Add random generated data between the span 0-20
                                        generatedData=generateRandomData(0,20);
                                    } else if (mov.getSettingsType().equals(SettingsDbHelper.MOVEMENTS_STRING[1])) {
                                        // Add random generated data between the span 20-60
                                        generatedData=generateRandomData(20, 60);
                                    } else {
                                        // Add random generated data between the span 60-100
                                        generatedData=generateRandomData(60, 100);
                                    }
                                    //Set the progress bar
                                    strength.setProgress(generatedData);

                                    // Add the random generated data to the graph
                                    addEntry(mov, lineChart, generatedData);

                                    try {
                                        Thread.sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();

            }
        }

        public int generateRandomData(int n1, int n2){
            Random r = new Random();
            return r.nextInt(n2 - n1) + n1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View vSetting=null;
            View vCard=null;
            // If its the first position, then inflate the "add movement" card.
            if(viewType==0) {
                vSetting = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_parameter_cardview, parent, false);
                return new AddCard(vSetting);
            }else if(viewType>0) {
                // Otherwise, if it's not the first position, then inflate a "movement card" and create the corresponding movement item
                vCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_movement, parent, false);
                Movement m = movementList.get(viewType - 1);
                return new Card(vCard, m);
            }else {
                // For some reason, this occurs
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {


            // "Add movement" card
            if(position==0){
                ((AddCard)holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AddMovement.class);
                        startActivityForResult(intent, ADD_MOVEMENT);
                    }
                });

                // "Movement" item card
            } else if(position>0) {
                // Get the type of movement
                final Movement m = movementList.get(position - 1);
                final boolean isExpanded = position == mExpandedPosition;
                ((Card) holder).expandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ((Card) holder).graph.setActivated(isExpanded);
                ((Card) holder).updateMovement.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int thresholdVal=((Card) holder).threshold.getProgress();
                        m.setThreshold(String.valueOf(thresholdVal));
                        sendUpdateMovementRequest(m,1);
                        //Toast.makeText(getActivity(),"Movement updated",Toast.LENGTH_SHORT).show();
                    }
                });
                ((Card) holder).graph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mExpandedPosition = isExpanded ? -1 : position;
                        notifyDataSetChanged();
                    }
                });



                // This lets the real-time graph view be created before actual adding any data.
                m.setCardCreated(true);

                //Update the textviews inside the card
                ((Card)holder).movement.setText(m.getMovement());
                ((Card)holder).channel.setText(m.getChannel());
                ((Card)holder).remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.removeMovement)
                                //Add the positive button
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //User clicked yes, continue with removing the data
                                        Movement m = movementList.get(position - 1);
                                        //Send remove movement request
                                        sendUpdateMovementRequest(m,0);
                                        //Remove the item from the list
                                        remove(m);
                                        // Update the database entry to false
                                        int channel= Integer.valueOf(m.getChannel())-1;
                                        settingsdb.updateSetting(SettingsDbHelper.THRESHOLD_STRING[channel],"false");
                                        settingsdb.updateSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[channel],"0");
                                        //dcDbHelper.updateSetting(m.getSettingsType(),m.getChannel(),m.getThreshold(),m.getGain(),"false");
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //User cancelled the dialog, do nothing
                                    }
                                });

                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
            }
        }

        // Remove item from the list
        public void remove(Movement data) {
            int position = movementList.indexOf(data);
            movementList.remove(data);
            notifyDataSetChanged();
            // (+ 1 one to compensate for the first card)
            //notifyItemRemoved(position+1);
            rv.removeAllViews();
        }


        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public int getItemViewType(int position) {
            //Return the position
            return position;
        }


        @Override
        public int getItemCount() {
            // (+1 to compensate for the first card)
            return movementList.size()+1;
        }

        public void sendUpdateMovementRequest(Movement m, int active){
            int channel= Integer.valueOf(m.getChannel())-1;
            String movementName = m.getSettingsType();
            float mvc=0f;

            int movementIndex=-1;

            String thresholdStr= String.valueOf(m.getThreshold());
            int chNr=Integer.valueOf(m.getChannel())-1;

             //   Toast.makeText(getActivity(),"Error while updating threshold in database", Toast.LENGTH_LONG).show();


            for (int j = 0; j < movementNames.length; j++) {
                //Check which index that matches the one that i
                if (movementNames[j].equals(movementName)) {
                    movementIndex = j;
                    break;
                }
            }

            if(movementIndex>-1) {
                String[] mvcStr = settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[channel]);

                if (mvcStr != null) {
                    mvc = Float.valueOf(mvcStr[3]);
                }

                //Remap the threshold into correct span according to the mcv
                // [A, B] --> [a, b]
                // (val*max_contraction_value-0)/100
                float thres = (float)(Float.valueOf(m.getThreshold())*mvc)/100;

                //Insert the new threshold value in the database
                if(!settingsdb.updateSetting(SettingsDbHelper.THRESHOLD_STRING[chNr],String.valueOf(thres)))
                    settingsdb.insertSetting(SettingsDbHelper.THRESHOLD_STRING[chNr],String.valueOf(thres));

                byte[] threshold = convertFloat2Bytes(thres);
                byte[] mvC = convertFloat2Bytes(mvc);

                int[] data = new int[11];
                data[0]=movementIndex+1;
                data[1]=channel;

                for(int i=0;i<4;i++){
                    data[2+i]=(int)threshold[i];
                    data[6+i]=(int)mvC[i];
                }
                data[10]=active;

                CommunicationProtocol com = new CommunicationProtocol();
                MessageStruct msg = com.createMessage(Constants.UPDATE_MOVEMENT,data);
                mCallback.sendParameterMessage(msg);
            }
        }

    }

    public byte[] convertFloat2Bytes(float f){
        int bits = Float.floatToIntBits(f);
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(bits & 0xff);
        bytes[1] = (byte)((bits >> 8) & 0xff);
        bytes[2] = (byte)((bits >> 16) & 0xff);
        bytes[3] = (byte)((bits >> 24) & 0xff);
        return bytes;
    }

    public class DirectControlThread extends Thread {

        private boolean threadIsRunning = false;
        private boolean debug=false;
        private float mvc=0f;

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
            threadName = utilsThread.getThreadName();
            String prevTimeStamp = "null";
            String prevId = "null";
            String featureSingleData[]=null;
            //Set tag on the container equal to the thread ID to be able
            //to stop the thread in the parent fragment
            // emgContainer.setTag(threadName);

            // Store the thread name inside shared preferences_basic
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("DirectControlThread", threadName);
            editor.apply();

            while (threadIsRunning) {
                try {
                    sleep(200);
                    String info = utilsThread.getThreadSignature();
                    String featuresStr[] = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH);
                    Boolean featuresStreamOn = false;

                    if (featuresStr != null) {
                        featuresStreamOn = Boolean.valueOf(featuresStr[3]);
                    }

                    if(debug){
                        featuresStreamOn=true;
                    }

                    if (featuresStreamOn) {
                        float latestData = 0f;

                            //Get the latest feature data
                            featureSingleData = emgdb.getLatestData();

                        if(debug){
                            featureSingleData = new String[]{"0"};
                        }

                        //Make sure that there is actually data from the database
                        if (featureSingleData != null) {
                            String currentId = featureSingleData[0];

                            // Check that we are not adding old data to the graph and the progressbar by comparing the ID from the database
                            if (!prevId.equals(currentId)) {
                                //Save the currentId to previous ID
                                prevId=currentId;
                                if(debug){
                                    prevId="null";
                                }
                                //Loop thorough each of the movements cards that are shown in the list
                                for (int i = 0; i < movementList.size(); i++) {
                                    //Extract the movement for the current view
                                    Movement mov = movementList.get(i);

                                    if(debug){
                                        int chn=Integer.valueOf(mov.getChannel())-1;
                                        String[] mvcStr = settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[chn]);
                                        if (mvcStr != null) {
                                            mvc = Float.valueOf(mvcStr[3]);
                                        }
                                    }
                                    // Depending on what type of movement, add corresponding data to progress
                                    // (to be sure that the views are correctly updated when removed for instance)
                                    if (mov.getSettingsType().equals(movementNames[0])) {
                                        if(debug){
                                            latestData =(Float.valueOf(generateRandomData(0,20)));
                                        } else {
                                            // Open hand movement
                                            latestData = Float.valueOf(featureSingleData[2]);
                                        }
                                    } else if (mov.getSettingsType().equals(movementNames[1])) {
                                        if(debug){
                                            latestData =(Float.valueOf(generateRandomData(20,40)));
                                        } else {
                                            // Close hand movement
                                            latestData = Float.valueOf(featureSingleData[3]);
                                        }
                                    } else if (mov.getSettingsType().equals(movementNames[4])) {
                                        if(debug){
                                            latestData =(Float.valueOf(generateRandomData(40,80)));
                                        } else {
                                            // Pronation
                                            latestData = Float.valueOf(featureSingleData[4]);
                                        }
                                    } else if (mov.getSettingsType().equals(movementNames[5])) {
                                        if(debug){
                                            latestData =(Float.valueOf(generateRandomData(80,100)));
                                        } else {
                                            // Supination
                                            latestData = Float.valueOf(featureSingleData[5]);
                                        }
                                    }

                                    // Verify that the each of the cards have been inflated correctly before adding any data
                                    if (mov.getcardCreated()) {
                                        // Check if current value exceeds the maximum value
                                        int channel = Integer.valueOf(mov.getChannel()) - 1;
                                        String[] maxProgressValue = settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[channel]);
                                        // To map the feature values in between 0-100, use linear mapping
                                        // [A, B] --> [a, b]
                                        // current_val*100/(feature_max_val)
                                        float mvc = 0f;
                                        float latestDataProgress=0f;

                                        if (maxProgressValue != null) {
                                            mvc = Float.valueOf(maxProgressValue[3]);
                                            latestDataProgress = latestData * 100 / mvc;
                                        }

                                        if(!debug) {
                                            //Set the progress bar
                                            mov.getProgressBar().setProgress((int) latestDataProgress);

                                            // Add the data from the feature channel to the graph
                                            addEntry(mov, mov.getLineCh(), latestData);
                                        }else {
                                            //Set the progress bar
                                            mov.getProgressBar().setProgress((int) latestData);

                                            // Add the random generated data to the graph
                                            addEntry(mov, mov.getLineCh(), latestData);
                                        }

                                    }

                                }
                            }
                        }
                    }

                    //addEntry(generateRandomData(40, 80));
                    System.out.println("In thread: " + info);



                }catch(InterruptedException e){
                    threadIsRunning = false;
                }

            }
        }
    }
    public int generateRandomData(int n1, int n2){
        Random r = new Random();
        return r.nextInt(n2 - n1) + n1;
    }
}
