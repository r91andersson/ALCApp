package integrum.bioniclimbcontroller;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
//import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Set;

import com.nullwire.trace.ExceptionHandler;

import integrum.bioniclimbcontroller.Bluetooth.BluetoothMessageService;
import integrum.bioniclimbcontroller.Bluetooth.DeviceListActivity;
import integrum.bioniclimbcontroller.Bluetooth.MessageStruct;
import integrum.bioniclimbcontroller.Database.EmgDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Database.XyzDbHelper;
import integrum.bioniclimbcontroller.Debug_Fragment.DebugFragment;
import integrum.bioniclimbcontroller.Graph_Fragment.EMGFragment;
import integrum.bioniclimbcontroller.Graph_Fragment.GraphFragment;
import integrum.bioniclimbcontroller.Graph_Fragment.GraphFragmentExtended;
import integrum.bioniclimbcontroller.Graph_Fragment.OverviewFragment;
import integrum.bioniclimbcontroller.Home_Fragment.HomeFragment;
import integrum.bioniclimbcontroller.Login_Fragment.LoginFragment;
import integrum.bioniclimbcontroller.Parameter_Fragment.ControlFragment;
import integrum.bioniclimbcontroller.Parameter_Fragment.ParameterFragment;
import integrum.bioniclimbcontroller.Parameter_Fragment.ParameterFragmentV2;
import integrum.bioniclimbcontroller.Parameter_Fragment.SelectControlMethod;
import integrum.bioniclimbcontroller.Settings_Fragment.AdvanceSettings;
import integrum.bioniclimbcontroller.Settings_Fragment.BasicSettings;
import integrum.bioniclimbcontroller.Settings_Fragment.SettingsFragment;

public class MainActivity extends FragmentActivity  implements HomeFragment.HomeFragmentListener, EMGFragment.EMGFragmentListener, OverviewFragment.OverviewFragmentListener, BasicSettings.BasicSettingsListener, LoginFragment.LoginListener, ControlFragment.ControlFragmentListener,ParameterFragmentV2.ParameterV2MethodListener, AdvanceSettings.AdvanceSettingsListener{

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;


    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;
    /**
     * Member object for the chat services
     */
    private BluetoothMessageService mMessageService = null;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * String builder for received data
     */
    private StringBuilder recDataString = new StringBuilder();

    /**
     * Bottombar handler for changing layouts
     */
    private BottomBar bottomBar;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    //Advance settings constant
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    // Initial bluetooth state
    private int bluetoothState=0;

    // Initial layout mode
    private int layoutMode=Constants.USER_MODE;

    // TAG for activity
    private static final String TAG = "HomeFragment";

    // Orientation values
    private int rollValue;
    private int pitchValue;
    private int yawValue;


    // Database for EMG data
    EmgDbHelper emgdb;
    XyzDbHelper xyzdb;
    SettingsDbHelper settingsdb;

    FragmentTransaction ft;

    // Local bundle
    Bundle tempBundle;
    BottomBar tmpBar=null;
    BottomBar tmpBar2=null;

    private SharedPreferences pref;

    public static boolean deviceConnected = false;
    public static boolean ack = false;
    public static int lastCommandSent=0;
    public static boolean pendingMessage=false;
    /* ============================== Interface functions =======================================*/

    public void sendCommand(int command, String message) {
        switch(command) {
            case 1:
                System.out.println("Starting bluetooth thread");
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                break;
            case 2:
                setupListDevices();
                break;
            case Constants.SEND_MESSAGE:
                sendMessage(message);
        }
    }

    public void setRollValue(String rollVal){
        HomeFragment.rollValue.setText(rollVal);
        String tmpStr=rollVal;
        tmpStr=tmpStr.replace("Roll: ","");
        rollValue=Integer.valueOf(tmpStr);
    }

    @Override
    public void sendMessageOverview(MessageStruct msg) {
                sendMessage(msg);
    }

    @Override
    public void sendMessageControlFragment(MessageStruct msg){
        sendMessage(msg);
    }

    @Override
    public void sendSettingsMessage(MessageStruct msg) {
        sendMessage(msg);
    }

    @Override
    public void sendParameterMessage(MessageStruct msg) {
        sendMessage(msg);
    }

    public void setBatteryVoltage(String voltage){
        HomeFragment.batteryVoltage.setText(voltage);
    }

