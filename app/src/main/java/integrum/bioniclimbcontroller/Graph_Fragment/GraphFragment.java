package integrum.bioniclimbcontroller.Graph_Fragment;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import integrum.bioniclimbcontroller.R;
import integrum.bioniclimbcontroller.ThreadUtils;

/**
 * Created by Robin on 2016-09-29.
 */
public class GraphFragment extends Fragment {

    PagerAdapterGraphFragment adapter;
    Fragment emg;
    Fragment xyz;
    Fragment temp;
    String threadID;
    ViewPager viewPager;
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //Terminate graph threads
        terminateGraphThread();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_graph, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("EMG"));
        tabLayout.addTab(tabLayout.newTab().setText("XYZ"));
        tabLayout.addTab(tabLayout.newTab().setText("Temp"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new PagerAdapterGraphFragment(GraphFragment.this.getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setCurrentItem(0);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public void terminateGraphThread(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String threadNameEMG = sharedPrefs.getString("EMGThreadName", null);
        String threadNameXYZ = sharedPrefs.getString("XYZThreadName", null);
        ThreadUtils utilsThread = new ThreadUtils();
        Thread currentEMG=utilsThread.getThreadByName(threadNameEMG);
        Thread currentXYZ=utilsThread.getThreadByName(threadNameXYZ);

        if(currentEMG!=null) {
            while (currentEMG.isAlive()) {
                currentEMG.interrupt();
            }
        }

        if(currentXYZ!=null) {
            while (currentXYZ.isAlive()) {
                currentXYZ.interrupt();
            }
        }
    }

}