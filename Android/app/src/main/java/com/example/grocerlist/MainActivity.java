package com.example.grocerlist;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout checkboxContainer;
    private Button addButton;
    private ArrayAdapter<String> adapter; // for the list of lists

    // list of lists

    private Spinner spinnerDropdown;
    private List<String> dropdownItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views by their respective IDs
        EditText searchBar = findViewById(R.id.searchBar);

        // add list of lists
        spinnerDropdown = findViewById(R.id.spinner_dropdown);
        dropdownItems = new ArrayList<>();
        dropdownItems.add("Add List");
        dropdownItems.add("Remove List");

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, dropdownItems);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Apply the adapter to the spinner
        spinnerDropdown.setAdapter(adapter);

        // Set a listener to handle item selection
        spinnerDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = dropdownItems.get(position);
                if (selectedItem.compareTo("Add List") == 0) {
                    showAddItemDialog();
                } else if(selectedItem.compareTo("Remove List") == 0) {
                    showRemoveItemDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        checkboxContainer = findViewById(R.id.checkboxContainer);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditTextDialog();
            }
        });
    }

    // Method to dynamically add items to the list of lists
    private void addItemToDropdown(String newItem) {
        dropdownItems.add(newItem);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDropdown.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void showRemoveItemDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Item")
                .setMessage("Select an item to remove:")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected item
                        EditText editText = dialogView.findViewById(R.id.editTextButtonName);
                        String buttonName = editText.getText().toString();
                        // Remove the selected item
                        if(buttonName.compareTo("Add List") != 0 || buttonName.compareTo("Remove List") != 0) {
                            dropdownItems.remove(buttonName);
                        }
                        // Update the adapter
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // Add a new list
    public void onAddItemClick(View view) {
        addItemToDropdown("New List");
    }

    // Show an AlertDialog to get the button name from the user
    private void showAddItemDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Enter Button Name")
                .setPositiveButton("Add List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = dialogView.findViewById(R.id.editTextButtonName);
                        String buttonName = editText.getText().toString();
                        addItemToDropdown(buttonName);
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // enter text of checkbox
    private void openEditTextDialog() {
        // Use a custom dialog or AlertDialog to prompt the user for text input
        // For simplicity, this example uses a custom layout with an EditText
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edittext, null);
        EditText editText = dialogView.findViewById(R.id.editTextDialog);

        // Create an AlertDialog and set the custom view
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Enter Text")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    // Retrieve the entered text from the EditText
                    String newText = editText.getText().toString().trim();

                    // Check if the entered text is not empty
                    if (!TextUtils.isEmpty(newText)) {
                        // Add a new checkbox with the entered text
                        addCheckbox(newText);
                    } else {
                        // Show a toast message if the entered text is empty
                        Toast.makeText(MainActivity.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        alertDialog.show();
    }

    private void addCheckbox(String text) {
        CheckBox newCheckbox = new CheckBox(this);
        newCheckbox.setText(text);

        // Set an OnCheckedChangeListener to handle checkbox state changes
        newCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Apply or remove strikethrough based on the checkbox state
                if (isChecked) {
                    buttonView.setPaintFlags(buttonView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    buttonView.setPaintFlags(buttonView.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
        checkboxContainer.addView(newCheckbox);
    }
}
