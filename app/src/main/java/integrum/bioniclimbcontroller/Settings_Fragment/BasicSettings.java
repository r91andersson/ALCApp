package integrum.bioniclimbcontroller.Settings_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Login_Fragment.LoginFragment;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-24.
 */
public class BasicSettings extends PreferenceFragment {
    private ListPreference mListPrefStorage;
    private EditTextPreference mListPrefName;
    private CheckBoxPreference mListPrefUpdates;
    private Preference mLogout;
    private SharedPreferences pref;
    private BasicSettingsListener mCallBack;
    private SwitchPreference mListPrefDebug;
    private SettingsDbHelper settingsdb;
    private Preference mListPrefDatabase;
    private Preference mPrefMvc;
    private int nChannels=8;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // Container Activity must implement this interface
    public interface BasicSettingsListener {
        public int initMain();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBack = (BasicSettingsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BasicSettingsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences_basic from an XML resource
        addPreferencesFromResource(R.xml.preferences_basic);
        // Database for Settings
        settingsdb=new SettingsDbHelper(getActivity());

        pref = getActivity().getPreferences(0);
        mListPrefStorage = (ListPreference) getPreferenceManager().findPreference("downloadType");
        mListPrefName = (EditTextPreference) getPreferenceManager().findPreference("username");
        mListPrefUpdates = (CheckBoxPreference) getPreferenceManager().findPreference("applicationUpdates");
        mLogout = (Preference) getPreferenceManager().findPreference("log_out");
        mListPrefDebug = (SwitchPreference) getPreferenceManager().findPreference("pref_key_advance_mode");
        mListPrefDatabase = getPreferenceManager().findPreference("database");
        mPrefMvc = getPreferenceScreen().findPreference("resetMvc");

        //Check if the debug mode is active, if so manually turn on the switch
        String debugStr[]=settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE);
        if(debugStr!=null) {
            int debugMode = Integer.valueOf(debugStr[3]);
            if(debugMode==Constants.DEBUG_MODE){
                mListPrefDebug.setChecked(true);
            } else {
                mListPrefDebug.setChecked(false);
            }

        }

        mListPrefStorage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // insert custom code
                return true;
            }
        });

        mListPrefName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                return true;
            }
        });

        mListPrefUpdates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                return true;
            }
        });

        mListPrefDatabase.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                verifyStoragePermissions(getActivity());
                return true;
            }
        });

        mListPrefDebug.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Intent intent = new Intent(getActivity(), DebugPassword.class);
                startActivityForResult(intent, Constants.DEBUG_PW_REQUEST);
                return false;
            }
        });

        mPrefMvc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Reset all of the maximum voluntary contraction values
                for (int i=0;i<nChannels;i++) {
                    settingsdb.insertSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[i], "0.0");
                }
                Toast.makeText(getActivity(),"Variables cleared",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(Constants.IS_LOGGED_IN, false);
                editor.putString(Constants.EMAIL,"");
                editor.putString(Constants.NAME,"");
                editor.putString(Constants.UNIQUE_ID,"");
                editor.apply();
                goToLogin();
                return true;
            }
        });

    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE );
        } else exportDatabase(activity);

    }

    @SuppressWarnings("resource")
    public static void exportDatabase(Context ctx) {

        File backupEmgDB = null;
        File backupXyzDB = null;
        File backupSettingsDB = null;
        ArrayList<Uri> uris = new ArrayList<Uri>();

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            String currentEmgDBPath = "//data//" + ctx.getPackageName()
                    + "//databases//" + "EMGDatabase.db" + "";
            String currentXyzDBPath = "//data//" + ctx.getPackageName()
                    + "//databases//" + "XYZDatabase.db" + "";
            String currentSettingsDBPath = "//data//" + ctx.getPackageName()
                    + "//databases//" + "SettingsDatabase.db" + "";

            File currentEmgDB = new File(data, currentEmgDBPath);
            File currentXyzDB = new File(data, currentXyzDBPath);
            File currentSettingsDB = new File(data,currentSettingsDBPath);


            backupEmgDB = new File(sd, "EMGDatabase.db");
            backupXyzDB = new File(sd, "XYZDatabase.db");
            backupSettingsDB = new File(sd,"SettingsDatabase.db");



            if (currentEmgDB.exists()) {

                FileChannel src = new FileInputStream(currentEmgDB)
                        .getChannel();
                FileChannel dst = new FileOutputStream(backupEmgDB)
                        .getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                uris.add(Uri.fromFile(backupEmgDB));
            }

            if (currentXyzDB.exists()) {

                FileChannel src = new FileInputStream(currentXyzDB)
                        .getChannel();
                FileChannel dst = new FileOutputStream(backupXyzDB)
                        .getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                uris.add(Uri.fromFile(backupXyzDB));
            }

            if (currentSettingsDB.exists()) {

                FileChannel src = new FileInputStream(currentSettingsDB)
                        .getChannel();
                FileChannel dst = new FileOutputStream(backupSettingsDB)
                        .getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                uris.add(Uri.fromFile(backupSettingsDB));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);

        emailIntent.setType("*/*");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { "r91.andersson@gmail.com" });

        Random r = new Random();

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Local db " + r.nextInt());
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent completeWith = Intent.createChooser(emailIntent, "Export database");
        completeWith.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(completeWith);
    }

    private void goToLogin(){
        mCallBack.initMain();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Database for Settings
        settingsdb=new SettingsDbHelper(getActivity());

        switch (requestCode){
            case Constants.DEBUG_PW_REQUEST:

                if(resultCode == Activity.RESULT_OK){
                    int pw = data.getExtras().getInt("pw");

                    if (pw==Constants.DEBUG_PASSWORD){
                        String debugStr[]=settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE);
                        if(debugStr!=null) {
                            int debugMode = Integer.valueOf(debugStr[3]);
                            if(debugMode==Constants.DEBUG_MODE){
                                settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE,Constants.USER_MODE);
                                mListPrefDebug.setChecked(false);
                            } else {
                                settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE,Constants.DEBUG_MODE);
                                mListPrefDebug.setChecked(true);
                            }

                        } else {
                            //Password is correct, however, no settings is found within the database, most probably because it's empty.
                            settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE,Constants.DEBUG_MODE);
                        }

                        // Restart the application to enable the change to take effect
                        Intent i = getActivity().getPackageManager()
                                .getLaunchIntentForPackage( getActivity().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else {
                        //Show snackbar on top of layout because if shown on bottom, it hides behind the bottombar layout.
                        Snackbar snack = Snackbar.make(getView(), "Wrong password", Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                        params.gravity = Gravity.TOP;
                        view.setLayoutParams(params);
                        snack.show();
                    }
                    String debugStr[]=settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE);
                    if(debugStr!=null) {
                        int debugMode = Integer.valueOf(debugStr[3]);
                        if (debugMode != Constants.DEBUG_MODE) {
                            mListPrefDebug.setChecked(false);
                        }
                    }
                }
                break;
        }


    }

}
