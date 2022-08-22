package com.example.productivityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText Email, Password, Name;
    Button Register;
    TextView backLoginBtn;
    String NameHolder, EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    String F_Result = "Not_Found";

    String userID;

    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Register = (Button) findViewById(R.id.buttonRegister);

        Email = (EditText) findViewById(R.id.editEmail);
        Password = (EditText) findViewById(R.id.editPassword);
        Name = (EditText) findViewById(R.id.editName);
        backLoginBtn = (TextView) findViewById(R.id.backToLoginBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUsername() && validateEmail() && validatePassword()) {
                    CheckEditTextStatus();
                    EmptyEditTextAfterDataInsert();
                    InsertDataIntoDatabase();
                }
            }
        });

        backLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private Boolean validateUsername() {
        String val = Name.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            Name.setError("Полето не може да е празно");
            return false;
        } else if (val.length() >= 15) {
            Name.setError("Потребителското име е твърде дълго");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            Name.setError("Не са позволени интервали");
            return false;
        } else {
            Name.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = Password.getText().toString();

        String passwordVal =
                //"(?=.*[0-9])" +          //at least 1 digit
                // "(?=.*[a-z])" +         //at least 1 lower case letter
                // "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +         //any letter
                        "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +            //no white spaces
                        ".{4,}" +                //at least 4 characters
                        "$";

        if (val.isEmpty()) {
            Password.setError("Полето не може да е празно");
            return false;
        } else if (!val.matches(passwordVal)) {
            Password.setError("Паролата е твърде слаба");
            return false;
        } else {
            Password.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = Email.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            Email.setError("Полето не може да е празно");
            return false;
        } else if (!val.matches(emailPattern)) {
            Email.setError("Невалиден имейл адрес");
            return false;
        } else {
            Email.setError(null);
            return true;
        }
    }

    public void InsertDataIntoDatabase() {

        if (EditTextEmptyHolder) {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(EmailHolder, PasswordHolder)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Потребителят е създаден успешно", Toast.LENGTH_SHORT).show();
                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String,Object> user = new HashMap<>();
                                user.put("userName",NameHolder);
                                user.put("email", EmailHolder);
                                user.put("userType", "Student");
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Profile created for: " + userID);

                                    }
                                });
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else {
                                try {
                                    throw task.getException();
                                } catch(Exception e) {
                                    // email already in use
                                    Toast.makeText(getApplicationContext(), "Потребител с този имейл вече съществува", Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(RegisterActivity.this, "Възникна грешка при регистрацията", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

        } else {
            Toast.makeText(RegisterActivity.this, "Моля попълнете всички полета", Toast.LENGTH_LONG).show();
        }
    }

    public void EmptyEditTextAfterDataInsert() {

        Name.getText().clear();
        Email.getText().clear();
        Password.getText().clear();

    }

    public void CheckEditTextStatus() {

        NameHolder = Name.getText().toString().trim();
        EmailHolder = Email.getText().toString().trim();
        PasswordHolder = Password.getText().toString().trim();
        EditTextEmptyHolder = !TextUtils.isEmpty(NameHolder) && !TextUtils.isEmpty(EmailHolder) && !TextUtils.isEmpty(PasswordHolder);
    }

}