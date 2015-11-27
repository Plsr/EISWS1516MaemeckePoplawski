package de.rfunk.hochschulify;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends android.support.v4.app.Fragment {

    private ListView listView;

    public SearchFragment() {
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView)getView().findViewById(R.id.listView);
        List<String> listContent = new ArrayList<>();
        listContent.add("nrw.de");
        listContent.add("dekra-hochschule-berlin");
        listContent.add("Medieninformatik");
        listContent.add("businessschool-berlin-potsdam");
        listContent.add("BWL");
        listContent.add("Deutsch TH Köln");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, listContent);
        listView.setAdapter(arrayAdapter);
    }

}
