package cpsc430.choretracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class parentView extends AppCompatActivity {
    public final String user = "";
    public final String EXTRA_MESSAGE = "MESSAGE";
    private List<String> starList = new ArrayList<>();
    private List<String> choreList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);

        //Set title for chore list spinner
        choreList.add("Current Chores:");
        addToList(choreList, 2);

        //Set star value spinner
        starList.add("Chore Star Value:");
        for(int i = 1; i <= 10; i++){
            starList.add(i + "");
        }
        addToList(starList, 1);

    }

    //Fill spinners
    public void addToList(List L, int choice){
        Spinner dropdown;
        if(choice == 1){
            dropdown = findViewById(R.id.spinnerStarValue);
        }else{
            dropdown = findViewById(R.id.choreList);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, L);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
    }

    //Go-to rewards page
    public void rewards(View v){
        Intent intent = new Intent(this, Reward.class);
        intent.putExtra(user, EXTRA_MESSAGE);
        startActivity(intent);
    }


}
