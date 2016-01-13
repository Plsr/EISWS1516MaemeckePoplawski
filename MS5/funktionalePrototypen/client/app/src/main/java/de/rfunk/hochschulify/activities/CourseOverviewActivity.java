package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.adapters.CourseOverviewAdapter;
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.pojo.University;
import de.rfunk.hochschulify.pojo.User;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;
import de.rfunk.hochschulify.pojo.Entry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseOverviewActivity extends AppCompatActivity implements CourseOverviewAdapter.EntryAdapterInterface {



    // ID of dummy course for development
    public static final String COURSE_ID = "5694f9550beaa63b4ef4e3d5";

    public static final String SERVER_URL = Utils.SERVER_URL;
    public static final String COURSE_PATH = Utils.COURSE_PATH;

    Toolbar toolbar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Entry> mEntries = new ArrayList<Entry>();
    private Course mCourse;
    private String mCourseId;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_overview);

        Bundle args = getIntent().getExtras();
        mCourseId = args.getString("ID");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setThreads();


        findViewById(R.id.button_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCourse != null) {
                    Intent writeIntent = new Intent(CourseOverviewActivity.this, WriteThreadActivity.class);
                    writeIntent.putExtra("COURSE_ID", mCourse.getId());
                    startActivity(writeIntent);
                }
            }
        });
    }

    public void setThreads() {
        // Set up request
        // Thread ID is sent in the URL, no body required
        String url = SERVER_URL + COURSE_PATH + "/" + mCourseId;

        Req req = new Req(this, url);
        req.get(new Req.Res() {

            @Override
            public void onSuccess(JSONObject res) {
                try {
                    mCourse = Parse.course(res);
                    JSONArray entries = res.getJSONArray("entries");
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject jsonEntry = entries.getJSONObject(i);
                        Entry entry = Parse.entry(jsonEntry);
                        mEntries.add(entry);
                    }

                    toolbar.setTitle(mCourse.getName());
                    toolbar.setSubtitle(mCourse.getUniversity().getName());
                    setUpRecyclerView();
                    setCorrectBookmarkIcon();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // TODO: Maybe Errorhandling?
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        //TODO: Do stuff
        Intent intent = new Intent(CourseOverviewActivity.this, SingleThreadActivity.class);
        intent.putExtra("ID", mEntries.get(position).getId());
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_overview, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_bookmark:
                bookmarkCourse();
                return true;
            case R.id.action_unbookmark:
                unbookmarkCourse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true); //Needed? Answer: Yes, I think so.
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CourseOverviewAdapter(this, mEntries, this);
        recyclerView.setAdapter(mAdapter);
    }

    public void bookmarkCourse() {
        Set<String> bookmarks = getCoursesBookmarks();
        bookmarks.add(mCourse.getId());
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet("COURSES_BOOKMARKS", bookmarks);
        editor.apply();
        setCorrectBookmarkIcon();
        Toast toast = Toast.makeText(this, "Bookmark hinzugefügt", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void unbookmarkCourse() {
        Set<String> bookmarks = getCoursesBookmarks();
        bookmarks.remove(mCourse.getId());
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet("COURSES_BOOKMARKS", bookmarks);
        editor.apply();
        setCorrectBookmarkIcon();
        Toast toast = Toast.makeText(this, "Bookmark entfernt", Toast.LENGTH_SHORT);
        toast.show();
    }

    public Set<String> getCoursesBookmarks() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> bookmarks = new HashSet<>();
        try {
            bookmarks = sharedPrefs.getStringSet("COURSES_BOOKMARKS", new HashSet<String>());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookmarks;
    }

    public void setCorrectBookmarkIcon() {
        Set<String> bookmarks = getCoursesBookmarks();
        mMenu.findItem(R.id.action_bookmark).setVisible(true);
        mMenu.findItem(R.id.action_unbookmark).setVisible(false);
        if(bookmarks.contains(mCourse.getId())) {
            mMenu.findItem(R.id.action_bookmark).setVisible(false);
            mMenu.findItem(R.id.action_unbookmark).setVisible(true);
        }
    }
}
