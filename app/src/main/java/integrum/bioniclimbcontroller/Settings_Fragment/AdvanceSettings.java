package integrum.bioniclimbcontroller.Settings_Fragment;


import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;
import java.util.jar.Manifest;

import integrum.bioniclimbcontroller.Bluetooth.CommunicationProtocol;
import integrum.bioniclimbcontroller.Bluetooth.MessageStruct;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Parameter_Fragment.ParameterFragmentV2;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-24.
 */
public class AdvanceSettings extends PreferenceFragment {
    // Storage Permissions

    private ListPreference mListPrefSampling;
    private ListPreference mListPrefPattern;
    private SwitchPreference mListPrefNeuro;
    private Preference mListPrefCalibrate;
    SettingsDbHelper settingsdb;
    View view;
    AdvanceSettingsListener mCallback;


    public interface AdvanceSettingsListener{
        public void sendSettingsMessage(MessageStruct msg);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (AdvanceSettings.AdvanceSettingsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AdvanceSettingsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        // Database for Settings
        settingsdb=new SettingsDbHelper(getActivity());

            // Load the preferences_basic from an XML resource
            addPreferencesFromResource(R.xml.preferences_advance);

        mListPrefSampling = (ListPreference) getPreferenceManager().findPreference("samplingsFreq");
        mListPrefPattern = (ListPreference) getPreferenceManager().findPreference("pattern");
        mListPrefNeuro = (SwitchPreference) getPreferenceManager().findPreference("neuroStimulator");
        mListPrefCalibrate =  getPreferenceManager().findPreference("calibrate");

        mListPrefSampling.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                return true;
            }
        });

        mListPrefPattern.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                return true;
            }
        });


        mListPrefNeuro.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                final boolean[] switchValue = {false};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String switchMode;

                // Determine which string to use
                if(mListPrefNeuro.isChecked()) switchMode = getString(R.string.off);
                else switchMode=getString(R.string.on);

                //Set up alert dialog builder with corresponding settings
                builder.setMessage("Switch neurostimulator " + switchMode)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean checked= mListPrefNeuro.isChecked();
                        if(checked) {
                            // Send message "TURN OFF NEURO STIMULATOR"
                            mListPrefNeuro.setChecked(false);
                            CommunicationProtocol com = new CommunicationProtocol();
                            MessageStruct msg = com.createMessage(Constants.NS_ENABLE_DISABLE,new int[]{0});
                            mCallback.sendSettingsMessage(msg);
                        }
                        else {
                            // Send message "TURN ON NEURO STIMULATOR"
                            mListPrefNeuro.setChecked(true);
                            CommunicationProtocol com = new CommunicationProtocol();
                            MessageStruct msg = com.createMessage(Constants.NS_ENABLE_DISABLE,new int[]{1});
                            mCallback.sendSettingsMessage(msg);
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   // User pressed no, return false (means we don't make a swtich)
                        //switchValue[0] = false;
                        //mListPrefNeuro.setChecked(false);
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                return switchValue[0];
            }
        });


        mListPrefCalibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                return true;
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

}


