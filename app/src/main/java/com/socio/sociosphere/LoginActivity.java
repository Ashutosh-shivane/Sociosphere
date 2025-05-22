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
import com.google.firebase.auth.FirebaseUser;
import com.socio.sociosphere.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(), "Password is 6 characters long", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if password is too short
                }


                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Invalid Credintial",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        // Fix missing closing parenthesis
        binding.goToSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        }); 
    }


// To avoid multiple times logging for same user

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser!=null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}
