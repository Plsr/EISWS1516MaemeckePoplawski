package de.rfunk.hochschulify.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import de.rfunk.hochschulify.fragments.NotificationsFragment;
import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.fragments.BookmarksCoursesFragment;
import de.rfunk.hochschulify.fragments.BookmarksThreadsFragment;
import de.rfunk.hochschulify.services.RegistrationIntentService;
import de.rfunk.hochschulify.utils.Utils;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView searchView;

    BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout= (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                boolean sentToken = sharedPrefs.getBoolean(Utils.SENT_TOKEN_TO_SERVER, false);

                if (sentToken) {
                    Log.d("HomeActivity", "YAY");
                } else {
                    Log.d("HomeActivity", "OH NOES");
                }
            }
        };

    }

    private void setupViewPager(ViewPager  viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BookmarksThreadsFragment(), "Threads");
        adapter.addFragment(new BookmarksCoursesFragment(), "Studiengänge");
        adapter.addFragment(new NotificationsFragment(), "Notifications");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String string) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(string);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_threads:
                Intent threadsIntent = new Intent(this, WrittenThreadsActivity.class);
                startActivity(threadsIntent);
                return true;
            case R.id.action_search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToCourse(View view) {
        Intent intent = new Intent(HomeActivity.this, CourseOverviewActivity.class);
        startActivity(intent);
    }

}
