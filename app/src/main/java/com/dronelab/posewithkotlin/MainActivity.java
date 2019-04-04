package com.dronelab.posewithkotlin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    // ArrayList<String> listOfActionsInMainPage = new ArrayList<>(asList("Pose Estimation", "Prediction Log"));
    ListView mainList;
    CustomListAdapter whatever;
    String[] nameArray = {"Pose Estimation", "Prediction Log"};
    String[] infoArray = {
            "Opens camera to detect live pose.",
            "Check Logs."
    };

    Integer[] imageArray = {R.drawable.running,
            R.drawable.logicon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainList = findViewById(R.id.listView);
        mainList.setDivider(null);
        generateListView();

    }

    public void generateListView(){
        whatever = new CustomListAdapter(this, nameArray, infoArray, imageArray);

        //final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.list_layout,listOfActionsInMainPage);
        mainList.setAdapter(whatever);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(whatever.getItem(position).toString().equalsIgnoreCase(nameArray[0])){
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);
                }

            }
        });

    }
}

