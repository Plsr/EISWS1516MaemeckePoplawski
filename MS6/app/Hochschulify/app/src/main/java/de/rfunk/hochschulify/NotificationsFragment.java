package de.rfunk.hochschulify;


import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends android.support.v4.app.Fragment {
    //TODO: Access Single Rows and mark them as unread

    private static String NOTIFICATION_TEMPLATE = "Neuer Kommentar zu ";

    //This is pretty ugly and only in here for the Prototype UI
    private String[] threadNames = {"Was ist ein Monofach-Bachelor, was ist ein Zwei-Fächer-Bachelor?",
                                    "Was ist eine Fakultät?",
                                    "Welche Fächer sind zulassungsfrei und was bedeutet das konkret für mich?",
                                    "Bis wann muss ich mich bewerben?",
                                    "Wie hoch sind die Semesterbeiträge an der Universität Göttingen?",
                                    "Gibt es eine Einführungsveranstaltung für die StudienanfängerInnen?"};

    public NotificationsFragment() {
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
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null && getView().findViewById(R.id.listView) != null) {
            ListView listView = (ListView) getView().findViewById(R.id.listView);
            List<String> listContent = new ArrayList<>();

            for (String thread : threadNames) {
                listContent.add(buildNotificationString(thread));
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, listContent);
            listView.setAdapter(arrayAdapter);
            listView.setClickable(true);

            //Click listener for single rows
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick (AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //TODO: Move to matching Thread
                    System.out.println("Click listener Worked");
                }
            });
        }
    }




    /**
     * This method builds the notification strings to populate the ListView.
     * Takes a String containing the Thread the Notification is about as an argument
     * and combines thi with the template to generate a readable notification message.
     *
     * @param specific Thread the Notification is about
     * @return Built String or null if there is no valid string as an argument
     */
    private String buildNotificationString(String specific) {
        if (specific != null) {
           return  NOTIFICATION_TEMPLATE + specific;
        } else {
            return null;
        }
    }



}
