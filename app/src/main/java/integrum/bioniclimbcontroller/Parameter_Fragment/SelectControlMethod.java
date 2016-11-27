package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-10-25.
 */
public class SelectControlMethod extends Fragment {

    public SelectControlMethodListener mCallback;
    private SettingsDbHelper settingsDbHelper;
    private int mode=1;
    public interface SelectControlMethodListener{
        public void changeControlMethod(int mode);
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
            if (getParentFragment() instanceof SelectControlMethodListener) {
                mCallback = (SelectControlMethodListener) getParentFragment();
            } else {
                throw new RuntimeException("The parent fragment must implement SelectControlMethodListener");
            }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_method,container, false);
        settingsDbHelper = new SettingsDbHelper(getActivity());
        boolean patRecCalibrated=false;
        String[] calibrated = settingsDbHelper.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_PATTERN_REC_CALIBRATED);
        if(calibrated!=null){
            patRecCalibrated = Boolean.parseBoolean(calibrated[3]);
        }

        RadioGroup rGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        Button next = (Button) view.findViewById(R.id.buttonControlNext);
        final boolean finalPatRecCalibrated = patRecCalibrated;
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId==R.id.radioBtnDirectCtrl){
                    mode= Constants.DIRECT_CONTROL;
                }
                else if(checkedId==R.id.radioBtnPattern){
                    if(finalPatRecCalibrated){
                        mode=Constants.PATTERN_RECOGNITION_LIST;
                    } else {
                        mode = Constants.PATTERN_RECOGNITION;
                    }
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.changeControlMethod(mode);
            }
        });

        return view;
    }
}
