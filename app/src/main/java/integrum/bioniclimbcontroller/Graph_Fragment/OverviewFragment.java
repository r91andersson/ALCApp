package integrum.bioniclimbcontroller.Graph_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import integrum.bioniclimbcontroller.Bluetooth.CommunicationProtocol;
import integrum.bioniclimbcontroller.Bluetooth.MessageStruct;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.MainActivity;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-23.
 */
public class OverviewFragment extends Fragment {
    SettingsDbHelper settings;
    RecyclerView rvOV;
    OverviewFragmentListener mCallbackOverview;



    public interface OverviewFragmentListener {
        public void sendMessageOverview (MessageStruct msg);
        public void sendCommand(int command, String message);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackOverview = (OverviewFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OverviewFragmentListener");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_overview, container, false);
        settings = new SettingsDbHelper(getActivity());
        rvOV = (RecyclerView) view.findViewById(R.id.rvOverview);

        rvOV.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvOV.setLayoutManager(llm);
        rvOV.setNestedScrollingEnabled(false);
        MyOverviewAdapter adapter = new MyOverviewAdapter();
        rvOV.setAdapter(adapter);

        return view;
    }

    public class MyOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public MyOverviewAdapter() {
        }


        class ViewHolder2 extends RecyclerView.ViewHolder {
           // Switch emgSwitch;
            Switch orientationSwitch;
            Switch tempSwitch;
            Switch directControlSwitch;
            Switch extractFeaturesSwitch;
            Switch commandModeSwitch;
            Button statusCheck;
            TextView batteryVoltage;
            TextView temperature;
            TextView sensorHand;
            TextView sdcard;
            TextView inemo;
            TextView controlMode;
            TextView enableNS;

            public ViewHolder2(View itemView) {
                super(itemView);
               // emgSwitch=(Switch) itemView.findViewById(R.id.switchEmg);
                orientationSwitch=(Switch) itemView.findViewById(R.id.switchImu);
                statusCheck = (Button) itemView.findViewById(R.id.statusCheck);
                batteryVoltage = (TextView) itemView.findViewById(R.id.batteryVoltage);
                temperature = (TextView) itemView.findViewById(R.id.temperature);
                sensorHand = (TextView) itemView.findViewById(R.id.sensorHand);
                sdcard = (TextView) itemView.findViewById(R.id.sdcard);
                inemo = (TextView) itemView.findViewById(R.id.inemo);
                controlMode =(TextView) itemView.findViewById(R.id.ctrlMode);
                enableNS = (TextView) itemView.findViewById(R.id.neurostimulator);
                extractFeaturesSwitch = (Switch) itemView.findViewById(R.id.extractFeatures);
                commandModeSwitch = (Switch) itemView.findViewById(R.id.switchCommandMode);

               // String emgSwitchState [] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_SWITCH);
                String orientationSwitchState [] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH);
                String extractFeaturesState [] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH);
                String commandModeState[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH);

                String sensorHandInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_SENSOR_HAND);
                String batteryVoltageInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_BATTERY_VOLTAGE);
                String temperatureInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_TEMPERATURE);
                String sdcardInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_SDCARD);
                String inemoInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_INEMO);
                String controlModeInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_CTRLMODE);
                String enableNsInfo[] = settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_ENABLENS);
