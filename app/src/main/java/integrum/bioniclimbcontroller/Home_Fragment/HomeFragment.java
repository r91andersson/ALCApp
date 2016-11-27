package integrum.bioniclimbcontroller.Home_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import integrum.bioniclimbcontroller.Bluetooth.BluetoothMessageService;
import integrum.bioniclimbcontroller.Bluetooth.CommunicationProtocol;
import integrum.bioniclimbcontroller.Bluetooth.MessageStruct;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.EmgDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-09-26.
 */
public class HomeFragment extends Fragment {
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

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    // Layout views
    public TextView mConnectTxt;
    public static TextView rollValue;
    public static TextView batteryVoltage;
    public static ImageView bltStatus;
    LinearLayoutManager mLayoutManager;
    private static final String TAG = "HomeFragment";
    HomeFragmentListener mCallback;

    // Database for EMG data
    EmgDbHelper emgdb;

    // Database for Settings
    SettingsDbHelper settingsdb;

    // Container Activity must implement this interface
    public interface HomeFragmentListener {
        public void sendCommand(int command, String message);
        public void setBluetoothState(int state);
        public int getBluetoothState();
        public int getRollValue();
        public void setBottomBarMenuLayoutMode(int layoutM);
        public void setBatteryVoltage(String voltage);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (HomeFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HomeFragmentListener");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // Get database for EMG
        emgdb = new EmgDbHelper(getActivity());

        //Get database for fragment_settings
        settingsdb = new SettingsDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);


        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvHome);

        // specify an adapter (see also next example)
        MyAdapter mAdapter = new MyAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

    }




    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        ListView profile_list;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CardView cv;
            public ViewHolder(View v) {
                super(v);
                cv = (CardView) itemView.findViewById(R.id.cvHome_2);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter() {
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cardview, parent, false);
            profile_list = (ListView) v.findViewById(R.id.list_profiles);
            //Button debugBtn = (Button) v.findViewById(R.id.debugBtn);
            //Button userBtn = (Button) v.findViewById(R.id.userBtn);
            bltStatus = (ImageView) v.findViewById(R.id.bluetoothStatusImg);


            int state=mCallback.getBluetoothState();
            switch(state) {
                case 0:
                bltStatus.setImageResource(R.drawable.offline);
                    break;
                case 1:
                    bltStatus.setImageResource(R.drawable.online);
                    break;
                case 2:
                    bltStatus.setImageResource(R.drawable.pending);
                    break;
            }


            mConnectTxt = (TextView) v.findViewById(R.id.connectBluetooth);
            mConnectTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    // If the adapter is null, then Bluetooth is not supported
                    if (mBluetoothAdapter == null) {
                      //  FragmentActivity activity = getActivity();
                        Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
                     //   activity.finish();
                    }

                    // Check that the bluetooth is enabled,if not, prompt the user to enable this.
                    if (!mBluetoothAdapter.isEnabled()) {
                        mCallback.sendCommand(1, null);
                    } else {
                        // Bluetooth is on, proceed with list the current available devices
                        mCallback.sendCommand(2,null);
                        //setupListDevices();
                    }
                }
            });


            /*
            startSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.sendCommand(3, "X");
                }
            });


            stopSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.sendCommand(3, "Z");
                }
            });
*/
            List<String> profile_list_array = new ArrayList<String>();
            profile_list_array.add("User profile");
            profile_list_array.add("Grip profile");
            profile_list_array.add("EMG profile");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(v.getContext(), R.layout.custom_listitem_textview, profile_list_array );
            profile_list.setAdapter(arrayAdapter);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return 1;
        }
    }
}


