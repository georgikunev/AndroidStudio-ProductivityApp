package com.example.productivityapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.productivityapp.StudentPanel.StudentHome;
import com.example.productivityapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    public static String TAG;
    public static String usrType;
    Button LogInButton;
    TextView RegisterButton, forgotPasswordBtn;
    EditText Email, Password;
    FirebaseUser firebaseUser;
    String firebaseUserUid;
    User user;
    ArrayList<User> userArrayList;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        LogInButton = findViewById(R.id.buttonLogin);

        RegisterButton = findViewById(R.id.buttonRegister);
        forgotPasswordBtn = findViewById(R.id.forgotPassword);

        Email = findViewById(R.id.editEmail);
        Password = findViewById(R.id.editPassword);


        progressBar = findViewById(R.id.progressBarLogin);

        userArrayList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        user = new User();
        userData();

        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Смяна на парола");
                passwordResetDialog.setMessage("Въведете имейл адреса си");
                passwordResetDialog.setPositiveButton("Изпрати", null);
                passwordResetDialog.setNegativeButton("Отказ", null);
                passwordResetDialog.setView(resetEmail);

                Button positiveBtn = passwordResetDialog.show().getButton(AlertDialog.BUTTON_POSITIVE);

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String mail = resetEmail.getText().toString().trim();

                        if (mail.isEmpty()) {
                            resetEmail.setError("Полето не може да е празно");
                            resetEmail.requestFocus();
                            return;
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            resetEmail.setError("Моля въведете валиден имейл");
                            resetEmail.requestFocus();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Toast.makeText(LoginActivity.this, "Линкът за смяна на парола е изпратен", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Възникна грешка при изпращането на линка", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                passwordResetDialog.create();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

    }

    private void userLogin() {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (email.isEmpty()) {
            Email.setError("Трябва да въведете имейл");
            Email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Въведете валиден имейл");
            Email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Password.setError("Трябва да въведете парола");
            Password.requestFocus();
            return;
        }

        if (password.length() < 4) {
            Password.setError("Паролата трябва да е по-голяма от 4 символа");
            Password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUserUid = firebaseUser.getUid();
                    if (firebaseUser.isEmailVerified()) {
                        userPanel(firebaseUserUid);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Проверете имейла си, за да потвърдите акаунта си", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Възникна грешка при влизането", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void userPanel(String uid) {

        database.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!Objects.equals(documentSnapshot.get("userType"), "Student")) {
                            startActivity(new Intent(LoginActivity.this, Home.class));
                        }
                        if (!Objects.equals(documentSnapshot.get("userType"), "Teacher")) {
                            startActivity(new Intent(LoginActivity.this, StudentHome.class));
                        }
                    }
                });

    }

    private void userData() {
        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                                user.setUsersId(document.getId());

                                usrType = user.getUserType();

                                userArrayList.add(user);


                                Log.d(TAG, "userIdModelVar:" + user.getUsersId());
                                Log.d(TAG, "userTypeModelVar:" + user.getUserType());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}