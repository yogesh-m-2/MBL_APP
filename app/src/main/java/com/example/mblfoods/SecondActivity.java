package com.example.mblfoods;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SecondActivity extends AppCompatActivity implements PostRequestAsyncTask.OnPostRequestListener {
    String url = "https://script.google.com/macros/s/AKfycbybg9O5WOHEK3jrTd4XGCkoj2yLfeQaFOgWnRrPnZQj1AnW0US3gxoCELtAkyby8wdT/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // Retrieve the idToken parameter from the Intent
        String idToken = getIntent().getStringExtra("idToken");
        loadalloutlets();

        Button addOutletButton = findViewById(R.id.addOutletButton);
        addOutletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a dialog to get the outlet name from the user
                showOutletNameDialog();
            }
        });

        // Initialize search functionality
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchForOutlet(newText);
                return true;
            }
        });
    }

    private void searchForOutlet(String query) {
        ConstraintLayout layout = findViewById(R.id.second_activity_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                String buttonText = button.getText().toString();
                if (buttonText.toLowerCase().contains(query.toLowerCase())) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.GONE);
                }
            }
        }
    }

    private void loadalloutlets() {
        Map<String, String> postData = new HashMap<>();
        JSONObject obj = new JSONObject();
        try {
            obj.put("outletaction", "getoutlets");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        postData.put("data", obj.toString());

        // Send the POST request
        new PostRequestAsyncTask(this, postData).execute(url);
    }

    private void showOutletNameDialog() {
        // Create a dialog to prompt the user to enter the name for the outlet
        // For simplicity, here's a basic example using an EditText within an AlertDialog
        final EditText outletNameEditText = new EditText(this);

        // Create the AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Enter Outlet Name")
                .setView(outletNameEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK button, retrieve the outlet name
                        String outletName = outletNameEditText.getText().toString();

                        // Trigger POST request with the outlet name
                        try {
                            sendOutletName(outletName);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendOutletName(String outletName) throws JSONException {
        // Here, you would trigger the POST request with the outlet name

        Map<String, String> postData = new HashMap<>();
        JSONObject obj = new JSONObject();
        obj.put("outletname", outletName);
        postData.put("data", String.valueOf(obj));
        AsyncTask<String, Void, String> res = new PostRequestAsyncTask(this, postData).execute(url);
        Log.d("OutletName", "Outlet Name: " + outletName);
        loadalloutlets();
    }

    @Override
    public void onPostRequestCompleted(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("outlets")) {
                JSONArray outletsArray = jsonObject.getJSONArray("outlets");

                CreateOutletButtons(outletsArray);
            } else {
                Toast.makeText(this, "No outlets found in the response", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing response"+result, Toast.LENGTH_SHORT).show();
        }
    }

    private void CreateOutletButtons(JSONArray outletsArray) {
        ConstraintLayout layout = findViewById(R.id.second_activity_layout);

        // Initialize the constraint to be connected to the searchView or addOutletButton if there are any
        int baselineId = R.id.searchView;

        for (int i = 0; i < outletsArray.length(); i++) {
            try {
                final String outletName = outletsArray.getString(i);
                Log.d("OutletButton", "Creating button for outlet: " + outletName); // Debug information

                Button button = new Button(this);
                button.setText(outletName);
                button.setGravity(Gravity.START | Gravity.CENTER_VERTICAL); // Align text to left
                button.setPadding(16, 0, 16, 0); // Add padding to text
                button.setTextColor(Color.BLACK); // Optional: Set text color

                // Set button ID dynamically to differentiate between buttons
                button.setId(View.generateViewId());

                // Create LayoutParams for the button
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT, // Set width to match parent
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );

                // Set constraints for the button
                layoutParams.topToBottom = baselineId;
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.setMargins(16, 16, 16, 0); // Adjust margins as needed

                // Apply LayoutParams to the button
                button.setLayoutParams(layoutParams);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openOutletView(outletName);
                    }
                });

                // Add button to the layout
                layout.addView(button);

                // Update the baselineId for the next button
                baselineId = button.getId();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void openOutletView(String outletName) {
        Intent intent = new Intent(this, OutletViewActivity.class);
        intent.putExtra("outletName", outletName);
        startActivity(intent);
    }
}
