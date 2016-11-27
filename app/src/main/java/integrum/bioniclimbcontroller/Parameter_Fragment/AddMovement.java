package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.security.spec.ECField;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.DcDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-11-04.
 */
public class AddMovement extends Activity{
    private Button addBtn;
    private Button cancelBtn;
    private Spinner movementSpinner;
    private Spinner channelSpinner;
    private EditText threshold;
    private EditText gain;
    private CheckBox enable;
    RecyclerView rv;
    private String[] movementsName;
    private String [] channelName;
    SettingsDbHelper settingsdb;
    DcDbHelper dcDbHelper;
    private String[] movements = SettingsDbHelper.MOVEMENTS_STRING;
    private boolean [] movementsActivated= new boolean[movements.length];
    private boolean [] channelsActivated= new boolean[SettingsDbHelper.THRESHOLD_STRING.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsdb = new SettingsDbHelper(this);
        dcDbHelper = new DcDbHelper(this);
        setContentView(R.layout.cardview_add_movement);

        checkActiveMovementsChannels();
        //initSettings();

        movementsName = Constants.movementNames;//getResources().getStringArray(R.array.spinnerItemsMovements);
        channelName = getResources().getStringArray(R.array.channels);

        SpinnerDropDownChannelsAdapter  sddadapterChannel = new SpinnerDropDownChannelsAdapter(this,channelName);
        SpinnerDropDownMovementsAdapter  sddadapterMovement = new SpinnerDropDownMovementsAdapter(this,movementsName);

        addBtn = (Button) findViewById(R.id.buttonSaveMov);
        cancelBtn = (Button) findViewById(R.id.buttonReturnMov);
        movementSpinner = (Spinner) findViewById(R.id.movementSpinner2);
        channelSpinner = (Spinner) findViewById(R.id.channel);
        threshold = (EditText) findViewById(R.id.thresholdEditText);
        gain = (EditText) findViewById(R.id.gainEditText);
        enable = (CheckBox) findViewById(R.id.enableMovementcheckBox);

        movementSpinner.setAdapter(sddadapterMovement);
        channelSpinner.setAdapter(sddadapterChannel);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String movement = movementSpinner.getSelectedItem().toString();
                String channel = channelSpinner.getSelectedItem().toString();
                String thresholdStr = threshold.getText().toString();
                String gainStr = gain.getText().toString();
                boolean enableMovement = enable.isSelected();
                int pos = movementSpinner.getSelectedItemPosition();

                    if(movementsActivated[movementSpinner.getSelectedItemPosition()] || channelsActivated[channelSpinner.getSelectedItemPosition()]) {
                        Snackbar.make(view, "Movement or channel is already activated", Snackbar.LENGTH_LONG).show();
                    }   else if ((pos==2|| pos==5 )&& (!movementsActivated[0] && !movementsActivated[1])){
                        Snackbar.make(view, "Open hand and close hand movement must be activated first", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (!movement.isEmpty() && !channel.isEmpty() && !thresholdStr.isEmpty() && !gainStr.isEmpty()) {
                            Intent result = new Intent();
                            int channelNr=Integer.valueOf(channel)-1;
                            result.putExtra(ParameterFragmentV2.movementCode, movement);
                            result.putExtra(ParameterFragmentV2.thresholdCode, thresholdStr);
                            result.putExtra(ParameterFragmentV2.channelCode, channelNr);
                            result.putExtra(ParameterFragmentV2.gainCode, gainStr);
                            result.putExtra(ParameterFragmentV2.enableCode, String.valueOf(enableMovement));
                            result.putExtra(ParameterFragmentV2.movementNrCode, movementSpinner.getSelectedItemPosition());
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        } else {
                            Snackbar.make(view, "Fields are empty !", Snackbar.LENGTH_LONG).show();
                        }
                    }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    public void checkActiveMovementsChannels(){
        int nChannels=SettingsDbHelper.THRESHOLD_STRING.length;
        for(int i=0; i< nChannels; i++){
            String value [] = settingsdb.getLatestSetting(SettingsDbHelper.THRESHOLD_STRING[i]);
            if(value!=null) {
                try {
                    // If value[3] is a number equals that this channels is active
                    if(value[3].matches(".*\\d+.*")){
                        channelsActivated[i]=true;

                        // We found the active channel, now find which movement that corresponds to
                        int index = -1;
                        for (int j = 0; j < ParameterFragmentV2.movementNames.length; j++) {
                            //Check which index that matches the one that i
                            if (ParameterFragmentV2.movementNames[j].equals(value[4])) {
                                index = j;
                                break;
                            }
                        }

                        if(index>-1){
                            //Index >-1 means we found a match in the array, set this to active in the movementsActivated array.
                            movementsActivated[index]=true;
                        }

                    }
                } catch (Exception e) {
                }
            }
        }
    }


    public void initSettings(){
        String[][]iStr=dcDbHelper.getAllPatRecMov();
        int nRows=iStr.length;
        for(int i=0; i< nRows; i++){
            movementsActivated[i] = Boolean.parseBoolean(iStr[i][6]);
        }
    }

    public class SpinnerDropDownChannelsAdapter extends BaseAdapter implements
            SpinnerAdapter {
        Context context;
        String[] textFields;

        SpinnerDropDownChannelsAdapter(Context ctx,String[] txt) {
            context = ctx;
            textFields=txt;
        }

        @Override
        public int getCount() {
            return textFields.length;
        }

        @Override
        public String getItem(int pos) {
            // TODO Auto-generated method stub
            return textFields[pos];
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = getLayoutInflater().inflate(R.layout.spinner_list_item, parent, false);
            TextView text = (TextView) row.findViewById(R.id.title_text_view);
            text.setText(textFields[position]);
            if(channelsActivated[position])
                text.setTextColor(Color.GRAY);
            else
                text.setTextColor(Color.BLACK);
            return text;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            View row = getLayoutInflater().inflate(R.layout.spinner_list_item, parent, false);
            TextView text = (TextView) row.findViewById(R.id.title_text_view);
            text.setText(textFields[position]);

                    if(channelsActivated[position])
                    text.setTextColor(Color.GRAY);
                    else
                    text.setTextColor(Color.BLACK);

            return row;
        }
    }

    public class SpinnerDropDownMovementsAdapter extends BaseAdapter implements
            SpinnerAdapter {
        Context context;
        String[] textFields;

        SpinnerDropDownMovementsAdapter(Context ctx,String[] txt) {
            context = ctx;
            textFields=txt;
        }

        @Override
        public int getCount() {
            return textFields.length;
        }

        @Override
        public String getItem(int pos) {
            // TODO Auto-generated method stub
            return textFields[pos];
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = getLayoutInflater().inflate(R.layout.spinner_list_item, parent, false);
            TextView text = (TextView) row.findViewById(R.id.title_text_view);
            text.setText(textFields[position]);
            if(movementsActivated[position])
                text.setTextColor(Color.GRAY);
            else
                text.setTextColor(Color.BLACK);
            return text;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            View row = getLayoutInflater().inflate(R.layout.spinner_list_item, parent, false);
            TextView text = (TextView) row.findViewById(R.id.title_text_view);
            text.setText(textFields[position]);

            if(position==2){
                if(movementsActivated[0]&&movementsActivated[1] && !movementsActivated[position]){
                    text.setTextColor(Color.BLACK);
                } else {
                    text.setTextColor(Color.GRAY);
                }
            } else if(position==5){
                if(movementsActivated[0]&&movementsActivated[1] && !movementsActivated[position]){
                    text.setTextColor(Color.BLACK);
                } else {
                    text.setTextColor(Color.GRAY);
                }
            } else {
                if(movementsActivated[position])
                    text.setTextColor(Color.GRAY);
                else
                    text.setTextColor(Color.BLACK);
            }




            return row;
        }
    }



}