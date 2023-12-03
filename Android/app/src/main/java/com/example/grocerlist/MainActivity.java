package com.example.grocerlist;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout checkboxContainer;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views by their respective IDs
        EditText searchBar = findViewById(R.id.searchBar);

        checkboxContainer = findViewById(R.id.checkboxContainer);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCheckbox();
            }
        });
    }

    private void addCheckbox() {
        CheckBox newCheckbox = new CheckBox(this);
        newCheckbox.setText("New Checkbox");
        checkboxContainer.addView(newCheckbox);
    }
}
