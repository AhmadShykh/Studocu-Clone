package com.example.studoc_clone;


import android.os.Bundle;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.studoc_clone.adapters.ViewPagerAdapter;
import com.example.studoc_clone.fragments.chats_fragment;
import com.example.studoc_clone.fragments.home_fragment;
import com.example.studoc_clone.fragments.search_fragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        tabLayout = findViewById(R.id.bottomBar);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);


        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new home_fragment(),"Home");
        vpAdapter.addFragment(new search_fragment(),"Search");
        vpAdapter.addFragment(new chats_fragment(),"Chat");

        viewPager.setAdapter(vpAdapter);



        int[] tabIcons = {
                R.drawable.home_logo, // Replace with your drawable resource names
                R.drawable.search_logo,
                R.drawable.chats_logo
        };

        for (int i = 0; i < tabIcons.length; i++) {
            if (tabLayout.getTabAt(i) != null) {
                tabLayout.getTabAt(i).setIcon(tabIcons[i]);
            }
        }
    }


}