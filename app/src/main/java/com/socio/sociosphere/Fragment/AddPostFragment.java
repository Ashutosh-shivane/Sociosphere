package com.socio.sociosphere.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.socio.sociosphere.Model.Post;
import com.socio.sociosphere.R;
import com.socio.sociosphere.User;
import com.socio.sociosphere.databinding.FragmentAddPostBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddPostFragment extends Fragment {

    FragmentAddPostBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressBar progressBar; // Add ProgressBar

    // New variable to track image selection
    private boolean isImageSelected = false;

    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance(); // Initialize storage
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false);

        // Initialize ProgressBar
        progressBar = binding.progressBar; // Assuming you have the ProgressBar in your binding

        // Fetch user data
        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                Picasso.get()
                                        .load(user.getProfilePhoto())
                                        .placeholder(R.drawable.profile).into(binding.profileImage);

                                binding.name.setText(user.getName());
                                binding.profession.setText(user.getProfession());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });

        // Initialize ActivityResultLauncher for picking an image
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        binding.postImage.setVisibility(View.VISIBLE);
                        binding.postImage.setImageURI(imageUri);
                        uri = imageUri; // Store the URI for uploading
                        isImageSelected = true; // Mark image as selected
                        updatePostButtonState(); // Update button state






                        try {
                            // Convert the image to InputImage for ML Kit
                            InputImage image = InputImage.fromFilePath(requireContext(), result.getData().getData());

                            // Get an instance of ImageLabeler with default options
                            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

                            // Process the image
                            labeler.process(image)
                                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                        @Override
                                        public void onSuccess(List<ImageLabel> labels) {



                                            String textdata="I have the following keywords with their respective confidence values:\n";



                                            // Clear previous results
                                            for (ImageLabel label : labels) {
                                                String text = label.getText();
                                                float confidence = label.getConfidence();


                                                textdata+=text+ " with confidence "+confidence+"\n" ;
                                            }
                                            textdata+="Generate relevant Instagram hashtags based on these keywords, considering the confidence values to ensure the hashtags align closely with the topics.\n";


                                            callgeminiapiforimage(textdata);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }





                    }
                }
        );

        // TextWatcher for enabling/disabling the post button
        binding.postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePostButtonState(); // Update button state
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Image Picker logic
        binding.addImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        binding.postBtn.setOnClickListener(v -> {
            if (uri != null) {
                progressBar.setVisibility(View.VISIBLE); // Show ProgressBar
                final StorageReference reference = storage.getReference().child("posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() + "");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                if (downloadUri != null) { // Check if downloadUri is not null
                                    Post post = new Post();
                                    post.setPostImage(downloadUri.toString());
                                    post.setPostDescription(binding.postDescription.getText().toString());
                                    post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                    post.setPostedAt(new Date().getTime());

                                    database.getReference().child("posts")
                                            .push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressBar.setVisibility(View.GONE); // Hide ProgressBar
                                                    Toast.makeText(getContext(), "Post Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
            }
        });

        return binding.getRoot();
    }

    // New method to update the post button state
    private void updatePostButtonState() {
        String des = binding.postDescription.getText().toString();
        boolean isPostButtonEnabled = !des.isEmpty() || isImageSelected; // Enable if description is not empty or image is selected
        if (isPostButtonEnabled) {
            binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn_bg));
            binding.postBtn.setTextColor(getContext().getColor(R.color.white));
            binding.postBtn.setEnabled(true);
        } else {
            binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_active_btn));
            binding.postBtn.setTextColor(getContext().getColor(R.color.grey));
            binding.postBtn.setEnabled(false);
        }
    }


    public void callgeminiapiforimage(String searchquery) {
        // Retrieve the user input from EditText
//        String userInput = result.getText().toString().trim();
        String userInput = searchquery;

        System.out.println(userInput);

        userInput+="give me five hashtag only and hashtag contain no num or other text and with hash between them";

        // Check if the input is empty
        if (userInput.isEmpty()) {

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
//                        runOnUiThread(() -> {
//                            binding.postDescription.setText(resultText);
//                            System.out.println(resultText);
//                        });

                        requireActivity().runOnUiThread(() -> binding.postDescription.setText(resultText));

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Update UI on the main thread
//                        runOnUiThread(() -> binding.postDescription.setText(t.toString()));
                        requireActivity().runOnUiThread(() -> binding.postDescription.setText(t.toString()));

                    }
                },
                executor
        );
    }


}
