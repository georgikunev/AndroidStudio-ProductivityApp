package com.example.productivityapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.productivityapp.model.Lesson;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddLessonsActivity extends Activity {

    public static final String TAG = "AddLessonsActivity";
    ListLessonAdapter adapter;
    ArrayList<Lesson> lessonArrayList;
    Lesson lesson;
    EditText selectPdf;
    Button uplBtn;
    Uri FileUri;
    StorageReference storageReference;
    FirebaseFirestore database;
    private EditText mTxtLessonTitle;
    private EditText mTxtLessonPdfName;
    private String receiveCourseId;
    //private String receiveLessonId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lessons);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        selectPdf = findViewById(R.id.choosePdf);
        uplBtn = findViewById(R.id.btnUplPdf);

        receiveCourseId = ListLessonTitle.courseIdReceiver;

        database = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        lesson = new Lesson();
        adapter = new ListLessonAdapter(this, lessonArrayList);

        uplBtn.setEnabled(false);
        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPDF();
            }
        });
        initViews();
    }

    private void pickPDF() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "FILE SELECT"), 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FileUri = data.getData();

            uplBtn.setEnabled(true);
            selectPdf.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") + 1));
            uplBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDFFileFirebase();
                }
            });
        }
    }

    private void uploadPDFFileFirebase() {

        Editable lessonTitle = mTxtLessonTitle.getText();
        Editable pdfName = mTxtLessonPdfName.getText();

        if (!TextUtils.isEmpty(lessonTitle) && !TextUtils.isEmpty(pdfName)) {
            final ProgressDialog progressDialog = new ProgressDialog(AddLessonsActivity.this);
            progressDialog.setTitle("File is loading...");
            progressDialog.show();


            StorageReference reference = storageReference.child("upload" + FileUri.getLastPathSegment());
            reference.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            DocumentReference ref = database.collection("Lessons").document();
                            String mid = ref.getId();

                            Map<String, String> hashMap = new HashMap<>();
                            hashMap.put("lessonTitle", mTxtLessonTitle.getText().toString().trim());
                            hashMap.put("pdfName", mTxtLessonPdfName.getText().toString().trim());
                            hashMap.put("pdfUrl", String.valueOf(uri));

                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            CollectionReference usersRef = rootRef.collection("Lessons");
                            usersRef.document(mid).set(hashMap);

                            //Log.d(TAG, "courseId: " + course.getCourseId());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("lessonIds", FieldValue.arrayUnion(mid));
                            database.collection("Courses")
                                    .document(receiveCourseId)
                                    .update(updates);


                            Toast.makeText(AddLessonsActivity.this, "Успешно добавихте лекция", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddLessonsActivity.this, "Възникна грешка при добавянето на лекиция" + e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("File Upload" + (int) progress + "%");

                }
            });
        } else {
            mTxtLessonTitle.setError("Полето не може да е празно");
            mTxtLessonTitle.requestFocus();
            //Toast.makeText(this, "Попълнете всички полета", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        this.mTxtLessonTitle = (EditText) findViewById(R.id.txt_lesson_title);
        this.mTxtLessonPdfName = (EditText) findViewById(R.id.choosePdf);
    }
}