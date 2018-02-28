package cpsc430.choretracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class main extends AppCompatActivity {
    public final String EXTRA_MESSAGE = "MESSAGE";
    public final String user = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        DatabaseReference myRef= database.getReference();

        //Get Password
        EditText password = findViewById(R.id.password);
        String pass = password.getText().toString();

        //get Username
        EditText editText = findViewById(R.id.username);
        String username = editText.getText().toString();

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
}
