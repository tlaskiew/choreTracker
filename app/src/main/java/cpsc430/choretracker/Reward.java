package cpsc430.choretracker;

import android.content.Intent;
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
    private List<String> redeemedList = new ArrayList<>();
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

        final TextView error = findViewById(R.id.rewardError);

        // Gather Session data
        getLocal();
        TextView accountName = findViewById(R.id.accountName);
        accountName.setText(user);

        // Show current list
        updateList();

        // Set up title for reward list
        rewardList.add("Current Rewards:");
        addToList(rewardList, 1);
        // Set up values for adding reward
        starList.add("Cost of Reward:");
        for (int i = 10; i <= 500; i += 10) {
            starList.add(i + "");
        }
        addToList(starList, 2);

        // Remove selected reward from database
        Button removeCur = findViewById(R.id.buttonRemoveReward);
        removeCur.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                String temp = dropdown.getSelectedItem().toString();
                final String original = temp;
                int value = 0;
                //Remove contents of ( ) to enable easier database searching
                if(temp.contains("(") || temp.contains(")")) {
                    int startIndex = temp.indexOf("(");
                    int endIndex = temp.indexOf(")");
                    String rewardValue = "(" + temp.substring(startIndex + 1, endIndex) + ")";
                    value = Integer.parseInt(temp.substring(startIndex + 1, endIndex));
                    temp = temp.replace(rewardValue, "");
                } else if(temp.contains("$")) {
                    temp = temp.replace("$", "Cash: ");
                }

                // Don't remove the title
                if (!original.equals("Current Rewards:")) {
                    myRef.child("Users").child(email).child("Rewards").child(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the reference of the reward and the delete it and update the list
                            dataSnapshot.getRef().removeValue();
                            rewardList.remove(original);
                            updateList();
                            dropdown.setSelection(0, true);
                            main.notification(v, original + " has been removed!");
                            error.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Catch any errors
                        }
                    });
                } else {
                    error.setText("Please select a reward.");
                }
            }
        });


        updateRedeemedRewards();
    }

    public void updateList() {
        myRef.child("Users").child(email).child("Rewards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through rewards in database and display current rewards
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String name = dsp.child("rewardName").getValue().toString();
                    // Avoiding error with firebase caused by '$'
                    if (name.contains("Cash: ")) {
                        name = name.replace("Cash: ", "$");
                    }
                    String value = dsp.child("rewardValue").getValue().toString();

                    // Making sure no duplicates
                    if (!rewardList.contains(name + "(" + value + ")")) {
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
    public void addReward(final View v) {
        // Collect user data
        EditText input = findViewById(R.id.textReward);
        String rewardName = input.getText().toString();

        TextView error = findViewById(R.id.rewardError);

        // Catching symbol '$' that isn't allowed by firebase rules
        if (rewardName.contains("$")) {
            rewardName = rewardName.replace("$", "Cash: ");
        }

        // Database ref: Where to put the data
        myRef = database.getReference().child("Users").child(email).child("Rewards").child(rewardName);

        Spinner rewardValueSpinner = findViewById(R.id.spinnerRewardValue);
        String rewardValue = rewardValueSpinner.getSelectedItem().toString();

        // Check user input
        if (rewardName.equals("")) {
            // Reward name was left blank
            error.setText("Type a Reward!");

        } else if (rewardValue.equals("Cost of Reward:")) {
            // Reward value was left blank
            error.setText("Choose a Reward Value!");
        } else {
            // All required input was given
            main.notification(v, rewardName + " has been added!");
            //Adding all data to database and updating the current list onscreen
            Map<String, String> userData = new HashMap<>();
            userData.put("rewardName", rewardName);
            userData.put("rewardValue", rewardValue);
            myRef.setValue(userData);
            updateList();

            // Cleaning up used boxes and dropdown
            input.setText("");
            error.setText("");
            rewardValueSpinner.setSelection(0, true);
            rewardValueSpinner.setSelection(0, true);
        }
    }

    // Updates visual of dropdown
    public void addToList(List L, int choice) {
        Spinner dropdown;
        if (choice == 1) {
            dropdown = findViewById(R.id.spinnerRewardList);
        } else {
            dropdown = findViewById(R.id.spinnerRewardValue);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, L);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
    }

    // Fill the redeemed rewards spinner from the database
    public void updateRedeemedRewards() {
        DatabaseReference myRef = database.getReference().child("Users").child(email).child("redeemedRewards");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Spinner redeemedSpinner = findViewById(R.id.redeemedSpinner);
                if(redeemedList.isEmpty()) {
                    redeemedList.add("Redeemed Rewards:");
                }

                for(DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String name = dsp.child("userName").getValue().toString();
                    String reward = dsp.child("reward").getValue().toString();

                    if(reward.contains("Cash: ")) {
                        reward.replace("Cash: ", "$");
                    }

                    redeemedList.add(name + " - " + reward);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, redeemedList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                redeemedSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error
            }
        });
    }

    // Search for already logged in user
    public void getLocal() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", "");
        email = preferences.getString("Email", "").replace(".", ",");
        role = preferences.getString("Role", "");
    }

    //Go-to chores page
    public void chores(View v) {
        Intent intent = new Intent(this, parentView.class);
        startActivity(intent);
    }

    public void removeRedeemedChore(View v) {
        DatabaseReference myRef = database.getReference().child("Users").child(email).child("redeemedRewards");
        final TextView error = findViewById(R.id.rewardError);
        Spinner redeemedRewardsSpinner = findViewById(R.id.redeemedSpinner);

        final String selectedItem = redeemedRewardsSpinner.getSelectedItem().toString();
        if(selectedItem.equals("Redeemed Rewards:")) {
            error.setText("Please select a redeemed reward.");
        } else {
            String reward = selectedItem.substring(selectedItem.indexOf('-') + 2);
            if(reward.contains("$")) {
                reward.replace("$", "Cash: ");
            }
            myRef.child(reward).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Remove the redeemed reward from the database
                    dataSnapshot.getRef().removeValue();
                    redeemedList.remove(selectedItem);

                    // Update the UI
                    updateRedeemedRewards();
                    error.setText("");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
