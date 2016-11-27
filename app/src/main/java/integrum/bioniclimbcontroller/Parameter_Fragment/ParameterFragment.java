package integrum.bioniclimbcontroller.Parameter_Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.R;


/**
 * Created by Robin on 2016-09-28.
 */
public class ParameterFragment extends Fragment {
    /**
     * Created by Robin on 2016-09-01.
     */


        SettingsDbHelper settingsdb;

        private static int mExpandedPosition = -1;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_parameter, container, false);

            RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
            // rv.setHasFixedSize(true);

            initializeData();
            RVAdapter adapter = new RVAdapter(persons);

            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));


            // Initialize fragment_settings database
            settingsdb = new SettingsDbHelper(getActivity());

            return view;
        }

        class Person {
            String name;
            String age;
            int photoId;
            int threshold;
            int gain;
            int seekThreshold;
            int seekGain;

            Person(String name, String age, int photoId, int threshold, int gain) {
                this.name = name;
                this.age = age;
                this.photoId = photoId;
                this.threshold = threshold;
                this.seekThreshold = threshold;
                this.seekGain = gain;
                this.gain = gain;
            }

            public void setThreshold(int threshold) {
                this.threshold = threshold;
            }

            public int getThreshold() {
                return threshold;
            }

            public void setSeekThreshold(int tmpThreshold) {
                this.seekThreshold = tmpThreshold;
            }

            public int getSeekThreshold() {
                return seekThreshold;
            }

            public void setSeekGain(int seekGain) {
                this.seekGain = seekGain;
            }

            public int getSeekGain() {
                return seekGain;
            }

            public void setGain(int gain) {
                this.gain = gain;
            }

            public int getGain() {
                return gain;
            }
        }

        private List<Person> persons;

        // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
        private void initializeData() {
            persons = new ArrayList<>();
            persons.add(new Person("Channel.1", "115", R.drawable.threshold,10,1));
            persons.add(new Person("Channel.2", "242", R.drawable.threshold,20,1));
            persons.add(new Person("Channel.3", "121", R.drawable.threshold,30,1));
            persons.add(new Person("Channel.4", "167", R.drawable.threshold,40,1));
            persons.add(new Person("Channel.5", "178", R.drawable.threshold,50,1));
            persons.add(new Person("Channel.6", "378", R.drawable.threshold,60,1));
        }

        public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

            List<Person> persons;
            int currentPressedCard;
            RVAdapter(List<Person> persons) {
                this.persons = persons;
            }

            @Override
            public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parameter_cardview, viewGroup, false);
                PersonViewHolder pvh = new PersonViewHolder(v);

                return pvh;
            }

            @Override
            public void onBindViewHolder(final PersonViewHolder personViewHolder, final int i) {
                personViewHolder.personName.setText(persons.get(i).name);
                personViewHolder.gain.setText("Gain: " + persons.get(i).gain);
                String[] th_ch_1 = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1);
                if(th_ch_1!=null) {
                    personViewHolder.personAge.setText("Threshold: " + th_ch_1[3]);
                } else {
                     personViewHolder.personAge.setText("Threshold: " + String.valueOf(persons.get(i).getThreshold()));
                }
                personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
                 if(th_ch_1!=null) {
                     personViewHolder.seekBarThreshold.setProgress(Integer.valueOf(th_ch_1[3]));
                 } else {
                     personViewHolder.seekBarThreshold.setProgress(persons.get(i).getSeekThreshold());
                 }
                personViewHolder.seekBarGain.setProgress(persons.get(i).getGain());
                final boolean isExpanded = i == mExpandedPosition;
                personViewHolder.details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                personViewHolder.itemView.setActivated(isExpanded);
                personViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpandedPosition = isExpanded ? -1 : i;
                        currentPressedCard = i;
                        int threshold=10+(i*10);
                        // In case the parameter card and its parameters were adjusted but values were not "set" -> reset the seekbar values to the threshold values
                        // and print the correct numbers above seekbars
                        String[] th_ch_1 = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1);
                        if(th_ch_1!=null) {
                            threshold = Integer.valueOf(th_ch_1[3]);
                        }
                        int gain = persons.get(i).getGain();
                        persons.get(i).setSeekThreshold(threshold);
                        persons.get(i).setSeekGain(gain);
                        personViewHolder.seekBarValueThreshold.setText("Threshold: " + String.valueOf(threshold));
                        personViewHolder.seekBarGainValue.setText("Gain: " + String.valueOf(gain));
                        //TransitionManager.beginDelayedTransition(personViewHolder);
                        notifyDataSetChanged();
                    }
                });


                    personViewHolder.seekBarThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                            if (isExpanded && currentPressedCard == i && b) {
                                persons.get(i).setSeekThreshold(progress);
                                int threshold = persons.get(i).getSeekThreshold();
                                personViewHolder.seekBarValueThreshold.setText("Threshold: " + String.valueOf(threshold));
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });

                personViewHolder.seekBarGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        if (isExpanded && currentPressedCard == i && b) {
                            persons.get(i).setSeekGain(progress);
                            int gain = persons.get(i).getSeekGain();
                            personViewHolder.seekBarGainValue.setText("Gain: " + String.valueOf(gain));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                personViewHolder.setBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int threshold = persons.get(i).getSeekThreshold();
                        int gain = persons.get(i).getSeekGain();
                        persons.get(i).setSeekThreshold(threshold);
                        persons.get(i).setThreshold(threshold);
                        persons.get(i).setSeekGain(gain);
                        persons.get(i).setGain(gain);

                        personViewHolder.gain.setText("Gain: " + gain);
                        personViewHolder.personAge.setText("Threshold: " + threshold);

                        settingsdb.insertSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1, threshold);
                    }
                });

                personViewHolder.defaultBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       String[] th_ch_1 = settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1);
                        if(th_ch_1!=null) {
                            System.out.println("Threshold for channel 1: " + Integer.valueOf(th_ch_1[3]));
                        }
                    }
                });

            }

            @Override
            public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
            }

            @Override
            public int getItemCount() {
                return 6;
            }


            public class PersonViewHolder extends RecyclerView.ViewHolder {
                CardView cv;
                TextView personName;
                TextView personAge;
                TextView gain;
                ImageView personPhoto;
                LinearLayout details;
                TextView seekBarValueThreshold;
                TextView seekBarGainValue;
                SeekBar seekBarGain;
                SeekBar seekBarThreshold;
                Button setBtn;
                Button defaultBtn;
                PersonViewHolder(View itemView) {
                    super(itemView);
                    cv = (CardView) itemView.findViewById(R.id.rv);
                    personName = (TextView) itemView.findViewById(R.id.person_name);
                    personAge = (TextView) itemView.findViewById(R.id.person_age);
                    gain = (TextView) itemView.findViewById(R.id.gain);
                    personPhoto = (ImageView) itemView.findViewById(R.id.person_photo);
                    details = (LinearLayout) itemView.findViewById(R.id.details);
                    seekBarThreshold = (SeekBar)itemView.findViewById(R.id.seekBarThreshold);
                    seekBarValueThreshold= (TextView) itemView.findViewById(R.id.thresholdValue);
                    seekBarGain = (SeekBar) itemView.findViewById(R.id.seekBarGain);
                    seekBarGainValue = (TextView) itemView.findViewById(R.id.gainValue);
                    setBtn = (Button) itemView.findViewById(R.id.buttonSet);
                    defaultBtn = (Button) itemView.findViewById(R.id.buttonDefault);
                }

            }
        }




}
