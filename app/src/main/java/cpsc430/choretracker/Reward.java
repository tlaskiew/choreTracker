package cpsc430.choretracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reward extends AppCompatActivity {
    private List<String> rewardList = new ArrayList<>();
    private List<String> starList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        //Set up title for reward list
        rewardList.add("Current Rewards:");
        addToList(rewardList, 1);
        //Set up values for adding reward
        starList.add("Cost of Reward:");
        for(int i = 10; i <= 500; i+=10){
            starList.add(i + "");
        }
        addToList(starList, 2);

    }

    // Add a reward
    public void addReward(View v) {
        // Collect user data
        EditText input = findViewById(R.id.choreText);
        String rewardName = input.getText().toString();

        Spinner rewardValueSpinner = findViewById(R.id.spinnerStarValue);
        String rewardValue = rewardValueSpinner.getSelectedItem().toString();

        // Check user input
        if(rewardName.equals("")) {
            // Reward name was left blank

        } else if(rewardValue.equals("Cost of Reward:")) {
            // Reward value was left blank

        } else {
            // All required input was given
            Map<String, String> userData = new HashMap<>();
            userData.put("rewardName", rewardName);
            userData.put("rewardValue", rewardValue);
        }
    }

    //Updates visual of dropdown
    public void addToList(List L, int choice){
        Spinner dropdown;
        if(choice == 1){
            dropdown = findViewById(R.id.spinnerRewardList);
        }else{
            dropdown = findViewById(R.id.spinnerRewardValue);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, L);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
    }
}
