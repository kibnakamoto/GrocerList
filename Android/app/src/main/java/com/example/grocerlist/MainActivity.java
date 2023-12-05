package com.example.grocerlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout checkboxContainer;
    private Button addButton; // for list in the list

    private Button addListButton; // list adder
    private Button removeListButton; // list remover

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

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, dropdownItems);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Apply the adapter to the spinner
        spinnerDropdown.setAdapter(adapter);

        // when a new list is selected
        spinnerDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedList = dropdownItems.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        checkboxContainer = findViewById(R.id.checkboxContainer);
        addButton = findViewById(R.id.addButton);
        addListButton = findViewById(R.id.btnAddItem);
        removeListButton = findViewById(R.id.btnRemoveItem);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditTextDialog();
            }
        });

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });


        removeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveItemDialog();
            }
        });
    }

    // Method to dynamically add items to the list of lists
    private void addItemToDropdown(String newItem) {
        dropdownItems.add(newItem);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDropdown.getAdapter();
        adapter.notifyDataSetChanged();
    }

    // save entered data
    private void save_to_json()
    {

    }

    // remove list
    private void showRemoveItemDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove List")
                .setMessage("Select a list to remove:")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected item
                        EditText editText = dialogView.findViewById(R.id.editTextButtonName);
                        String buttonName = editText.getText().toString();
                        // Remove the selected item
                        dropdownItems.remove(buttonName); // TODO: verify that the user wants to delete it
                        // Update the adapter
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // get the button name from the user and add new list
    private void showAddItemDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("New List")
                .setPositiveButton("Add List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = dialogView.findViewById(R.id.editTextButtonName);
                        String buttonName = editText.getText().toString();
                        // add if it doesn't exist already
                        boolean equal = false;
                        for(String button : dropdownItems) {
                            if(buttonName.compareTo(button) == 0) {
                                equal = true;
                                break;
                            }
                        }
                        if(!equal) {
                            addItemToDropdown(buttonName);
                        } else {
                            // TODO: give an information popup that tells the user that the list exists
                        }
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
