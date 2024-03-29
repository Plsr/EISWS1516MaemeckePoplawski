package de.rfunk.hochschulify.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.activities.SingleThreadActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksThreadsFragment extends android.support.v4.app.Fragment {


    public BookmarksThreadsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarks_threads, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.card1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SingleThreadActivity.class);
                startActivity(intent);

            }
        });
    }




}
