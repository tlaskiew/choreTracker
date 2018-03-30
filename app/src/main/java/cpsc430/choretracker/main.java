package cpsc430.choretracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class main extends AppCompatActivity {
    public final String EXTRA_MESSAGE = "MESSAGE";
    private String user;
    private String email;
    private String role;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Search for already logged in user
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", "");
        email = preferences.getString("Email", "").replace(".", ",");
        role = preferences.getString("Role", "");

        //User logged in currently is a parent
        if(!user.equals("") && !email.equals("") && !role.equals("") && role.equals("Parent")){
            loggedIn();
        //User logged in currently a child
        }else if(!user.equals("") && !email.equals("") && !role.equals("") && role.equals("Child")){
            loggedIn();
        }
    }

    //Clear local session data and then make visible needed items while making others invisible
    public void logout(View v){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Button view = findViewById(R.id.buttonView);
        view.setVisibility(View.INVISIBLE);
        Button logout = findViewById(R.id.buttonLogout);
        logout.setVisibility(View.INVISIBLE);
        EditText username = findViewById(R.id.username);
        username.setVisibility(View.VISIBLE);
        EditText pass = findViewById(R.id.password);
        pass.setVisibility(View.VISIBLE);
        Button login = findViewById(R.id.buttonLogin);
        login.setVisibility(View.VISIBLE);
        Button createAcc = findViewById(R.id.buttonCreateAccount);
        createAcc.setVisibility(View.VISIBLE);
    }

    //Logged in, hide unneeded items
    public void loggedIn(){
        Button view = findViewById(R.id.buttonView);
        view.setVisibility(View.VISIBLE);
        Button logout = findViewById(R.id.buttonLogout);
        logout.setVisibility(View.VISIBLE);
        EditText username = findViewById(R.id.username);
        username.setVisibility(View.INVISIBLE);
        EditText pass = findViewById(R.id.password);
        pass.setVisibility(View.INVISIBLE);
        Button login = findViewById(R.id.buttonLogin);
        login.setVisibility(View.INVISIBLE);
        Button createAcc = findViewById(R.id.buttonCreateAccount);
        createAcc.setVisibility(View.INVISIBLE);
    }

    //Sends user to appropriate page if signed in
    public void view(View v){
        if(role.equals("Child")) {
            Intent intent = new Intent(this, childView.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }else if(role.equals("Parent")){
            Intent intent = new Intent(this, parentView.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

    //Go-to createAccount view
    public void createAccount(View v) {
        Intent intent = new Intent(this, DisplayCreateAccount.class);
        EditText editText = (EditText) findViewById(R.id.username);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    //Go-to child/parent view
    public void login(View v){
        //Connecting to database and getting main reference
        DatabaseReference myRef= database.getReference().child("Users");

        //Get Username
        EditText editText = findViewById(R.id.username);
        String username = editText.getText().toString();

        //Get Password
        EditText password = findViewById(R.id.password);
        String pass = password.getText().toString();

        //Replace with users eventually
        if(username.equals("Parent") && pass.equals("Password")){
            //Correct Username/Password, Send Username to next window
            Intent intent = new Intent(this, parentView.class);
            intent.putExtra(user, username);
            startActivity(intent);
        }else if(username.equals("Child") && pass.equals("Password")){
            Intent intent = new Intent(this, childView.class);
            intent.putExtra(user, username);
            startActivity(intent);
        }else{
            //Incorrect Username/Password
            password.setText("");
            password.setHint("Invalid Credentials");
        }
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
}
