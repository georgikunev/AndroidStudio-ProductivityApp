package com.example.productivityapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.productivityapp.model.Course;
import com.example.productivityapp.model.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class secondFragment extends Fragment implements ListCourseAdapter.OnCourseClicked {


    public static final String TAG = "secondFragment";

    RecyclerView coursesLV;
    ListCourseAdapter adapter;
    ListLessonAdapter lAdapter;
    ArrayList<Course> courseArrayList;
    ArrayList<Lesson> lessonArrayList;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    Lesson lesson;
    EditText edittext;
    FirebaseAuth fAuth;
    String fUser;
    Course course;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        //progressDialog.show();

        coursesLV = view.findViewById(R.id.list_courses);
        coursesLV.setHasFixedSize(true);
        coursesLV.setLayoutManager(new LinearLayoutManager(getContext()));
        courseArrayList = new ArrayList<Course>();
        lessonArrayList = new ArrayList<Lesson>();
        lesson = new Lesson();
        adapter = new ListCourseAdapter(getActivity(), courseArrayList, this);
        lAdapter = new ListLessonAdapter(getActivity(), lessonArrayList);
        course = new Course();

        // initializing our variable for firebase
        // fireStore and getting its instance.
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        coursesLV.setAdapter(adapter);
        loadDataInListview();


        FloatingActionButton mBtnAddCourse = (FloatingActionButton) view.findViewById(R.id.addButton);
        mBtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.addButton) {
                    openDialog();
                }
            }
        });

        return view;
    }

    private void loadDataInListview() {
        db.collection("Courses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Course c = d.toObject(Course.class);
                                c.setCourseId(d.getId());

                                Log.d(TAG, "createdBy:" + c.getCreatedBy());

                                courseArrayList.add(c);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void openDialog() {
        edittext = new EditText(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Създаване на курс")
                .setMessage("Въведете име на курса")
                .setView(edittext)
                .setNegativeButton("Затвори", null)
                .setPositiveButton("Добави", null);
        Button positiveBtn = builder.show().getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseTitle = edittext.getText().toString().trim();
                Map<String, Object> items = new HashMap<>();
                items.put("courseTitle", courseTitle);
                items.put("lessonIds", Arrays.asList());
                items.put("enrolledUsers", Arrays.asList());
                items.put("createdBy", fUser);
                db.collection("Courses").add(items)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful() && !TextUtils.isEmpty(courseTitle)) {
                                    edittext.setText("");
                                    Toast.makeText(getActivity(), "Курсът е добавен", Toast.LENGTH_LONG).show();
                                } else {
                                    edittext.setError("Полето не може да е празно");
                                    edittext.requestFocus();
                                    Toast.makeText(getActivity(), "Курсът не беше добавен", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        builder.create();
    }

    @Override
    public void onCourseClicked(int position) {
        Course course = courseArrayList.get(position);
        String courseId = course.getCourseId();
        String courseTitle = course.getCourseTitle();
        ArrayList<String> lessonIds = course.getLessonIds();

        db.collection("users")
                .document(fUser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //if(!Objects.equals(documentSnapshot.get("userType"),"Student")){
                            Intent intent = new Intent(getActivity(), ListLessonTitle.class);
                            intent.putExtra("courseId", courseId);
                            intent.putExtra("courseTitle", courseTitle);
                            intent.putStringArrayListExtra("lessonIds", lessonIds);
                            startActivity(intent);

                    }
                });
    }
}
