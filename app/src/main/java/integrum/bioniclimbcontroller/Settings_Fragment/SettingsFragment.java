package integrum.bioniclimbcontroller.Settings_Fragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import integrum.bioniclimbcontroller.Constants;
import integrum.bioniclimbcontroller.Database.SettingsDbHelper;
import integrum.bioniclimbcontroller.Graph_Fragment.PagerAdapterGraphFragment;
import integrum.bioniclimbcontroller.R;

/**
 * Created by Robin on 2016-09-01.
 */

public class SettingsFragment extends Fragment
{
    ViewPager viewPager;
    private ListPreference mListPreference;
    private SettingsDbHelper settingsdb;
    private int pagesActive=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_settings, container, false);


        TabLayout tab = (TabLayout) view.findViewById(R.id.tab_layout_settings);
        tab.addTab(tab.newTab().setText("Basic"));

        settingsdb=new SettingsDbHelper(getActivity());
        String debugStr[]=settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE);
        if(debugStr!=null) {
            int debugMode = Integer.valueOf(debugStr[3]);

            // If we're in advance mode, let the user switch between advance settings or basic settings
            if (debugMode == Constants.DEBUG_MODE) {
                tab.addTab(tab.newTab().setText("Advance"));
                pagesActive=2;
            }
        }

        viewPager = (ViewPager) view.findViewById(R.id.pager_settings);
        PagerAdapterSettings adapter = new PagerAdapterSettings(getChildFragmentManager());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    public class PagerAdapterSettings extends FragmentPagerAdapter {
        public PagerAdapterSettings(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            settingsdb=new SettingsDbHelper(getActivity());
            String debugStr[]=settingsdb.getLatestSetting(SettingsDbHelper.SETTINGS_TYPE_DEBUG_MODE);
            if(debugStr!=null) {
                int debugMode = Integer.valueOf(debugStr[3]);

                // If we're in advance mode, let the user switch between advance settings or basic settings
                if (debugMode == Constants.DEBUG_MODE) {
                    switch (position) {
                        case 0:
                            return new BasicSettings();
                        case 1:
                            return new AdvanceSettings();
                        default:
                            return new BasicSettings();
                    }
                } else {
                    // We're not in advance mode, return only the basic settings
                    return new BasicSettings();
                }
            } else {
                // If the database were empty or we somehow didn't read from it successfully, return the basic settings
                return new BasicSettings();
            }

        }

        @Override
        public int getCount() {
            return pagesActive;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("HEEEELLLOO WORLD");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            default:
                System.out.println("Not correct CAAAASE");
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }
}
