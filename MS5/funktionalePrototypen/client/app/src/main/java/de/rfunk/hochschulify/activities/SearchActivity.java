package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

public class SearchActivity extends AppCompatActivity {

    ListView mListView;
    Toolbar toolbar;
    SearchView searchView;
    private List<Course> mCourses;
    private List<Course> mResults;
    private ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView) findViewById(R.id.searchView);
        mListView = (ListView)findViewById(R.id.listView);
        toolbar.setTitle("Suche");
        setSupportActionBar(toolbar);

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

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getListFromResults());
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, CourseOverviewActivity.class);
                intent.putExtra("ID", mResults.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void searchFor(String query) {
        mResults = new ArrayList<>();
        for (int i = 0; i < mCourses.size(); i++) {
            Course course = mCourses.get(i);
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                mResults.add(course);
            }
        }
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getListFromResults());
        mListView.setAdapter(mArrayAdapter);
    }

    private List<String> getListFromResults() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mResults.size(); i++) {
            list.add(mResults.get(i).getName());
        }
        return list;
    }
}
