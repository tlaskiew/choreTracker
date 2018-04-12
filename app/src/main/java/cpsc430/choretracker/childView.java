package cpsc430.choretracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class childView extends AppCompatActivity {
    private List<String> choreList = new ArrayList<>();
    private List<String> rewardList = new ArrayList<>();
    private String stars = "0";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String user;
    private String email;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_view);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", "");
        email = preferences.getString("Email", "").replace(".", ",");
        role = preferences.getString("Role", "");

        TextView accountName = findViewById(R.id.accountName);
        accountName.setText(user);

        //Setting up spinners
        choreList.add("Current Chores:");
        addToList(choreList, 1);
        rewardList.add("Current Rewards:");
        addToList(rewardList, 2);

        //Update reward list
        updateList();

        //Update chore list
        updateChoreList();

        //Show Stars
        final TextView totalStars = findViewById(R.id.totalStars);
        totalStars.setText(stars + "");

        //Update stars if anythings changed
        database.getReference().child("Users").child(email).child(user).child("Stars").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stars = dataSnapshot.getValue().toString();
                totalStars.setText(stars);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Catch any errors
            }
        });

        //Claim reward but check child's star amount to make sure they have enough
        final Spinner dropdown = findViewById(R.id.spinnerRewardValue);
        Button removeCur = findViewById(R.id.buttonRedeemReward);
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

                final int val = value;

                //Check to make sure child has enough stars and that its not on the title
                if(!original.equals("Current Rewards:") && val <= Integer.parseInt(stars)) {
                    database.getReference().child("Users").child(email).child("Rewards").child(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the reference of the reward and the delete it and update the list
                            dataSnapshot.getRef().removeValue();
                            rewardList.remove(original);
                            updateList();
                            dropdown.setSelection(0, true);
                            stars = (Integer.parseInt(stars) - val) + "";
                            //Change value of child's stars in database
                            database.getReference().child("Users").child(email).child(user).child("Stars").setValue(stars);
                            TextView error = findViewById(R.id.error);
                            error.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Catch any errors
                        }
                    });

                } else {
                    //Not enough stars!
                    TextView error = findViewById(R.id.error);
                    if(!original.equals("Current Rewards:")) {
                        error.setText("You Don't Have Enough Stars!");
                    }else{
                        error.setText("Choose A Reward!");
                    }
                }
            }
        });

        // Mark a chore as completed
        final Spinner choreSpinner = findViewById(R.id.spinnerChoreList);
        Button claimChore = findViewById(R.id.buttonClaimChore);
        claimChore.setOnClickListener( new View.OnClickListener() {
            public void onClick(final View v) {
                final String original = choreSpinner.getSelectedItem().toString();
                final TextView error = findViewById(R.id.error);

                if(original.equals("Current Chores:")) {
                    error.setText("Please select a chore.");
                } else {
                    final int choreStars = Integer.parseInt(original.substring(original.indexOf('(') + 1, original.indexOf(')')));
                    String chore = original.substring(0, original.indexOf('('));
                    DatabaseReference myRef = database.getReference();
                    myRef.child("Users").child(email).child("Chores").child(chore).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Increment the star value
                            stars = (Integer.parseInt(stars) + choreStars) + "";
                            database.getReference().child("Users").child(email).child(user).child(stars).setValue(stars);
                            updateStarValue();

                            // Remove the chore from the database
                            dataSnapshot.getRef().removeValue();
                            choreList.remove(original);
                            updateChoreList();
                            choreSpinner.setSelection(0, true);
                            error.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    //Display Rewards
    public void updateList(){
        database.getReference().child("Users").child(email).child("Rewards").addValueEventListener(new ValueEventListener() {
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
                        addToList(rewardList, 2);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    //Display Chores
    public void updateChoreList(){
        database.getReference().child("Users").child(email).child("Chores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through rewards in database and display current chores
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String name = dsp.child("choreName").getValue().toString();
                    String value = dsp.child("starValue").getValue().toString();

                    // Making sure no duplicates
                    if(!choreList.contains(name + "(" + value + ")")) {
                        // Adding and Displaying list to user
                        choreList.add(name + "(" + value + ")");
                        addToList(choreList, 1);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void updateStarValue() {
        TextView starView = findViewById(R.id.totalStars);
        starView.setText(stars);
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
