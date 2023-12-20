/* Copyright (c) 2023 Taha Canturk, Ashwin Rajakrishna, Kohei Arizumi, Henry Lin
 * this program is free software: you can redistribute it and/or modify
 * it under the terms of the gnu general public license as published by
 * the free software foundation, either version 3 of the license, or
 * (at your option) any later version.
 * this program is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  see the
 * gnu general public license for more details.
 * you should have received a copy of the gnu general public license
 * along with this program.  if not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors: Taha, Ashwin
 * Date: 2023, Dec 20
 * Description: This is the android version of the GrocerList application for grocery list management. 
 * Version: 1.0
 */

package com.example.grocerlist;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// grocery list item
// Author: Taha
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

    // Author: Taha
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                for (GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
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
    // Author: Taha
    private void addItemToDropdown(String newItem) {
        dropdownItems.add(newItem);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDropdown.getAdapter();
        adapter.notifyDataSetChanged();
    }

    // remove list
    // Author: Taha
    private void showRemoveItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove List")
                .setMessage("Are you sure you want to remove list: '" + spinnerDropdown.getSelectedItem() + "'?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Get the selected item
                    int indexOfSelected = spinnerDropdown.getSelectedItemPosition();
                    String selectedList = (String) spinnerDropdown.getSelectedItem();

                    // Remove the selected item
                    dropdownItems.remove(indexOfSelected);

                    // Update the adapter
                    adapter.notifyDataSetChanged();
                    checkboxContainer.removeAllViews();

                    // select new list value
                    if(!dropdownItems.isEmpty()) {
                        groceryLists.remove(selectedList); // remove old item

                        int newIndex = Math.min(indexOfSelected, dropdownItems.size() - 1);
                        currentList = (String) dropdownItems.get(newIndex);
                        spinnerDropdown.setSelection(newIndex);

                        // put the values from the new selected list
                        for (GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
                            addCheckbox(item.name, item.checked);
                        }
                    } else {
                        currentList = null;
                    }

                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // get the button name from the user and add new list
    // Author: Taha
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
    // Author: Taha
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
                        if(!itemExists) {
                            // Add a new checkbox with the entered text
                            Objects.requireNonNull(groceryLists.get(currentList)).add(new GroceryItem(newText, false)); // add item to list
                            addCheckbox(newText, false);
                        }
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

    // Author: Taha
    private void addCheckbox(String text, boolean checkedOff) {
        CheckBox newCheckbox = new CheckBox(this);
        text = text.trim();
        newCheckbox.setText(text);
        newCheckbox.setChecked(checkedOff);
        final String[] textFinal = {text};

        if (checkedOff)
            newCheckbox.getPaint().setFlags(newCheckbox.getPaint().getFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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
                showRemoveItemConfirmationDialog(textFinal[0]);
            } else {
                buttonView.setPaintFlags(buttonView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });
        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);


        Button renameButton = new Button(this);
        renameButton.setText("\t✏\uFE0F");
        renameButton.setTextColor(Color.RED);
        renameButton.setTypeface(null, Typeface.BOLD);

        // Set an OnClickListener to handle button click
        renameButton.setOnClickListener(v -> renameCheckbox(textFinal));
        containerLayout.addView(renameButton);
        containerLayout.addView(newCheckbox);
        checkboxContainer.addView(containerLayout);
    }

    // Method to show a confirmation dialog before removing an item
    // Author: Ashwin
    private void showRemoveItemConfirmationDialog(String itemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Item")
                .setMessage("Are you sure you want to remove item: '" + itemName + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Call a method to remove the item when 'Yes' is clicked
                    removeItem(itemName);
                })
                .setNegativeButton("No", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button buttonYes = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonYes.setOnClickListener(view -> {
                // Here you can add any additional logic before calling removeItem
                // For example, you can prompt another dialog for confirmation

                // Call a method to remove the item when 'Yes' is clicked
                removeItem(itemName);

                // Dismiss the current dialog if needed
                alertDialog.dismiss();
            });
        });

        alertDialog.show();
    }

    // Method to remove an item
    // Author: Ash
    private void removeItem(String itemName) {
        // Remove the item from the list and update the UI
        Iterator<GroceryItem> iterator = Objects.requireNonNull(groceryLists.get(currentList)).iterator();
        while (iterator.hasNext()) {
            GroceryItem item = iterator.next();
            if (item.name.equals(itemName)) {
                iterator.remove();
                break;
            }
        }

        // Update the UI to reflect the changes
        checkboxContainer.removeAllViews();
        for (GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
            addCheckbox(item.name, item.checked);
        }
    }

    // Rename a checkbox
    // ask the user if they are sure they want to rename as checkbox.
    // Author: Taha
    private void renameCheckbox(String[] renamedItem) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edittext, null);
        EditText editText = dialogView.findViewById(R.id.editTextDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Rename")
                .setPositiveButton("Ok", (dialog, which) -> {
            String newText = editText.getText().toString().trim();
            for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
                View view = checkboxContainer.getChildAt(i);
                LinearLayout linearLayout = ((LinearLayout)view);
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerView = linearLayout.getChildAt(j);

                    if (innerView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) innerView;

                        if (checkBox.getText().toString().equals(renamedItem[0].trim())) {
                            checkBox.setText(newText.trim());
                            if (groceryLists.containsKey(currentList)) { // update the list
                                for (GroceryItem item : Objects.requireNonNull(groceryLists.get(currentList))) {
                                    if (item.name.equals(renamedItem[0])) {
                                        item.name = newText;
                                        renamedItem[0] = newText;
                                        break;  // Stop iterating once the item is found and updated
                                    }
                                }
                            }
                            break;  // Stop searching once the checkbox is found and updated
                        }
                    }
                }
            }
        })
        .setNegativeButton("Cancel", null);
        builder.create().show();
    }
}
