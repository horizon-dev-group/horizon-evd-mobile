package com.example.fibath.ui.statement;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FundTransactionAdapter extends FragmentStatePagerAdapter {
    int totalTabs;

    public FundTransactionAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CreditFragment creditFragment = new CreditFragment();
                return creditFragment;
            case 1:
                DebateFragment debateFragment = new DebateFragment();
                return debateFragment;
            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}