package de.rfunk.hochschulify.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import de.rfunk.hochschulify.activities.CourseOverviewActivity;
import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.activities.SearchActivity;
import de.rfunk.hochschulify.adapters.CourseBookmarkAdapter;
import de.rfunk.hochschulify.adapters.CourseOverviewAdapter;
import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.utils.Parse;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksCoursesFragment extends Fragment implements CourseBookmarkAdapter.CourseAdapterInterface {

    Set<String> mIdSet;
    List<Course> mCourseList;
    Iterator<String> mIterator;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FrameLayout mNothingThere;
    private FrameLayout mLoadingIndicator;
    private Button mButton;


    public BookmarksCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_bookmarks_courses, container, false);
        mRecyclerView = (RecyclerView) inflated.findViewById(R.id.bookmarkCourseList);
        mLoadingIndicator = (FrameLayout) inflated.findViewById(R.id.loading);
        mNothingThere = (FrameLayout) inflated.findViewById(R.id.nothing);
        mButton = (Button) inflated.findViewById(R.id.searchButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        return inflated;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    public Set<String> getBookmarkedCourses() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> bookmarks = new HashSet<>();
        try {
            bookmarks = sharedPrefs.getStringSet("COURSES_BOOKMARKS", new HashSet<String>());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookmarks;
    }

    public void asyncLoad(String id) {
        Req req = new Req(getActivity(), Utils.SERVER_URL+Utils.COURSE_PATH+"/"+id);
        req.get(new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                mCourseList.add(Parse.course(res));
                if (mIterator.hasNext()) asyncLoad(mIterator.next());
                else asyncLoadIsFinished();
            }

            @Override
            public void onError(VolleyError error) {
                if (mIterator.hasNext()) asyncLoad(mIterator.next());
                else asyncLoadIsFinished();
            }
        });
    }

    public void asyncLoadIsFinished() {
        mLoadingIndicator.setVisibility(View.GONE);
        if (mCourseList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNothingThere.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNothingThere.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setHasFixedSize(true); //Needed? Answer: Yes, I think so.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CourseBookmarkAdapter(getActivity(), mCourseList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void fetchBookmarkedCourses() {
        mIdSet = getBookmarkedCourses();
        mIterator = mIdSet.iterator();
        mCourseList = new ArrayList<>();
        if (mIterator.hasNext()) asyncLoad(mIterator.next());
        else asyncLoadIsFinished();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), CourseOverviewActivity.class);
        intent.putExtra("ID", mCourseList.get(position).getId());
        startActivity(intent);
    }

    public void load() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        fetchBookmarkedCourses();
    }
}
