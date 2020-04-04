package com.kevin.wilmingtonwishlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.kevin.wilmingtonwishlist.util.SectionsPagerAdapter;

public class search extends AppCompatActivity {

    private static final String TAG = "search";

    // Widgets
    private TabLayout mTabLayout;
    public ViewPager mViewPager;

    // Variables
    public SectionsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);

        setupViewPager();
    }

    private void setupViewPager() {
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new SearchFragment());
        mPagerAdapter.addFragment(new WatchListFragment());
        mPagerAdapter.addFragment(new PostFragment());
        mPagerAdapter.addFragment(new AccountFragment());

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(getString(R.string.fragment_search));
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_watch_list));
        mTabLayout.getTabAt(2).setText(getString(R.string.fragment_post));
        mTabLayout.getTabAt(3).setText(getString(R.string.fragment_account));
    }

}