    @Override
    public int getRollValue() {
        return rollValue;
    }

    @Override
    public void setBluetoothState(int state) {
        bluetoothState=state;
    }

    @Override
    public int getBluetoothState() {
        return bluetoothState;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    /*========================================================================================*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get database for EMG
        emgdb = new EmgDbHelper(this);
        // Get database for IMU
        xyzdb = new XyzDbHelper(this);
        // Database for Settings
        settingsdb = new SettingsDbHelper(this);


        //Manually turn switches off in the beginning if we're not already connected
        if(!deviceConnected) {
                    settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH, "false");
                    settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH,"false");
        }

        //Set up crash report functionality
        ExceptionHandler.register(this, "https://crashreport.000webhostapp.com");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pref = getPreferences(0);
        //initFragment();
        setupBarBottomLayout();
    }

    /*
    private void initFragment(){
        Fragment fragment;
        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            setupBarBottomLayout();
            return;
        }else {
            fragment = new LoginFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,fragment);
        ft.commit();
    }
*/
    public void parseMessageFunction(String testString1) {

        StringBuilder strBuilder = new StringBuilder();
        String rollSubStrFinal=null;
        String pitchSubStrFinal=null;
        String yawSubStrFinal=null;
        boolean rollB=false;
        boolean pitchB=false;
        boolean yawB=false;
        int rollLineIndexStart = testString1.indexOf("Roll:");
        int pitchLineIndexStart = testString1.indexOf("Pitch:");
        int yawLineIndexStart = testString1.indexOf("Yaw:");
        int rollVal=0;
        int pitchVal=0;
        int yawVal=0;
        if(rollLineIndexStart!=-1 && pitchLineIndexStart!=-1 && yawLineIndexStart!=-1) {

            int rollLineIndexEnd = rollLineIndexStart + 11;
            int pitchLineIndexEnd = pitchLineIndexStart + 12;
            int yawLineIndexEnd = yawLineIndexStart + 9;

            int lenString = testString1.length();
            strBuilder.append(testString1);

            if ((rollLineIndexEnd) > lenString) {
                rollLineIndexEnd = lenString;
            }

            if ((pitchLineIndexEnd) > lenString) {
                pitchLineIndexEnd = lenString;
            }

            if ((yawLineIndexEnd) > lenString) {
                yawLineIndexEnd = lenString;
            }

            String rollSubStr = strBuilder.substring(rollLineIndexStart, rollLineIndexEnd);
            String pitchSubStr = strBuilder.substring(pitchLineIndexStart, pitchLineIndexEnd);
            String yawSubStr = strBuilder.substring(yawLineIndexStart, strBuilder.length());

            int endOfLineIndexRoll = rollSubStr.indexOf("e");
            if (endOfLineIndexRoll > 0) {
                rollSubStrFinal = strBuilder.substring(rollLineIndexStart, endOfLineIndexRoll + rollLineIndexStart);
            }

            int endOfLineIndexPitch = pitchSubStr.indexOf("e");
            if (endOfLineIndexPitch > 0) {
                pitchSubStrFinal = strBuilder.substring(pitchLineIndexStart, endOfLineIndexPitch + pitchLineIndexStart);
            }

            int endOfLineIndexYaw = yawSubStr.indexOf("e");
            if (endOfLineIndexYaw > 0) {
                yawSubStrFinal = strBuilder.substring(yawLineIndexStart, endOfLineIndexYaw + yawLineIndexStart);
            }


            if (rollSubStrFinal != null) {
                String rollValStr = rollSubStrFinal.substring(5, rollSubStrFinal.length());
                rollValStr = rollValStr.replace(" ", "");
                rollVal = Integer.valueOf(rollValStr);
                //System.out.println("Roll: " + rollVal);
                setRollValue(rollValStr);
                rollB=true;
            }

            if (pitchSubStrFinal != null) {
                String pitchValStr = pitchSubStrFinal.substring(6, pitchSubStrFinal.length());
                pitchValStr = pitchValStr.replace(" ", "");
                pitchVal = Integer.valueOf(pitchValStr);
               // System.out.println("Pitch: " + pitchVal);
                pitchB=true;
            }

            if (yawSubStrFinal != null) {
                String yawValStr = yawSubStrFinal.substring(4, yawSubStrFinal.length());
                yawValStr = yawValStr.replace(" ", "");
                yawVal = Integer.valueOf(yawValStr);
               // System.out.println("Yaw: " + yawVal);
                yawB=true;
            }

             if(rollB&&pitchB&&yawB){
                xyzdb.insertXYZdata(rollVal,pitchVal,yawVal);
             }

            strBuilder.delete(0, strBuilder.length());
        }
    }



    public  void setupBarBottomLayout(){
        Fragment fragment;


        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)) {
            updateBottomBarMenuLayout();

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    // Begin the transaction

                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    switch (tabId) {
                        case R.id.tab_home:
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_container, new HomeFragment());
                            ft.commit();
                            break;
                        case R.id.tab_home_debug:
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_container, new HomeFragment());
                            ft.commit();
                            break;

