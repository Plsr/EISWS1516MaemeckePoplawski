package de.rfunk.hochschulify.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.adapters.CourseBookmarkAdapter;
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class SearchActivity extends AppCompatActivity implements CourseBookmarkAdapter.CourseAdapterInterface {

    private final static String TAG = SearchActivity.class.getSimpleName();

    // TODO: A loading indicator for the HTTP Request would be good for slow connections.
    // Currently not in scope of project.

    RecyclerView mRecyclerView;
    Toolbar toolbar;
    SearchView searchView;
    private boolean mLocationAvailable = true;
    private String mCurrentQuery = "";
    private TextView mNoResultsText;
    private TextView mNoSearchText;
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
        mNoResultsText = (TextView) findViewById(R.id.noResultsText);
        mNoSearchText = (TextView) findViewById(R.id.noSearchText);
        toolbar.setTitle("Suche");
        setSupportActionBar(toolbar);


        // [START] Check if location is available and hide GeoSearch Information if no geopos
        // available
        LocationManager locationManager =
                (LocationManager) SearchActivity.this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation == null) {
            mGeoSearchButton.setVisibility(View.GONE);
            mNoSearchText.setVisibility(View.GONE);
            mLocationAvailable = false;
        }
        // [END] location

        // Set up recycler view
        mRecyclerView.setHasFixedSize(true); //Needed? Answer: Yes, I think so.
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set up click listeners
        mGeoSearchButton.setOnClickListener(mGeoSearchClickListener);
        searchView.setOnQueryTextListener(mSearchViewQueryListener);
        mResults = new ArrayList<>();

        // Make a Request to get all courses
        Req req = new Req(this, Utils.SERVER_URL+Utils.COURSE_PATH);
        req.get(new Req.Res() {

            @Override
            public void onSuccess(JSONObject res) {
                // Convert the received courses to our nice Course Objects
                // and add them to the ArrayList of all courses
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
                Log.e(TAG, "Search Request error: ", error);
            }
        });
    }

    // Helper for just filtering an ArrayList<Course> by a query
    private ArrayList<Course> filter(String query, ArrayList<Course> courses) {
        ArrayList<Course> result = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            // If course name contains query, add it to the results
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                result.add(course);
            }
        }
        return result;
    }

    // Searching for a course is actually filtering all received courses by the search query
    private void searchFor(String query) {
        mResults = new ArrayList<>();
        for (int i = 0; i < mCourses.size(); i++) {
            Course course = mCourses.get(i);
            // If course name contains query, add it to the results
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                mResults.add(course);
            }
        }

        fillList();
    }

    // When clicking on a searchResult, switch to the course activity
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(SearchActivity.this, CourseOverviewActivity.class);
        intent.putExtra("ID", mResults.get(position).getId());
        startActivity(intent);
    }

    // When clicking the GeoSearch Button, first show a Dialog with a NumberSpinner.
    // The user can define the search-distance. Then filter the courses by the user's inputs.
    private View.OnClickListener mGeoSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // [START] Build the Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
            builder.setTitle("Umkreissuche");
            builder.setMessage("Suche Studiengänge im Umkreis von:");
            LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_search_geo, null);
            builder.setView(dialogView);

            final NumberPicker distancePicker = (NumberPicker) dialogView.findViewById(R.id.distancePicker);
            distancePicker.setMinValue(1);
            distancePicker.setMaxValue(18);
            distancePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            distancePicker.setValue(1);
            distancePicker.setWrapSelectorWheel(false);

            // Bug in NumberPicker, where Formatter is not applied to first value.
            // This fixes it.
            // See http://stackoverflow.com/a/19104078/2376069
            try {
                Method method = distancePicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
                method.setAccessible(true);
                method.invoke(distancePicker, true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return (value * 50) + "";
                }
            };
            distancePicker.setFormatter(formatter);

            builder.setPositiveButton("Suchen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    geoSearch(distancePicker.getValue());
                }
            });
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // intentionally left blank
                }
            });

            // Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            // [END] dialog
        }
    };

    // If search is submitted, search for it (filter all results).
    // If search query changed, check if it's empty and show/hide some views
    private SearchView.OnQueryTextListener mSearchViewQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchFor(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mCurrentQuery = newText;

            if (newText.equals("")) {
                mResults = new ArrayList<>();
                fillList();
                mNoResultsText.setVisibility(View.GONE);
                if (mLocationAvailable)
                    mNoSearchText.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };

    // Sort a HashMap by values
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

    // Fill the recyclerview with the results (filtered courses)
    private void fillList() {
        mNoSearchText.setVisibility(View.GONE);
        if (mResults.size() > 0) {
            mNoResultsText.setVisibility(View.GONE);
            mAdapter = new CourseBookmarkAdapter(this, mResults, this, false);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoResultsText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }

    // filter courses by distance and optionally by the current query
    private void geoSearch(int dis) {
        float maxDistanceInMeters = dis * 50 * 1000;

        // Get current geopos
        LocationManager locationManager =
                (LocationManager) SearchActivity.this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (lastKnownLocation != null) {

            // For sorting and filtering by distance, we need to hashmaps.
            // One with the courseid and the course, and one with the courseid and the distances.
            // We will sort the distaneMap by its values (distance) and use the keys of this
            // sorted HashMaps to sort the HashMap with the Courses.
            HashMap<String, Course> courseMap = new HashMap<>();
            HashMap<String, Float> distanceMap = new HashMap<>();

            // Filter Results by maxDistance
            for (Course course : mCourses) {
                // Set new Location for current course
                Location loc = new Location(course.getName());
                loc.setLatitude(course.getUniversity().getGeoPos().getLat());
                loc.setLongitude(course.getUniversity().getGeoPos().getLong());

                // Get distance between current location and location of course's university
                Float distance = loc.distanceTo(lastKnownLocation);

                // If the distance is in the users given range, add it to our maps.
                if (distance < maxDistanceInMeters) {
                    courseMap.put(course.getId(), course);
                    distanceMap.put(course.getId(), distance);
                }
            }

            // Sort results by distance
            distanceMap = sortByValues(distanceMap);

            // Save the sorted HashMap to an ArrayList
            ArrayList<Course> results = new ArrayList<>();
            for (String id : distanceMap.keySet()) {
                results.add(courseMap.get(id));
            }

            // If there's currently text in the SearchView, filter the results by this query
            if (!mCurrentQuery.equals("")) {
                results = filter(mCurrentQuery, results);
            }

            mResults = results;
        }

        // Finally, show the results by filling the recyclerview
        fillList();
    }
}
