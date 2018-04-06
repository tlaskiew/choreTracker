package cpsc430.choretracker;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reward extends AppCompatActivity {
    private List<String> rewardList = new ArrayList<>();
    private List<String> starList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private String user;
    private String email;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        final Spinner dropdown = findViewById(R.id.spinnerRewardList);

        // Gather Session data
        getLocal();

        // Show current list
        updateList();

        // Set up title for reward list
        rewardList.add("Current Rewards:");
        addToList(rewardList, 1);
        // Set up values for adding reward
        starList.add("Cost of Reward:");
        for(int i = 10; i <= 500; i+=10){
            starList.add(i + "");
        }
        addToList(starList, 2);

        // Remove selected reward from database
        Button removeCur = findViewById(R.id.buttonRemoveReward);
        removeCur.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                String temp = dropdown.getSelectedItem().toString();
                final String original = temp;
                //Remove contents of ( ) to enable easier database searching
                int startIndex = temp.indexOf("(");
                int endIndex = temp.indexOf(")");
                String rewardValue = "(" + temp.substring(startIndex + 1, endIndex) + ")";
                temp = temp.replace(rewardValue, "");
                temp = temp.replace("$", "Cash: ");

                // Don't remove the title
                if (!original.equals("Current Rewards:")) {
                    myRef.child("Users").child(email).child("Rewards").child(temp).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the reference of the reward and the delete it and update the list
                            dataSnapshot.getRef().removeValue();
                            rewardList.remove(original);
                            updateList();
                            dropdown.setSelection(0, true);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Catch any errors
                        }
                    });
                }
            }
        });

    }

    public void updateList(){
        myRef.child("Users").child(email).child("Rewards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through rewards in database and display current rewards
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String name = dsp.child("rewardName").getValue().toString();
                    // Avoiding error with firebase caused by '$'
                    if(name.contains("Cash: ")){
                        name = name.replace("Cash: ", "$");
                    }
                    String value = dsp.child("rewardValue").getValue().toString();

                    // Making sure no duplicates
                    if(!rewardList.contains(name + "(" + value + ")")) {
                        // Adding and Displaying list to user
                        rewardList.add(name + "(" + value + ")");
                        addToList(rewardList, 1);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    // Add a reward
    public void addReward(View v) {
        // Collect user data
        EditText input = findViewById(R.id.textReward);
        String rewardName = input.getText().toString();

        //
        TextView error = findViewById(R.id.rewardError);

        // Catching symbol '$' that isn't allowed by firebase rules
        if(rewardName.contains("$")){
            rewardName = rewardName.replace("$", "Cash: ");
        }

        // Database ref: Where to put the data
        myRef = database.getReference().child("Users").child(email).child("Rewards").child(rewardName);

        Spinner rewardValueSpinner = findViewById(R.id.spinnerRewardValue);
        String rewardValue = rewardValueSpinner.getSelectedItem().toString();

        // Check user input
        if(rewardName.equals("")) {
            // Reward name was left blank
            error.setText("Type a Reward!");

        } else if(rewardValue.equals("Cost of Reward:")) {
            // Reward value was left blank
            error.setText("Choose a Reward Value!");
        } else {
            // All required input was given

            //Adding all data to database and updating the current list onscreen
            Map<String, String> userData = new HashMap<>();
            userData.put("rewardName", rewardName);
            userData.put("rewardValue", rewardValue);
            myRef.setValue(userData);
            updateList();

            // Cleaning up used boxes and dropdown
            input.setText("");
            rewardValueSpinner.setSelection(0, true);
        }
    }

    // Updates visual of dropdown
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

    // Search for already logged in user
    public void getLocal(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", "");
        email = preferences.getString("Email", "").replace(".", ",");
        role = preferences.getString("Role", "");
    }
}
