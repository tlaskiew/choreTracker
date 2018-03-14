package cpsc430.choretracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    }

    //Create account after collecting user data
    public void create(View v){
        DatabaseReference myRef = database.getReference();

        //Collect all data from user
        EditText email = findViewById(R.id.textEmail);
        String userEmail = email.getText().toString();
        EditText username = findViewById(R.id.textUsername);
        String user = username.getText().toString();
        EditText pass = findViewById(R.id.textPassword);
        String password = pass.getText().toString();
        Spinner role = findViewById(R.id.spinnerChooseRole);
        String chosenRole = role.getSelectedItem().toString();

        Map<String, String> userData = new HashMap<>();
        userEmail = EncodeString(userEmail);
        userData.put("Username", user);
        userData.put("Email", userEmail);
        userData.put("Password", password);
        userData.put("Role", chosenRole);
        if(chosenRole.equals("Child")){
            userData.put("Stars", "0");
        }

        myRef = myRef.child("Users").child(userEmail).child(user);

        //Input value into database
        myRef.setValue(userData);

        if(chosenRole.equals("Parent")){
            Intent intent = new Intent(this, parentView.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }else if(chosenRole.equals("Child")){
            Intent intent = new Intent(this, childView.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
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
