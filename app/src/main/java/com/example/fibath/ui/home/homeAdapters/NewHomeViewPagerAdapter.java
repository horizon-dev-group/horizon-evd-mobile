package com.example.fibath.ui.home.homeAdapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fibath.ui.home.homeFragments.BalanceTransferHistoryFragment;
import com.example.fibath.ui.home.homeFragments.ReportFragment;
import com.example.fibath.ui.home.homeFragments.MyAgentsFragment;
import com.example.fibath.ui.home.homeFragments.VouchersFragment;

public class NewHomeViewPagerAdapter extends FragmentPagerAdapter {
    int totalFragmentItems;

    public NewHomeViewPagerAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalFragmentItems = totalTabs;
    }

    // region Return specific fragment according to the selected item
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                VouchersFragment vouchersFragment = new VouchersFragment();
                return vouchersFragment;
            case 1:
                MyAgentsFragment myAgentsFragment = new MyAgentsFragment();
                return myAgentsFragment;
            case 2:
                BalanceTransferHistoryFragment balanceTransferHistoryFragment = new BalanceTransferHistoryFragment();
                return balanceTransferHistoryFragment;
            case 3:
                ReportFragment comingSoonFragment = new ReportFragment();
                return comingSoonFragment;
            default:
                return null;
        }
    }
    // endregion

    // region Get total number of fragment items
    @Override
    public int getCount() {
        return totalFragmentItems;
    }
    // endregion
}
