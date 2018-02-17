package cpsc430.choretracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class childView extends AppCompatActivity {
    private List<String> choreList = new ArrayList<>();
    private List<String> rewardList = new ArrayList<>();
    private int stars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_view);

        //Setting up spinners
        choreList.add("Current Chores: ");
        addToList(choreList, 1);
        rewardList.add("Current Rewards:");
        addToList(rewardList, 2);

        //Show Stars
        TextView totalStars = findViewById(R.id.totalStars);
        totalStars.setText(stars + "");
    }

    //Updates visual of dropdown for chores
    public void addToList(List L, int choice){
        Spinner dropdown;
        if(choice == 1){
            dropdown = findViewById(R.id.spinnerChoreList);
        }else{
            dropdown = findViewById(R.id.spinnerRewardValue);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, L);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
    }

}
