package com.terrykwon.fliptimer.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.terrykwon.fliptimer.adapters.PagerAdapter;
import com.terrykwon.fliptimer.R;
import com.terrykwon.fliptimer.fragments.RecordFragment;
import com.terrykwon.fliptimer.fragments.TimerFragment;

/**
 * The first screen that the app displays.
 * Uses a ViewPager with 3 tabs.
 */
public class MainActivity extends AppCompatActivity implements
        TimerFragment.OnFragmentInteractionListener, RecordFragment.OnFragmentInteractionListener {

    ViewPager viewPager;
    ImageButton optionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_pie_chart_white_36dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_alarm_white_36dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_list_white_36dp));

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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

        optionsButton = (ImageButton) findViewById(R.id.button_options);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(MainActivity.this, optionsButton);
                menu.getMenuInflater()
                        .inflate(R.menu.menu_options, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        switch (id) {
                            case R.id.menu_item_help:
                                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.menu_item_rate:
                                openPlayStorePage();
                                return true;
                            default:
                                return true;
                        }
                    }
                });

                menu.show();
            }
        });
    }

    // Opens Phlip! Play Store page.
    private void openPlayStorePage() {
        Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())));
        }
    }

    /**
     * Pressing back button takes user to middle screen.
     */
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1, true);
        } else {
            super.onBackPressed();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    // Todo: Settings and Help
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Empty... is there anything to put?
    }
}
