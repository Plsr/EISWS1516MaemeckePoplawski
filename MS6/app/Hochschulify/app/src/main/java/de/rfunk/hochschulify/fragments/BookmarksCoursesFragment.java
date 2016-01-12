package de.rfunk.hochschulify.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.rfunk.hochschulify.activities.CourseOverviewActivity;
import de.rfunk.hochschulify.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksCoursesFragment extends Fragment {


    public BookmarksCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarks_courses, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.course1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CourseOverviewActivity.class);
                startActivity(intent);

            }
        });
    }

}
