package integrum.bioniclimbcontroller.Graph_Fragment;


import android.app.Fragment;

import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by Robin on 2016-09-29.
 */
public class PagerAdapterGraphFragment extends FragmentPagerAdapter {
    int mNumOfTabs;
    public PagerAdapterGraphFragment(FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                OverviewFragment tab1= new OverviewFragment();
                return tab1;
            case 1:
                GraphFragmentExtended tab2 = new GraphFragmentExtended();
                return tab2;
            case 2:
                XYZFragment tab3 = new XYZFragment();
                return tab3;
            case 3:
                TempFragment tab4 = new TempFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}

