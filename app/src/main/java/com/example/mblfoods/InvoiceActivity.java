package com.example.mblfoods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity implements PostRequestAsyncTask.OnPostRequestListener {
    String url = "https://script.google.com/macros/s/AKfycbxEqxlidBmAHC4R_fdlSDZPY1OM7-q5coEMN3mjYEiyCyWo4954m1r32BpF1TozrfzL/exec";

    private static final int REQUEST_CODE_GET_SIGNATURE = 101;
    private String signatureBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Retrieve data from OutletViewActivity
        String outletName = getIntent().getStringExtra("outletName");
        List<String> itemNames = getIntent().getStringArrayListExtra("itemNames");
        List<Integer> quantities = getIntent().getIntegerArrayListExtra("quantities");

        // Display the outlet name
        TextView outletNameTextView = findViewById(R.id.outletNameTextView);
        outletNameTextView.setText("Outlet Name: " + outletName);

        // Add items to the table layout
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        for (int i = 0; i < itemNames.size(); i++) {
            TableRow row = new TableRow(this);
            row.setBackground(getResources().getDrawable(R.drawable.table_row_border)); // Set transparent border

            TextView itemNameTextView = new TextView(this);
            itemNameTextView.setText(itemNames.get(i));
            row.addView(itemNameTextView);

            TextView quantityTextView = new TextView(this);
            quantityTextView.setText(String.valueOf(quantities.get(i)));
            row.addView(quantityTextView);

            tableLayout.addView(row);
        }

        // Get Signature Button
        Button getSignatureButton = findViewById(R.id.getSignatureButton);
        getSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceActivity.this, SignatureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_GET_SIGNATURE);
            }
        });

        // Submit Button
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if signatureBase64 is not null
                if (signatureBase64 != null && !signatureBase64.isEmpty()) {
                    Map<String, String> postData = new HashMap<>();
                    JSONObject obj = new JSONObject();
                    try {
                        // Add your invoice data
                        obj.put("outletname", outletName);
                        obj.put("itemNames", itemNames.toString());
                        obj.put("quantities", quantities.toString());
                        // Add the signatureBase64
                        obj.put("signature", signatureBase64);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    postData.put("data", obj.toString());

                    // Send the POST request
                    new PostRequestAsyncTask(InvoiceActivity.this, postData).execute(url);
                    Toast.makeText(getApplicationContext(), "Submitting Invoice...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InvoiceActivity.this, SecondActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide a signature", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_SIGNATURE && resultCode == RESULT_OK && data != null) {
            byte[] byteArray = data.getByteArrayExtra("signatureBitmap");
            if (byteArray != null) {
                Bitmap signatureBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                if (signatureBitmap != null) {
                    // Convert bitmap to base64
                    signatureBase64 = bitmapToBase64(signatureBitmap);
                }
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }

    @Override
    public void onPostRequestCompleted(String result) {

    }
}
