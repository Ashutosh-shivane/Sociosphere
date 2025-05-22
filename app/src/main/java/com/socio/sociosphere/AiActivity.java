package com.socio.sociosphere;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AiActivity extends AppCompatActivity {

    Button b1,copy;
    TextView t1;
    EditText et1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        t1 = findViewById(R.id.textview1);
        b1 = findViewById(R.id.button);
        et1 = findViewById(R.id.editTextText);
        copy=findViewById(R.id.button3);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the TextView
                String textToCopy = t1.getText().toString();

                // Get the ClipboardManager
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object with the text to copy
                ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);

                // Set the ClipData to the clipboard
                clipboard.setPrimaryClip(clip);

                // Optionally, show a Toast message to inform the user
                Toast.makeText(AiActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();

            }
        });


        // Set up the button click listener
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callgeminiapi();
            }
        });
    }

    public void callgeminiapi() {
        // Retrieve the user input from EditText
        String userInput = et1.getText().toString().trim();

        // Check if the input is empty
        if (userInput.isEmpty()) {
            t1.setText("Please enter some text.");
            return; // Exit the method if the input is empty
        }

        // Specify a Gemini model appropriate for your use case
        GenerativeModel gm = new GenerativeModel(
                /* modelName */ "gemini-1.5-flash",
                /* apiKey */ "AIzaSyBVKinAXiDeN7GnAdrjvtvk-HAFfIZnYtQ"
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Use the user input as the content for generation
        Content content = new Content.Builder()
                .addText(userInput)
                .build();

        // Executor for running the API call in a separate thread
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            t1.setText(resultText);
                            System.out.println(resultText);
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Update UI on the main thread
                        runOnUiThread(() -> t1.setText(t.toString()));
                    }
                },
                executor
        );
    }

}