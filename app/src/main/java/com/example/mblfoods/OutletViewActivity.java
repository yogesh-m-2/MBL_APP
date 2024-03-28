package com.example.mblfoods;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutletViewActivity extends AppCompatActivity {

    private LinearLayout dynamicLayout;
    private List<String> selectedItems = new ArrayList<>(); // List to keep track of selected items
    private Map<String, EditText> quantityEditTextMap = new HashMap<>(); // Map to keep track of quantity EditTexts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outlet_view);

        // Retrieve outletName from the intent
        String outletName = getIntent().getStringExtra("outletName");

        // Initialize the Spinner
        Spinner spinnerDropdown = findViewById(R.id.spinner_dropdown);

        // Sample data for the spinner
        String[] items = {"","Chips 50gms", "Chips 100gms", "Chips Small", "Mixture","Kodbale","Bennemurku"};

        // Creating adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerDropdown.setAdapter(adapter);

        // Initialize dynamic layout
        dynamicLayout = findViewById(R.id.dynamic_layout);

        // Spinner item selected listener
        spinnerDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (!selectedItem.isEmpty() && !selectedItems.contains(selectedItem)) { // Check if the item is not empty and not already listed
                    selectedItems.add(selectedItem); // Add item to the list of selected items

                    // Create a horizontal LinearLayout to hold item label and quantity input
                    LinearLayout itemLayout = new LinearLayout(OutletViewActivity.this);
                    itemLayout.setOrientation(LinearLayout.HORIZONTAL);

                    // Create a TextView to display the selected item
                    TextView textView = new TextView(OutletViewActivity.this);
                    textView.setText(selectedItem);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.gravity = Gravity.START;
                    textView.setLayoutParams(params);

                    // Create EditText for quantity input
                    EditText quantityEditText = new EditText(OutletViewActivity.this);
                    quantityEditText.setHint("Quantity");
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params2.gravity = Gravity.END;
                    quantityEditText.setLayoutParams(params2);

                    // Add TextView and EditText to the horizontal layout
                    itemLayout.addView(textView);
                    itemLayout.addView(quantityEditText);

                    // Add horizontal layout to the dynamic layout
                    dynamicLayout.addView(itemLayout);

                    // Store the EditText in the map
                    quantityEditTextMap.put(selectedItem, quantityEditText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // GetInvoice button
        Button getInvoiceButton = findViewById(R.id.GetInvoice);
        getInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data
                List<String> itemNames = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();
                for (String itemName : selectedItems) {
                    EditText quantityEditText = quantityEditTextMap.get(itemName);
                    if (quantityEditText != null) {
                        String quantityStr = quantityEditText.getText().toString();
                        if (!quantityStr.isEmpty()) {
                            int quantity = Integer.parseInt(quantityStr);
                            itemNames.add(itemName);
                            quantities.add(quantity);
                        }
                    }
                }

                // Pass data to InvoiceActivity
                Intent intent;
                intent = new Intent(OutletViewActivity.this, InvoiceActivity.class);
                intent.putExtra("outletName", outletName);
                intent.putStringArrayListExtra("itemNames", (ArrayList<String>) itemNames);
                intent.putIntegerArrayListExtra("quantities", (ArrayList<Integer>) quantities);
                startActivity(intent);
            }
        });
    }
}
