package ru.mirea.allik.mireaproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.allik.mireaproject.databinding.ActivityFireBinding;
import ru.mirea.allik.mireaproject.ui.profile.profileFragment;

public class fireActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityFireBinding binding;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFireBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// [START initialize_auth] Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(binding.emailEdit.getText());
                String password = String.valueOf(binding.passwordEdit.getText());
                signIn(email, password, view);
            }
        });
        binding.createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(binding.emailEdit.getText());
                String password = String.valueOf(binding.passwordEdit.getText());
                createAccount(email, password, view);
            }
        });
// [END initialize_auth]
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
// Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
//        profileFragment prof = new profileFragment();
//        binding.emailEdit.setText(prof.getEmailLog());
//        binding.passwordEdit.setText(prof.getPasLog());
    }
    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {
//            binding.emailText.setText(getString(R.string.emailpassword_status_fmt,user.getEmail(), user.isEmailVerified()));
//            binding.firebaseIDText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//            binding.emailEdit.setVisibility(View.GONE);
//            binding.passwordEdit.setVisibility(View.GONE);
//            binding.signInBtn.setVisibility(View.GONE);
//            binding.createAccBtn.setVisibility(View.GONE);
//            binding.signOutBtn.setVisibility(View.VISIBLE);
//            binding.verifyBtn.setVisibility(View.VISIBLE);
//            binding.verifyBtn.setEnabled(!user.isEmailVerified());

        } else {
            binding.emailText.setText(R.string.signed_out);
            binding.firebaseIDText.setText(null);
            binding.emailEdit.setVisibility(View.VISIBLE);
            binding.passwordEdit.setVisibility(View.VISIBLE);
            binding.signInBtn.setVisibility(View.VISIBLE);
            binding.createAccBtn.setVisibility(View.VISIBLE);
            binding.signOutBtn.setVisibility(View.GONE);
            binding.verifyBtn.setVisibility(View.GONE);
        }
    }

    private void createAccount(String email, String password, View view) {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
// [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            loadMain(view);
                        } else {
// If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure",
                                    task.getException());
                            Toast.makeText(fireActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
// [END create_user_with_email]
    }


    private void signIn(String email, String password, View view) {
        Log.d(TAG, "signIn:" + email);
// [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            loadMain(view);
                        } else {
// If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(fireActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
// [START_EXCLUDE]

                        if (!task.isSuccessful()) {

                            binding.emailText.setText(R.string.auth_failed);
                        }

// [END_EXCLUDE]

                    }
                });
// [END sign_in_with_email]
    }

    public void loadMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}