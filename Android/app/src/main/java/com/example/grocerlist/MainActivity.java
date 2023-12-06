package com.example.grocerlist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// grocery list item
class GroceryItem
{
    public boolean checked; // checked off
    public String name;

    public GroceryItem(String name, boolean checked)
    {
        this.checked = checked;
        this.name = name;
    }
}

public class MainActivity extends AppCompatActivity {

    private LinearLayout checkboxContainer;

    private ArrayAdapter<String> adapter; // for the list of lists

    // list of lists

    private Spinner spinnerDropdown;
    private List<String> dropdownItems;

    private String currentList; // which list is currently selected

    // format: Map< list, grocery-items<grocery item> >
    private Map<String, List<GroceryItem>> groceryLists; // the list of lists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views by their respective IDs
        EditText searchBar = findViewById(R.id.searchBar);
        checkboxContainer = findViewById(R.id.checkboxContainer);
        // for list in the list
        Button addButton = findViewById(R.id.addButton);
        // list adder
        Button addListButton = findViewById(R.id.btnAddItem);
        // list remover
        Button removeListButton = findViewById(R.id.btnRemoveItem);


        // add list of lists
        spinnerDropdown = findViewById(R.id.spinner_dropdown);
        dropdownItems = new ArrayList<>();
        currentList = null; // no list selected
        groceryLists = new HashMap<>();

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
                currentList = dropdownItems.get(position);

                // remove all checkboxes from screen.
                checkboxContainer.removeAllViews();

                // put the values from the new selected list
                for(GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
                    addCheckbox(item.name, item.checked);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // add new item to list
        addButton.setOnClickListener(view -> {
            if(currentList != null) {
                openEditTextDialog();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Grocery Item")
                        .setMessage("Please create a list first")
                        .setPositiveButton("Ok", null);
                builder.create().show();
            }
        });

        addListButton.setOnClickListener(view -> showAddItemDialog());

        removeListButton.setOnClickListener(view -> showRemoveItemDialog());
    }

    // Method to dynamically add items to the list of lists
    private void addItemToDropdown(String newItem) {
        dropdownItems.add(newItem);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDropdown.getAdapter();
        adapter.notifyDataSetChanged();
    }

    // remove list
    private void showRemoveItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove List")
                .setMessage("Are you sure you want to remove list: '" + spinnerDropdown.getSelectedItem() + "'?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Get the selected item
                    String selectedList = (String) spinnerDropdown.getSelectedItem();

                    // Remove the selected item
                    dropdownItems.remove(selectedList);

                    // select new list value
                    if(!dropdownItems.isEmpty()) {
                        currentList = (String) spinnerDropdown.getSelectedItem();

                        // put the values from the new selected list
                        for (GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
                            addCheckbox(item.name, item.checked);
                        }
                        groceryLists.remove(selectedList); // remove old item
                    } else {
                        currentList = null;
                    }
                    checkboxContainer.removeAllViews();

                    // Update the adapter
                    adapter.notifyDataSetChanged();
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
                .setPositiveButton("Add List", (dialog, which) -> {
                    EditText editText = dialogView.findViewById(R.id.editTextButtonName);
                    String buttonName = editText.getText().toString().trim();
                    // add if it doesn't exist already
                    boolean equal = false;
                    for(String button : dropdownItems) {
                        if(buttonName.compareTo(button) == 0) {
                            equal = true;
                            break;
                        }
                    }
                    if(!equal) {
                        currentList = buttonName;
                        groceryLists.put(currentList, new ArrayList<>()); // create empty list

                        addItemToDropdown(buttonName);
                        spinnerDropdown.setSelection(dropdownItems.indexOf(buttonName));

                        checkboxContainer.removeAllViews(); // remove the current list items
                    } else {
                        // give an information popup that tells the user that the list exists
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle("Add List")
                                .setMessage("List Already Exists: '" + spinnerDropdown.getSelectedItem() + "'?")
                                .setPositiveButton("Ok", null);
                        builder1.create().show();
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
                        // make sure the list item isn't already there
                        boolean itemExists = false;
                        for(GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
                            if(newText.compareTo(item.name) == 0) { // if text is already in list
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Add grocery List Item")
                                        .setMessage("Value '" + newText + "' already exists in list")
                                        .setPositiveButton("Ok", null);
                                builder.create().show();
                                itemExists = true;
                                break;
                            }
                        }
                        if(!itemExists)
                            // Add a new checkbox with the entered text
                            addCheckbox(newText, false);
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

    private void addCheckbox(String text, boolean checkedOff) {
        CheckBox newCheckbox = new CheckBox(this);
        text = text.trim();
        newCheckbox.setText(text);
        newCheckbox.setChecked(checkedOff);
        if (checkedOff)
            newCheckbox.getPaint().setFlags(newCheckbox.getPaint().getFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Objects.requireNonNull(groceryLists.get(currentList)).add(new GroceryItem(text, false)); // add item to list

        // Set an OnCheckedChangeListener to handle checkbox state changes
        String finalText = text;
        newCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Apply or remove strikethrough based on the checkbox state

            // change value from list
            for (GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
                if (item.name.compareTo(finalText) == 0) {
                    item.checked = isChecked;
                }
            }

            if (isChecked) {
                buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            } else {
                buttonView.setPaintFlags(buttonView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });
        checkboxContainer.addView(newCheckbox);
    }
}
