package cpsc430.choretracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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

        getLocal();

        //User logged in currently is a parent
        if(!user.equals("") && !email.equals("") && !role.equals("") && role.equals("Parent")){
            loggedIn();
        //User logged in currently a child
        }else if(!user.equals("") && !email.equals("") && !role.equals("") && role.equals("Child")){
            loggedIn();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        getLocal();

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
        TextView accountName = findViewById(R.id.accountName);
        accountName.setVisibility(View.INVISIBLE);
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
        TextView accountName = findViewById(R.id.accountName);
        accountName.setVisibility(View.VISIBLE);
        accountName.setText(user);
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
        DatabaseReference myRef = database.getReference().child("Users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            EditText username = findViewById(R.id.username);
            String user = username.getText().toString().replace(".", ",");
            EditText password = findViewById(R.id.password);
            String pass = password.getText().toString();
            int done = 0;

             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(username.getText().toString().equals("")){
                     username.setText("Enter A Username!");
                 }else if(password.getText().toString().equals("")){
                     password.setText("Enter A Password!");
                 }else {
                     for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                         if (dsp.hasChild(user)) {
                             //Username is in the system!
                             done = 1;
                             if (dsp.child(user).child("Password").getValue().toString().equals(pass)) {
                                 //Username and password matches!
                                 String role = "";
                                 String email = "";
                                 done = 2;
                                 //Fixing email
                                 String current = dsp.child(user).getValue().toString().replace(",com", ".com");
                                 current.replace(",net", ".net");
                                 //Check what role they are
                                 if(current.contains("Role=Parent")){
                                     role = "Parent";
                                 }else if(current.contains("Role=Child")){
                                     role = "Child";
                                 }
                                 //Search reference for email
                                 String[] temp = current.split(",");
                                 for(String cur : temp){
                                     if(cur.contains("Email")){
                                        cur = cur.replace(" Email=", "");
                                        email = cur;
                                     }
                                 }
                                 //Adding session
                                 addLocal(user, email, role);
                                 //Send user to view page
                                 userView();
                             }
                         }
                     }
                     if (done == 0) {
                         //Username isn't in the system
                         username.setText("");
                         password.setText("");
                         username.setHint("Account Doesn't Exist!");
                     } else if (done == 1) {
                         //Username is valid but not password
                         password.setText("");
                         password.setHint("Incorrect Password!");
                     }
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    void userView(){
        if(role.equals("Child")) {
            Intent intent = new Intent(this, childView.class);
            startActivity(intent);
        }else if(role.equals("Parent")){
            Intent intent = new Intent(this, parentView.class);
            startActivity(intent);
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

    //Search for already logged in user
    public void getLocal(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", "");
        email = preferences.getString("Email", "").replace(".", ",");
        role = preferences.getString("Role", "");
    }
}
