package cpsc430.choretracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class DisplayCreateAccount extends AppCompatActivity {
    private List<String> list = new ArrayList<>();

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

    //Updates visual of dropdown
    public void addToList(){
        Spinner dropdown = findViewById(R.id.spinnerChooseRole);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
    }
}