                        case R.id.tab_graph:
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_container, new GraphFragment());
                            ft.commit();
                            break;
                        case R.id.tab_graph_debug:
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_container, new GraphFragment());
                            ft.commit();
                            break;

                        case R.id.tab_parameters:
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_container, new ControlFragment());
                            ft.commit();
                            break;
                        case R.id.tab_parameters_debug:
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_container, new ControlFragment());
                            ft.commit();
                            break;

                        case R.id.tab_debug:
                            ft.replace(R.id.fragment_container, new DebugFragment());
                            ft.commit();
                            break;

                        case R.id.tab_settings:
                            // Replace the contents of the container with the new fragment
                            // Display the fragment as the main content.
                            ft.replace(R.id.fragment_container, new SettingsFragment());
                            ft.commit();
                            break;
                        case R.id.tab_settings_debug:
                            // Replace the contents of the container with the new fragment
                            // Display the fragment as the main content.
                            ft.replace(R.id.fragment_container, new SettingsFragment());
                            ft.commit();
                            break;

                        default:
                            // Replace the contents of the container with the new fragment
                            //  ft.replace(R.id.fragment_container, new HomeFragment());
                            //  ft.commit();
                            //  break;
                    }
                }
            });

        } else {
            if(bottomBar!=null) {
                bottomBar.setVisibility(View.GONE);
            }
            fragment = new LoginFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,fragment);
            ft.commit();
        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Upload the databases
                    BasicSettings.exportDatabase(getApplicationContext());
                }


            default:
                System.out.println("Not correct CAAAASE");
                // other 'case' lines to check for other
                // permissions this app might request
        }

    }

    public void updateBottomBarMenuLayout(){
        int debugMode=0;
        String debugStr[]=settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE);
        if(debugStr!=null) {
            try {
                debugMode = Integer.valueOf(debugStr[3]);
            } catch (Exception e) {
                debugMode=0;
            }
        }

        // Update layout according to the debug mode
        switch(debugMode){
            case Constants.DEBUG_MODE:
                try {
                    bottomBar = new BottomBar(getApplicationContext(), null);
                    bottomBar = (BottomBar) findViewById(R.id.bottomBar_debug);

                    // Check if the bottom bar already is created, otherwise just turn on the visibility
                    if (bottomBar.getTabCount() == 0) {
                        bottomBar.setItems(R.xml.bottom_menu_debug);
                        bottomBar.setVisibility(View.VISIBLE);
                        bottomBar.selectTabAtPosition(0);
                    } else {
                        bottomBar.setVisibility(View.VISIBLE);
                        bottomBar.selectTabAtPosition(0);
                    }
                } catch (Exception e) {
                  Toast.makeText(getApplicationContext(),"Problem while setting up bottombar",Toast.LENGTH_LONG).show();
                }
                break;
            case Constants.USER_MODE:
                try {
                    bottomBar = new BottomBar(getApplicationContext(), null);
                    bottomBar = (BottomBar) findViewById(R.id.bottomBar);

                    // Check if the bottom bar already is created, otherwise just turn on the visibility
                    if (bottomBar.getTabCount() == 0) {
                        bottomBar.setItems(R.xml.bottom_menu);
                        bottomBar.setVisibility(View.VISIBLE);
                        bottomBar.selectTabAtPosition(0);
                    } else {
                        bottomBar.setVisibility(View.VISIBLE);
                        bottomBar.selectTabAtPosition(0);
                    }
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(),"Problem while setting up bottombar",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                /*
                bottomBar =  new BottomBar(getApplicationContext(),null);
                bottomBar= (BottomBar) findViewById(R.id.bottomBar);
                bottomBar.setItems(R.xml.bottom_menu);
                bottomBar.setVisibility(View.VISIBLE);
                */
        }

         //Set the gravity correctly
        for (int i = 0; i < bottomBar.getTabCount(); i++) {
            bottomBar.getTabAtPosition(i).setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    public void setBottomBarMenuLayoutMode(int layoutMode) {

        // Store the layout mode inside shared preferences_basic
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("LayoutMode", layoutMode);
        editor.apply();

        // Restart the application to enable the change to take effect
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        //setupBarBottomLayout();
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {

                    if (mMessageService == null) {
                        setupMessageService();
                        mMessageService.start();
                    } else {
                        mMessageService.start();
                    }
                    Toast.makeText(getApplicationContext(), "Device found and are about to connect (SECURE)",
                            Toast.LENGTH_SHORT).show();
                    connectDevice(data, false);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    if (mMessageService == null) {
                        setupMessageService();
                        mMessageService.start();
                    } else {
                        mMessageService.start();
                    }
                    //Toast.makeText(getActivity(), "Device found and are about to connect (INSECURE)",
                    //        Toast.LENGTH_SHORT).show();
                    setupMessageService();
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    Toast.makeText(getApplicationContext(), "Bluetooth enabled",
                            Toast.LENGTH_SHORT).show();
                    //Proceed with list the available devices
                    setupListDevices();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();

                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mMessageService.connect(device, secure);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupMessageService() {
        Log.d(TAG, "setupMessageService()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mMessageService = new BluetoothMessageService(getApplicationContext(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * The Handler that gets information back from the BluetoothMessageService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothMessageService.STATE_CONNECTED:
                            setBluetoothState(1);
                            HomeFragment.bltStatus.setImageResource(R.drawable.online);
                            deviceConnected=true;
                            // setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //  mConversationArrayAdapter.clear();
                            break;
                        case BluetoothMessageService.STATE_CONNECTING:
                            setBluetoothState(2);
                            HomeFragment.bltStatus.setImageResource(R.drawable.pending);
                            deviceConnected=false;
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothMessageService.STATE_LISTEN:
                            HomeFragment.bltStatus.setImageResource(R.drawable.offline);
                            deviceConnected=false;
                            break;
                        case BluetoothMessageService.STATE_NONE:
                            HomeFragment.bltStatus.setImageResource(R.drawable.offline);
                            deviceConnected=false;
                             break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                   // byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                   // String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    //byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    MessageStruct message = (MessageStruct) msg.obj;

                    if(message.getSTATUS()==Constants.MESSAGE_OK) {

                        switch (message.getCMD()) {

                            case Constants.COMMAND_ACK:
                                //Toast.makeText(getApplicationContext(),"ACK received",Toast.LENGTH_LONG).show();
                                /*
                                int[] voltData = message.getDATA();
                                byte[] voltBytes = toBytes(voltData);
                                float volt = ByteBuffer.wrap(voltBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                String v = String.valueOf(volt);
                                String cutString = v.substring(0, 5);
                                Toast.makeText(getApplicationContext(),"battery: "+cutString,Toast.LENGTH_LONG).show();
                                */
                                //setBatteryVoltage(cutString);
                                //emgdb.insertEMGdata(cutString,cutString,cutString,cutString,cutString,cutString,cutString,cutString);

                                break;

                            case Constants.NEW_FEATURES:
                                //Check if the feature switch is turned on, if not, save the switch state to true
                                String extractFeaturesState [] = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH);
                                if(extractFeaturesState!=null){
                                    if(!Boolean.parseBoolean(extractFeaturesState[3])){
                                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH,"true");
                                    }
                                }
                                // Continue and parse the data received
                                parseAndSaveNewFeatures(message);
                                break;
                            case Constants.STATUS_CHECK:
                                parseAndSaveStatusData(message);
                                break;

                            case Constants.STREAMING_IMU_DATA:
                                parseAndSaveImuData(message);
                        }

                    } else {
                        switch (message.getSTATUS()){
                            case Constants.WRONG_CHECKSUM:
                             //   Toast.makeText(getApplicationContext(),"Wrong checksum",Toast.LENGTH_LONG).show();
                                System.out.println("Wrong checksum");
                                break;

                            case Constants.WRONG_CMD:
                             //   Toast.makeText(getApplicationContext(),"Wrong CMD",Toast.LENGTH_LONG).show();
                                System.out.println("Wrong CMD");
                                break;

                            case Constants.WRONG_CMD_STRUCTURE:
                             //   Toast.makeText(getApplicationContext(),"Wrong CMD structure",Toast.LENGTH_LONG).show();
                                System.out.println("Wrong CMD Structure");
                                break;
                        }
                    }


                    //parseMessageFunction(readMessage);
                   /*
                    int endOfLineIndex = readMessage.indexOf("e");
                    int startOfLineIndex = readMessage.indexOf("s");
                    int lenString=readMessage.length();

                    recDataString.append(readMessage);

                    if((startOfLineIndex-endOfLineIndex) == 1){
                        String dataInPrint = recDataString.substring(startOfLineIndex,lenString-1);
                        endOfLineIndex = dataInPrint.indexOf("e");
                        recDataString.delete(0, readMessage.length());
                        recDataString.append(dataInPrint);
                    }

                    if (endOfLineIndex > 0) {
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        if (dataInPrint.charAt(0) == 's')                                //if it starts with # we know it is what we are looking for
                        {
                            String message = recDataString.substring(1, endOfLineIndex);//get sensor value from string between indices 1-5
                            //System.out.println(message);
                            String tmpStr=message;
                            tmpStr=tmpStr.replace("Roll: ","");
                            emgdb.insertEMGdata(Integer.valueOf(tmpStr),0);
                            setRollValue(message);
                        }
                        //clear all string data
                    }
                    // String readMessage = new String(readBuf, 0, msg.arg1);
                    //System.out.println("MessageStruct from server: " + readMessage);
                    recDataString.delete(0,recDataString.length());
                    */
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    // mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    // if (null != activity) {
                    //     Toast.makeText(activity, "Connected to "
                    //             + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //  }
                    break;
                case Constants.MESSAGE_TOAST:
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        if(mMessageService != null) {
            // Check that we're actually connected before trying anything
            if (mMessageService.getState() != BluetoothMessageService.STATE_CONNECTED) {
                Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] send;
            byte[] sendn;


            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
                try {
                    send = message.getBytes("UTF-8");
                    sendn = message.getBytes();
                     //mMessageService.write(send);
                    ack=false;
                    while (!ack) {
                        //lastCommandSent=message.get
                        mMessageService.writeString(message);
                        SystemClock.sleep(500);
                    }
                }catch (Exception e){

                }
/*
                int mL=message.length();
                int sendArr[] = new int[mL];
                for (int j=0;j<mL;j++){
                    sendArr[j]=(int)message.charAt(j);
                }


                    mMessageService.writeIntArr(sendArr);

*/
                // Reset out string buffer to zero and clear the edit text field
                mOutStringBuffer.setLength(0);
            }
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.not_connected),Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(MessageStruct message) {
        //Check if there is a pending sending message going on, if so ignore the send action.
        if(!pendingMessage) {
            pendingMessage=true;
            if (mMessageService != null) {
                // Check that we're actually connected before trying anything
                if (mMessageService.getState() != BluetoothMessageService.STATE_CONNECTED) {
                    Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
                    return;
                }

                String messageStr = message.getMessageAsString();

                // Check that there's actually something to send
                if (messageStr.length() > 0) {
                    try {
                        //Set the maximum number of tries to send the message. 30*100ms delay = 3s
                        int nMaxTries=3;

                        //Track the number of tries
                        int nTries=0;

                        //Set last command sent, so that we can compare the response received
                        lastCommandSent = message.getCMD();

                        //Initiate the acknowledge bit to false
                        ack = false;

                        while (!ack && nTries<nMaxTries) {

                            //Write the message out on the bluetooth
                            mMessageService.writeString(messageStr);

                            //Wait some time in order for the response to arrive
                            SystemClock.sleep(200);

                            //Increment the step
                            nTries++;
                        }
                    } catch (Exception e) {
                        mOutStringBuffer.setLength(0);
                    }
                    if(!ack){
                        Toast.makeText(getApplicationContext(),"Message delivery failure",Toast.LENGTH_SHORT).show();
                    } else {
                        if(lastCommandSent==Constants.SET_COMMAND_MODE){
                           settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH,"true");
                            Toast.makeText(getApplicationContext(),"Command Mode Successfully set",Toast.LENGTH_SHORT).show();
                        }
                        if(lastCommandSent==Constants.SET_CONTROL_MODE){
                            settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_COMMAND_MODE_SWTICH,"false");
                            Toast.makeText(getApplicationContext(),"Control Mode Successfully set",Toast.LENGTH_SHORT).show();
                        }
                        if(lastCommandSent==Constants.UPDATE_MOVEMENT){
                            Toast.makeText(getApplicationContext(),"Movement Updated Successfully",Toast.LENGTH_SHORT).show();
                        }
                        if(lastCommandSent==Constants.START_FEATURES_ACQ){
                            settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH,"true");
                            Toast.makeText(getApplicationContext(),"EMG Feature Acquisition Successfully Started",Toast.LENGTH_SHORT).show();
                        }

                        if(lastCommandSent==Constants.STATUS_CHECK){
                            Toast.makeText(getApplicationContext(),"Status Check Successfully Received",Toast.LENGTH_SHORT).show();
                        }
                        if(lastCommandSent==Constants.STREAMING_IMU_DATA){
                            settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_ORIENTATION_SWITCH,"true");
                            Toast.makeText(getApplicationContext(),"IMU Acquisition Successfully Started",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                pendingMessage=false;
                }
            }else {
            Toast.makeText(getApplicationContext(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    public void parseAndSaveImuData(MessageStruct msg) {
        int[] bytes = msg.getDATA();
        byte [] byteData=toBytes(bytes);
        int nBytes = msg.getN_BYTES();

        int sizeOfInt16= 2;

        if(nBytes==12) {
            byte[] accXBytes = new byte[]{byteData[0], byteData[1]};
            byte[] accYBytes = new byte[]{byteData[2], byteData[3]};
            byte[] accZBytes = new byte[]{byteData[4], byteData[5]};
            byte[] gyrXBytes = new byte[]{byteData[6], byteData[7]};
            byte[] gyrYBytes = new byte[]{byteData[8], byteData[9]};
            byte[] gyrZBytes = new byte[]{byteData[10], byteData[11]};

            short accX = ByteBuffer.wrap(accXBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
            short accY = ByteBuffer.wrap(accYBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
            short accZ = ByteBuffer.wrap(accZBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
            short gyrX = ByteBuffer.wrap(gyrXBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
            short gyrY = ByteBuffer.wrap(gyrYBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
            short gyrZ = ByteBuffer.wrap(gyrZBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();

            /*
            short accX = (short)(((accXBytes[0] & 0xFF) << 8) | (accXBytes[1] & 0xFF));
            short accY = (short)(((accYBytes[0] & 0xFF) << 8) | (accYBytes[1] & 0xFF));
            short accZ = (short)(((accZBytes[0] & 0xFF) << 8) | (accZBytes[1] & 0xFF));
            short gyrX = (short)(((gyrXBytes[0] & 0xFF) << 8) | (gyrXBytes[1] & 0xFF));
            short gyrY = (short)(((gyrYBytes[0] & 0xFF) << 8) | (gyrYBytes[1] & 0xFF));
            short gyrZ = (short)(((gyrZBytes[0] & 0xFF) << 8) | (gyrZBytes[1] & 0xFF));
            */

            float accXf=accX;
            float accYf=accY;
            float accZf=accZ;
            float gyrXf=gyrX;
            float gyrYf=gyrY;
            float gyrZf=gyrZ;

            xyzdb.insertXYZdata((int)accXf,(int)accYf,(int)accZf);

        }


    }

    public void parseAndSaveNewFeatures(MessageStruct msg){
        int sizeOfFloat = 4;
        int[] bytes = msg.getDATA();
        int nBytes = msg.getN_BYTES();
        int nFeatures = nBytes/sizeOfFloat;
        int [] intArrFeatures = new int[sizeOfFloat];
        float [] featureVal = new float[nFeatures];
        float maxMvcCh1=0f;
        float maxMvcCh2=0f;
        float maxMvcCh3=0f;
        float maxMvcCh4=0f;

        for (int i=0;i<nFeatures;i++) {
            for (int j = 0; j< sizeOfFloat;j++){
                intArrFeatures[j]=bytes[i*sizeOfFloat+j];
            }
            byte[] byteArrFeatures = toBytes(intArrFeatures);
            featureVal[i] = ByteBuffer.wrap(byteArrFeatures).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        // Get the latest MVC
        String ch1Str[]=settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[0]);
        String ch2Str[]=settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[1]);
        String ch3Str[]=settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[2]);
        String ch4Str[]=settingsdb.getLatestSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[3]);

        // Check if there is data
        if(ch1Str!=null) maxMvcCh1=Float.valueOf(ch1Str[3]);
        if(ch2Str!=null) maxMvcCh2=Float.valueOf(ch2Str[3]);
        if(ch3Str!=null) maxMvcCh3=Float.valueOf(ch3Str[3]);
        if(ch3Str!=null) maxMvcCh4=Float.valueOf(ch4Str[3]);


        // To map the featurevalues in between 0-100, use linear mapping
        // [A, B] --> [a, b]
        // current_val*100/(feature_max_val)

        // Compare the actual feature-values of tmabs to the maximum values.
        if(featureVal[0]>maxMvcCh1){
            if(!settingsdb.updateSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[0],String.valueOf(featureVal[0])))
            settingsdb.insertSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[0],String.valueOf(featureVal[0]));
        }

        if(featureVal[1]>maxMvcCh2){
            if(!settingsdb.updateSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[1],String.valueOf(featureVal[1])))
            settingsdb.insertSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[1],String.valueOf(featureVal[1]));
        }

        if(featureVal[2]>maxMvcCh3){
            if(!settingsdb.updateSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[2],String.valueOf(featureVal[2])))
                settingsdb.insertSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[2],String.valueOf(featureVal[2]));
        }

        if(featureVal[3]>maxMvcCh4){
            if(!settingsdb.updateSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[3],String.valueOf(featureVal[3])))
                settingsdb.insertSetting(SettingsDbHelper.PROGRESS_MAX_VALUE_STRING[3],String.valueOf(featureVal[3]));
        }

        //Insert the values into the database
        emgdb.insertEMGdata(String.valueOf(featureVal[0]),String.valueOf(featureVal[1]),String.valueOf(featureVal[2]),String.valueOf(featureVal[3]),"0.0","0.0","0.0","0.0");

    }

   public void parseAndSaveStatusData(MessageStruct msg){

       int[] bytes = msg.getDATA();
       int[] voltData = new int[4];
       int[] temperatureData = new int[4];
       int sensorhand;
       int neurostimulator;
       int enableNS1;
       int sdCard;
       int inemo;
       int controlmode;

        //Extract the voltage
       for (int i=0; i<voltData.length; i++){
           voltData[i]=bytes[i];
       }
       byte[] voltBytes = toBytes(voltData);
       float volt = ByteBuffer.wrap(voltBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
       String voltStr = String.valueOf(volt);
       String voltFinalStr;
       if(voltStr.length()>6)  voltFinalStr = voltStr.substring(0, 5);
        else voltFinalStr=voltStr;

       //Extract the temperature
       for (int j=4;j<4+temperatureData.length;j++){
           temperatureData[j-4]=bytes[j];
       }
       byte[] temperatureBytes = toBytes(temperatureData);
       float temperature = ByteBuffer.wrap(temperatureBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
       String temperatureStr = String.valueOf(temperature);
       String temperatureFinalStr;
       if (temperatureStr.length()>6) temperatureFinalStr = temperatureStr.substring(0, 5);
       else temperatureFinalStr=temperatureStr;

       int offset = temperatureData.length + voltData.length;
       sensorhand = bytes[offset];
       enableNS1 = bytes[offset+1];
       sdCard = bytes[offset+2];
       inemo = bytes[offset+3];
       controlmode = bytes[offset+4];

       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_BATTERY_VOLTAGE,voltFinalStr);
       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_TEMPERATURE,temperatureFinalStr);
       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_SENSOR_HAND,String.valueOf(sensorhand));
       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_ENABLENS,String.valueOf(enableNS1));
       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_SDCARD,String.valueOf(sdCard));
       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_INEMO,String.valueOf(inemo));
       settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_CTRLMODE,String.valueOf(controlmode));


   }

    public void setupListDevices(){
        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app fragment_settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    byte[] toBytes(int[] data)
    {
        byte[] result = new byte[data.length];
        for(int i=0; i< data.length;i++){
            result[i]=(byte)(data[i]);
        }
        return result;
    }

    @Override
    public int initMain() {
        setupBarBottomLayout();
        return 0;
    }

}
