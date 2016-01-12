package de.rfunk.hochschulify.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.rfunk.hochschulify.R;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Suche");
        setSupportActionBar(toolbar);

        listView = (ListView)findViewById(R.id.listView);
        List<String> listContent = new ArrayList<>();
        listContent.add("nrw.de");
        listContent.add("dekra-hochschule-berlin");
        listContent.add("Medieninformatik");
        listContent.add("businessschool-berlin-potsdam");
        listContent.add("BWL");
        listContent.add("Deutsch TH Köln");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContent);
        listView.setAdapter(arrayAdapter);
    }
}
