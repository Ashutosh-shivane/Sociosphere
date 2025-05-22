package com.socio.sociosphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.socio.sociosphere.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding before accessing any UI elements
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailET.getText().toString();
                String password = binding.passwordET.getText().toString();


                if (email.isEmpty()) {
//                    binding.emailET.setError("Email cannot be empty");
                    Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if email is invalid
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    binding.emailET.setError("Invalid email format");
                    Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if email format is incorrect
                }

// Validation for password
                if (password.isEmpty()) {
//                    binding.passwordET.setError("Password cannot be empty");
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if password is empty
                } else if (password.length() < 6) {
//                    binding.passwordET.setError("Password must be at least 6 characters long");
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if password is too short
                }

                String name = binding.nameET.getText().toString();
                String profession = binding.professionET.getText().toString();

                if (name.isEmpty()) {
//                    binding.nameET.setError("Name cannot be empty");
                    Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if name is empty
                }

// Validation for profession
                if (profession.isEmpty()) {
//                    binding.professionET.setError("Profession cannot be empty");
                    Toast.makeText(getApplicationContext(), "Profession cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if profession is empty
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Handle success or failure
                                if (task.isSuccessful()) {
                                    // Registration successful
                                    User user = new User(binding.nameET.getText().toString(), binding.professionET.getText().toString(),email,password);
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);

                                    Toast.makeText(SignUpActivity.this, "User data saved!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);

                                }
                                else{
                                    Toast.makeText(SignUpActivity.this, "Invalid Input Or User already exist" , Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
