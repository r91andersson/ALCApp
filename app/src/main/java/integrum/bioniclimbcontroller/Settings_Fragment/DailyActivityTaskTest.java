package integrum.bioniclimbcontroller.Settings_Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import integrum.bioniclimbcontroller.Graph_Fragment.OverviewFragment;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-11-24.
 */

public class DailyActivityTaskTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_activity_task);

        RecyclerView rvTask = (RecyclerView) findViewById(R.id.rvDailyTask);
        rvTask.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvTask.setLayoutManager(llm);
        rvTask.setNestedScrollingEnabled(false);
        DailyTaskTestAdapter adapter = new DailyTaskTestAdapter();
        rvTask.setAdapter(adapter);
    }


    public class DailyTaskTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class ViewHolderTaskCard extends RecyclerView.ViewHolder {
            Button startTestBtn;
            Button stopTestBtn;

            public ViewHolderTaskCard(View view) {
                super(view);
                startTestBtn = (Button) view.findViewById(R.id.startTest);
                stopTestBtn = (Button) view.findViewById(R.id.stopTest);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderTaskCard(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_daily_activity_task_cardview, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position==0){
                final ViewHolderTaskCard dailyTaskCard = (ViewHolderTaskCard) holder;
                dailyTaskCard.startTestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Start Button Pressed",Toast.LENGTH_SHORT).show();
                    }
                });

                dailyTaskCard.stopTestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Stop Button Pressed",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}
