package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

import integrum.bioniclimbcontroller.Calibration.CalibrationInstructionsNew;
import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.DcDbHelper;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-11-08.
 */
public class SelectMovementsPatternRec extends Fragment {

    private int movementSelectionRequest=1;
    private SettingsDbHelper settingsDbHelper;

    int[] movementImage = new int[]{R.drawable.open_hand,
            R.drawable.close_hand,
            R.drawable.flex_hand,
            R.drawable.extend_hand,
            R.drawable.pronation,
            R.drawable.supination,
            R.drawable.side_grip,
            R.drawable.fine_grip,
            R.drawable.agree,
            R.drawable.pointer,
            R.drawable.no_image,
            R.drawable.thumb_flex,
            R.drawable.thumb_abduc,
            R.drawable.thumb_adduc,
            R.drawable.flex_elbow,
            R.drawable.extend_elbow,
            R.drawable.no_image,
            R.drawable.no_image,
            R.drawable.no_image,
            R.drawable.no_image,
            R.drawable.no_image,
            R.drawable.no_image,
            R.drawable.no_image,
            R.drawable.no_image};

    String [] movementNames = new String[] {"Open Hand",
            "Close Hand",
            "Flex Hand",
            "Extend hand",
            "Pronation",
            "Supination",
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
    public SelectControlMethodListener mCallback;

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
        View v = inflater.inflate(R.layout.fragment_select_movements_pattern_rec, container, false);
        settingsDbHelper = new SettingsDbHelper(getActivity());
        ArrayList<SelectMovement> movList = initializeData();

        final CustomCheckBoxAdapter checkAdapter = new CustomCheckBoxAdapter(getActivity(),
                R.layout.movement_list_item_pattern_rec, movList);
        ListView listView = (ListView) v.findViewById(R.id.listViewMovements);

        // Assign adapter to ListView
        listView.setAdapter(checkAdapter);

        Button myButton = (Button) v.findViewById(R.id.buttonNext);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // StringBuffer responseText = new StringBuffer();
               // responseText.append("The following were selected...\n");

                ArrayList<SelectMovement> moveList = checkAdapter.movementList;
                ArrayList<Integer> idx = new ArrayList<Integer>();
                for (int i = 0; i < moveList.size(); i++) {
                    SelectMovement move = moveList.get(i);
                    if (move.isSelected()) {
                        //responseText.append("\n" + move.getName());
                        idx.add(i);
                    }
                }
                int idxSize=idx.size();
                int[] arrIdx=new int[idxSize];
                for(int j=0;j<idxSize;j++){
                    arrIdx[j]=idx.get(j);
                }

                Intent i = new Intent(getActivity(), CalibrationInstructionsNew.class);
                i.putExtra("videoIndex", arrIdx);
                startActivityForResult(i,movementSelectionRequest);
               // Toast.makeText(getActivity(),
               //         responseText, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    public int generateRandomData(int n1, int n2){
        Random r = new Random();
        return r.nextInt(n2 - n1) + n1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK) {
            if(requestCode==movementSelectionRequest){
                Bundle mBundle = data.getExtras();
                int[] movementId = mBundle.getIntArray("movementIdx");
                for(int i=0;i<movementId.length;i++){
                    int thresVal=generateRandomData(0,100);
                    //dcDbHelper.insertData(movementNames[movementId[i]],String.valueOf(i),String.valueOf(thresVal),String.valueOf(thresVal),"true");
                }
                settingsDbHelper.insertSetting(SettingsDbHelper.SETTINGS_TYPE_PATTERN_REC_CALIBRATED,"true");
                //Change control method
                mCallback.changeControlMethod(Constants.PATTERN_RECOGNITION_LIST);
            }
        }

    }

    public  ArrayList<SelectMovement> initializeData() {
        ArrayList<SelectMovement> movementList = new ArrayList<SelectMovement>();
        for (int i=0;i<movementImage.length;i++) {
            movementList.add(new SelectMovement(movementNames[i],movementImage[i],false));
        }
        return movementList;
    }


    public class SelectMovement {

        private String name;
        private int img;
        boolean selected = false;

        public SelectMovement( String name, int img, boolean selected ) {
            this.name=name;
            this.img=img;
            this.selected=selected;
        }



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private class CustomCheckBoxAdapter extends ArrayAdapter<SelectMovement> {

        private ArrayList<SelectMovement> movementList;

        public CustomCheckBoxAdapter(Context context, int textViewResourceId,
                               ArrayList<SelectMovement> movementList) {
            super(context, textViewResourceId, movementList);
            this.movementList = new ArrayList<SelectMovement>();
            this.movementList.addAll(movementList);
        }

        private class ViewHolder {
            CheckBox name;
            ImageView img;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.movement_list_item_pattern_rec, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.img = (ImageView) convertView.findViewById(R.id.movementImg);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        SelectMovement movement = (SelectMovement) cb.getTag();
                        movement.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            SelectMovement mov = movementList.get(position);
            holder.name.setText(mov.getName());
            holder.name.setChecked(mov.isSelected());
            holder.name.setTag(mov);
            holder.img.setImageResource(movementImage[position]);

            return convertView;

        }

    }
}


