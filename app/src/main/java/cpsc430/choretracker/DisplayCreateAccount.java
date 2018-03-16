package cpsc430.choretracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class DisplayCreateAccount extends AppCompatActivity {
    private List<String> list = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_create_account);

        //Fill role spinner
        list.add("Choose Role:");
        list.add("Parent");
        list.add("Child");
        addToList();

        Button create = findViewById(R.id.buttonCreateAccount);
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                DatabaseReference myRef = database.getReference();

                //Collect all data from user
                EditText email = findViewById(R.id.textEmail);
                final String userEmail = EncodeString(email.getText().toString());
                EditText username = findViewById(R.id.textUsername);
                final String user = username.getText().toString();
                EditText pass = findViewById(R.id.textPassword);
                final String password = pass.getText().toString();
                Spinner role = findViewById(R.id.spinnerChooseRole);
                final String chosenRole = role.getSelectedItem().toString();

                myRef = myRef.child("Users").child(userEmail);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user)){
                            //Catch if an account with the username already exists
                            TextView error = findViewById(R.id.createError);
                            error.setText("Account Already Exists!");
                        }else{
                            Map<String, String> userData = new HashMap<>();
                            userData.put("Username", user);
                            userData.put("Email", userEmail);
                            userData.put("Password", password);
                            userData.put("Role", chosenRole);
                            if (chosenRole.equals("Child")) {
                                userData.put("Stars", "0");
                            }
                            //Add data to database
                            dataSnapshot.getRef().child(user).setValue(userData);

                            TextView error = findViewById(R.id.createError);
                            if(chosenRole.equals("Parent") && !error.getText().toString().contains("Account Already Exists!")){
                                //Go-To Parent View
                                Intent intent = new Intent(v.getContext(), parentView.class);
                                intent.putExtra("user", user);
                                addLocal(user, userEmail, chosenRole);
                                startActivity(intent);
                            }else if(chosenRole.equals("Child") && !error.getText().toString().contains("Account Already Exists!")){
                                //Go-To Child View
                                Intent intent = new Intent(v.getContext(), childView.class);
                                intent.putExtra("user", user);
                                addLocal(user, userEmail, chosenRole);
                                startActivity(intent);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //Adds a session for the user who logged in
    public void addLocal(String user, String email, String role){
        //Add user to signed in
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Username", user);
        editor.putString("Email", email);
        editor.putString("Role", role);
        editor.apply();
    }

    //Replaces . with , to allow the string to be put into database
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    //Replaces , with . to correct the string back to original
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    //Updates visual of dropdown
    public void addToList(){
        Spinner dropdown = findViewById(R.id.spinnerChooseRole);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
    }
}
