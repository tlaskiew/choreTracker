package cpsc430.choretracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class parentView extends AppCompatActivity {
    private List<String> starList = new ArrayList<>();
    private List<String> choreList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String user;
    private String email;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", "");
        email = preferences.getString("Email", "").replace(".", ",");
        role = preferences.getString("Role", "");

        TextView accountName = findViewById(R.id.accountName);
        accountName.setText(user);

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

    public void addChore(View v) {
        // Collect user data
        EditText input = findViewById(R.id.choreText);
        String choreName = input.getText().toString();

        // Database ref: Where to put the data
        DatabaseReference myRef = database.getReference().child("Users").child(email).child("Chores");

        Spinner starValueSpinner = findViewById(R.id.spinnerStarValue);
        String starValue = starValueSpinner.getSelectedItem().toString();

        TextView error = findViewById(R.id.rewardError);

        // Check user input
        if(choreName.equals("")) {
            // Chore name was left blank
            error.setText("Please enter a chore name.");
        } else if (starValue.equals("Chore Star Value:")) {
            // Star value was left blank
            error.setText("Please select a star value.");
        } else {
            // All required input is given

            // Add the chore to the database
            Map<String, String> userData = new HashMap<>();
            userData.put("choreName", choreName);
            userData.put("starValue", starValue);
        }
    }

    //Go-to rewards page
    public void rewards(View v){
        Intent intent = new Intent(this, Reward.class);
        startActivity(intent);
    }


}
