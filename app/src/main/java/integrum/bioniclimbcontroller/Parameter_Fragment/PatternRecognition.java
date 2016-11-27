package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.DcDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-11-08.
 */
public class PatternRecognition extends Fragment {
    RecyclerView rv;
    LinearLayoutManager llm;
    SelectControlMethodListener mCallback;
    SettingsDbHelper settingsDbHelper;
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
        final View view = inflater.inflate(R.layout.fragment_pattern_recognition, container, false);
        rv = (RecyclerView) view.findViewById(R.id.rvPatRecInfo);
        settingsDbHelper = new SettingsDbHelper(getActivity());
        //rv.setVisibility(View.GONE);
        rv.setNestedScrollingEnabled(false);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        InfoAdapter adapter = new InfoAdapter();
        rv.setAdapter(adapter);

        return view;
    }


    public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
        class ViewHolder extends RecyclerView.ViewHolder {
           Button reCalibrate;
            public ViewHolder(View itemView) {
                super(itemView);
                reCalibrate = (Button) itemView.findViewById(R.id.reclibrateBtn);
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pattern_rec_info,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.reCalibrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    settingsDbHelper.updateSetting(SettingsDbHelper.SETTINGS_TYPE_PATTERN_REC_CALIBRATED,"false");
                    mCallback.changeControlMethod(Constants.PATTERN_RECOGNITION);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

}


