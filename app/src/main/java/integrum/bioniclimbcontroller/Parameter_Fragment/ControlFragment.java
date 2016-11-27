package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import integrum.bioniclimbcontroller.Bluetooth.CommunicationProtocol;
import integrum.bioniclimbcontroller.Bluetooth.MessageStruct;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Graph_Fragment.OverviewFragment;
import integrum.bioniclimbcontroller.MainActivity;
import integrum.bioniclimbcontroller.R;
import integrum.bioniclimbcontroller.ThreadUtils;

/**
 * Created by Robin on 2016-10-25.
 */
public class ControlFragment extends Fragment implements SelectControlMethod.SelectControlMethodListener, SelectMovementsPatternRec.SelectControlMethodListener, PatternRecognition.SelectControlMethodListener {
    FragmentTransaction ft;
    TabLayout tabControl;

    ControlFragmentListener mCallBackControl;

    public interface ControlFragmentListener{
        public void sendMessageControlFragment(MessageStruct msg);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBackControl = (ControlFragment.ControlFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ControlFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_control,container,false);

        //Send "SET_CONTROL_MODE" to embedded system
        /*
        if(MainActivity.deviceConnected){
            sendSetCommandModeRequest();
        } else {
            Toast.makeText(getActivity(),"Device not connected",Toast.LENGTH_SHORT).show();
        }
        */

        tabControl = (TabLayout) view.findViewById(R.id.tab_layout_control);
        tabControl.addTab(tabControl.newTab().setText(""));

        ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_control,new SelectControlMethod());
        ft.commit();

        return view;
    }

    @Override
    public void changeControlMethod(int mode) {
        ft = getChildFragmentManager().beginTransaction();

        switch (mode){
            case Constants.DIRECT_CONTROL:
                ft.replace(R.id.fragment_container_control, new ParameterFragmentV2());
                ft.commitAllowingStateLoss();
                tabControl.getTabAt(0).setText("Direct Control");
                break;
            case Constants.PATTERN_RECOGNITION:
                ft.replace(R.id.fragment_container_control, new SelectMovementsPatternRec());
                ft.commitAllowingStateLoss();
                tabControl.getTabAt(0).setText("Pattern recognition");
                break;
            case Constants.PATTERN_RECOGNITION_LIST:
                ft.replace(R.id.fragment_container_control, new PatternRecognition());
                ft.commitAllowingStateLoss();
                tabControl.getTabAt(0).setText("Pattern recognition");
                break;
        }

    }

    public void onDestroyView() {
        super.onDestroyView();
        //Terminate direct control thread
        terminateDcThread();

        //Send "SET_CONTROL_MODE" request
        //sendSetControlModeRequest();
    }

    public void terminateDcThread(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String threadNameEMG = sharedPrefs.getString("DirectControlThread", null);
        ThreadUtils utilsThread = new ThreadUtils();
        Thread currentDc=utilsThread.getThreadByName(threadNameEMG);
        if(currentDc!=null) {
            while (currentDc.isAlive()) {
                currentDc.interrupt();
            }
        }
    }

    public void sendSetControlModeRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.SET_CONTROL_MODE,new int[]{1});
        mCallBackControl.sendMessageControlFragment(msg);
    }

    public void sendSetCommandModeRequest(){
        CommunicationProtocol com = new CommunicationProtocol();
        MessageStruct msg = com.createMessage(Constants.SET_COMMAND_MODE,new int[]{1});
        mCallBackControl.sendMessageControlFragment(msg);
    }
}
