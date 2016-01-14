package de.rfunk.hochschulify.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.adapters.CourseBookmarkAdapter;
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class SearchActivity extends AppCompatActivity implements CourseBookmarkAdapter.CourseAdapterInterface {

    RecyclerView mRecyclerView;
    Toolbar toolbar;
    SearchView searchView;
    private ImageButton mGeoSearchButton;
    private List<Course> mCourses;
    private List<Course> mResults;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView) findViewById(R.id.searchView);
        mRecyclerView = (RecyclerView)findViewById(R.id.courseResults);
        mGeoSearchButton = (ImageButton) findViewById(R.id.geoSearch);
        toolbar.setTitle("Suche");
        setSupportActionBar(toolbar);

        mRecyclerView.setHasFixedSize(true); //Needed? Answer: Yes, I think so.
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mGeoSearchButton.setOnClickListener(mGeoSearchClickListener);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Req req = new Req(this, Utils.SERVER_URL+Utils.COURSE_PATH);
        req.get(new Req.Res() {

            @Override
            public void onSuccess(JSONObject res) {
                JSONArray courses;
                try {
                    courses = res.getJSONArray("courses");
                    mCourses = new ArrayList<Course>();
                    for (int i = 0; i < courses.length(); i++) {
                        Course course = Parse.course(courses.getJSONObject(i));
                        mCourses.add(course);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        mResults = new ArrayList<>();
    }

    private void searchFor(String query) {
        mResults = new ArrayList<>();
        for (int i = 0; i < mCourses.size(); i++) {
            Course course = mCourses.get(i);
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                mResults.add(course);
            }
        }

        fillList();
    }

    private List<String> getListFromResults() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mResults.size(); i++) {
            list.add(mResults.get(i).getName());
        }
        return list;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(SearchActivity.this, CourseOverviewActivity.class);
        intent.putExtra("ID", mResults.get(position).getId());
        startActivity(intent);
    }

    private View.OnClickListener mGeoSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LocationManager locationManager =
                    (LocationManager) SearchActivity.this.getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {

                HashMap<String, Course> courseMap = new HashMap<>();
                HashMap<String, Float> distanceMap = new HashMap<>();

                float maxDistanceInMeters = 35000;

                // Filter Results by maxDistance
                for (Course course : mCourses) {
                    Location loc = new Location(course.getName());
                    loc.setLatitude(course.getUniversity().getGeoPos().getLat());
                    loc.setLongitude(course.getUniversity().getGeoPos().getLong());
                    Float distance = loc.distanceTo(lastKnownLocation);
                    Log.d("TIMOTIMO", distance + "");
                    if (distance < maxDistanceInMeters) {
                        courseMap.put(course.getId(), course);
                        distanceMap.put(course.getId(), distance);
                    }
                }

                // Sort results by distance
                List<Course> sortedResults = new ArrayList<>();
                SortedSet<Float> distances = new TreeSet<>(distanceMap.values());
                distanceMap = sortByValues(distanceMap);

                mResults = new ArrayList<>();
                for (String id : distanceMap.keySet()) {
                    mResults.add(courseMap.get(id));
                }

            }

            else {
                mResults = new ArrayList<>();
                Course noCourse = new Course();
                noCourse.setName("Keine GeoPos gefunden");
                mResults.add(noCourse);
            }

            fillList();
        }
    };

    // from http://beginnersbook.com/2013/12/how-to-sort-hashmap-in-java-by-keys-and-values/
    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private void fillList() {
        mAdapter = new CourseBookmarkAdapter(this, mResults, this, false);
        mRecyclerView.setAdapter(mAdapter);
    }
}
