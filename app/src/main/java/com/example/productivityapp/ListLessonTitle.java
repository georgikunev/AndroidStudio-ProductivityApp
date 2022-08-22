package com.example.productivityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.productivityapp.model.Course;
import com.example.productivityapp.model.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ListLessonTitle extends Activity{

    public static String TAG;
    public static String courseIdReceiver;
    public static String courseNameReceiver;
    public static ArrayList<String> lessonIdsReceiver;
    private ListLessonAdapter mAdapter;
    private ArrayList<Lesson> mListLessons;
    FloatingActionButton mBtnAddLesson;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    Course course;
    String fUser;
    TextView courseNameDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_lesson_title);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        lessonIdsReceiver = getIntent().getStringArrayListExtra("lessonIds");
        courseIdReceiver = getIntent().getExtras().get("courseId").toString();
        courseNameReceiver = getIntent().getExtras().get("courseTitle").toString();
        mBtnAddLesson = findViewById(R.id.addButtonLesson);
        courseNameDisplay = findViewById(R.id.textViewCourseName);
        courseNameDisplay.setText(courseNameReceiver);
        RecyclerView mListviewLessons = findViewById(R.id.list_lessons);
        mListviewLessons.setHasFixedSize(true);
        mListviewLessons.setLayoutManager(new LinearLayoutManager(this));
        mListLessons = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAdapter = new ListLessonAdapter(ListLessonTitle.this, mListLessons);
        mListviewLessons.setAdapter(mAdapter);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser().getUid();
        course = new Course();
        Log.d(TAG, "lessonIdsReceived:" + lessonIdsReceiver);

        loadDataInListview();

        mBtnAddLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListLessonTitle.this, AddLessonsActivity.class);
                startActivity(intent);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefreshLessonT);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void loadDataInListview() {
        for (String lessonId : lessonIdsReceiver) {
            db.collection("Lessons").document(lessonId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Lesson l = document.toObject(Lesson.class);
                                    l.setLessonId(document.getId());

                                    mListLessons.add(l);

                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d(TAG, "No such document");
                                }

                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ListLessonTitle.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ListLessonTitle.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