/*
                if(emgSwitchState!=null) {
                    emgSwitch.setChecked(Boolean.valueOf(emgSwitchState[3]));
                }
*/
                if(extractFeaturesState!=null){
                    extractFeaturesSwitch.setChecked(Boolean.valueOf(extractFeaturesState[3]));
                }


                if(orientationSwitchState!=null){
                    orientationSwitch.setChecked(Boolean.valueOf(orientationSwitchState[3]));
                }

                if(commandModeState!=null){
                    commandModeSwitch.setChecked(Boolean.valueOf(commandModeState[3]));
                }


                if(sensorHandInfo!=null){
                    if(sensorHandInfo[3].equals("0")) sensorHand.setText("Not present");
                    else sensorHand.setText("Present");
                }

                if(batteryVoltageInfo!=null){
                    batteryVoltage.setText(batteryVoltageInfo[3]);
                }

                if(temperatureInfo!=null){
                    temperature.setText(temperatureInfo[3]);
                }

                if(sdcardInfo!=null){
                    if(sdcardInfo[3].equals("0")) sdcard.setText("Not present");
                    else sdcard.setText("Present");
                }

                if(inemoInfo!=null){
                    if(inemoInfo[3].equals("0")) inemo.setText("Disabled");
                    else inemo.setText("Enabled");
                }

                if(controlModeInfo!=null){
                    if(controlModeInfo[3].equals("0")) controlMode.setText("Direct Control");
                    else if(controlModeInfo[3].equals("1")) controlMode.setText("Pattern Recognition");
                    else if (controlModeInfo[3].equals("2")) controlMode.setText("LDA");
                }

                if(enableNsInfo!=null){
                    if(enableNsInfo[3].equals("0")) enableNS.setText("Disabled");
                    else enableNS.setText("Enabled");
                }
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
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                default: return new ViewHolder2(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_overview_sync_cardview, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {

            if(position==0){
                final ViewHolder2 overview = (ViewHolder2) holder;
/*
                overview.emgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        //Check if the switch mode is active or not, manually turn the switch
                        String debugStr[]=settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_SWITCH);
                        if(debugStr!=null) {
                            boolean streamOn = Boolean.valueOf(debugStr[3]);
                            if(!streamOn){
                                if(MainActivity.deviceConnected) {
                                    overview.emgSwitch.setChecked(true);
                                    settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_SWITCH, "true");
                                //    sendStartFeatureExtractionRequest();
                                } else {
                                    Toast.makeText(getActivity(),"You are not connected to the artificial limb",Toast.LENGTH_SHORT).show();
                                    overview.emgSwitch.setChecked(false);
                                }
                            } else {
                                overview.emgSwitch.setChecked(false);
                                settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_SWITCH, "false");
                            }

                        } else {
                            //The settings could not be found in the database, insert the setting
                            settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EMG_SWITCH, "false");
                        }

                    }
                });
*/
                overview.orientationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        //Check if the switch mode is active or not, manually turn the switch
                        String debugStr[]=settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH);
                        if(debugStr!=null) {
                            boolean streamOn = Boolean.valueOf(debugStr[3]);
                            if(!streamOn){
                                //Check that we are connected to the device before try to send command
                                if(MainActivity.deviceConnected) {
                                    overview.orientationSwitch.setChecked(true);
                                    settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH, "true");
                                    sendStreamEnabledImuRequest();
                                } else {
                                    overview.orientationSwitch.setChecked(false);
                                }
                            } else {
                                overview.orientationSwitch.setChecked(false);
                                settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH, "false");
                                sendStreamDisabledImuRequest();
                            }

                        } else {
                            //The settings could not be found in the database, insert the setting
                            settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH, "false");
                        }


                    }
                });

                overview.extractFeaturesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String featuresStr[]=settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH);
                        if(featuresStr!=null) {
                            boolean streamOn = Boolean.valueOf(featuresStr[3]);
                            if(!streamOn){
                                //Check that we are connected to the device before try to send command
                                if(MainActivity.deviceConnected) {
                                    overview.extractFeaturesSwitch.setChecked(true);
                                    settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH, "true");
                                    sendStartFeatureExtractionRequest();
                                } else {
                                    overview.extractFeaturesSwitch.setChecked(false);
                                }
                            } else {
                                overview.extractFeaturesSwitch.setChecked(false);
                                settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH, "false");
                               if(MainActivity.deviceConnected) {
                                   //Disable the feature extraction
                               }
                            }
                        } else {
                            //The settings could not be found in the database, insert the setting
                            settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH, "false");
                        }
                    }
                });

                overview.commandModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String commandModeStr[]=settings.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH);
                        if(commandModeStr!=null) {
                            boolean commandMode = Boolean.valueOf(commandModeStr[3]);
                            if(!commandMode){
                                //Check that we are connected to the device before try to send command
                                if(MainActivity.deviceConnected) {
                                    overview.commandModeSwitch.setChecked(true);
                                    settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH, "true");
                                    sendSetCommandModeRequest();

                                } else {
                                    overview.commandModeSwitch.setChecked(false);
                                }
                            } else {
                                overview.commandModeSwitch.setChecked(false);
                                settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH, "false");
                                if(MainActivity.deviceConnected) {
                                    sendSetControlModeRequest();
                                }
                            }
                        } else {
                            //The settings could not be found in the database, insert the setting
                            settings.insertSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH, "false");
                        }
                    }
                });

                overview.statusCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendStatusCheck();
                        //Refresh the view
                        rvOV.removeAllViews();
                    }
                });
            }

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

    }


    public void sendStreamEnabledImuRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.STREAMING_IMU_DATA,new int[]{1});
        String streamImuRequest=msg.getMessageAsString();
        //mCallbackOverview.sendCommand(Constants.SEND_MESSAGE, streamImuRequest);
        mCallbackOverview.sendMessageOverview(msg);
    }

    public void sendStreamDisabledImuRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.STREAMING_IMU_DATA,new int[]{0});
        String streamImuRequest=msg.getMessageAsString();
        //mCallbackOverview.sendCommand(Constants.SEND_MESSAGE, streamImuRequest);
        mCallbackOverview.sendMessageOverview(msg);
    }

    public void sendStatusCheck() {
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.STATUS_CHECK,new int[]{1});
        String statusCheckRequest=msg.getMessageAsString();
        //mCallbackOverview.sendCommand(Constants.SEND_MESSAGE, statusCheckRequest);
        mCallbackOverview.sendMessageOverview(msg);
    }

    public void sendStartFeatureExtractionRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.START_FEATURES_ACQ,new int[]{4});
        String featExtr=msg.getMessageAsString();
       // mCallbackOverview.sendCommand(Constants.SEND_MESSAGE, featExtr);
        mCallbackOverview.sendMessageOverview(msg);
    }

    public void sendSetControlModeRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.SET_CONTROL_MODE,new int[]{1});
        mCallbackOverview.sendMessageOverview(msg);
    }

    public void sendSetCommandModeRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.SET_COMMAND_MODE,new int[]{1});
        mCallbackOverview.sendMessageOverview(msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settings.close();
    }
}
