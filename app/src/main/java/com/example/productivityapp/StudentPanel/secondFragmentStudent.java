package com.example.productivityapp.StudentPanel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.productivityapp.ListCourseAdapter;
import com.example.productivityapp.ListLessonAdapter;
import com.example.productivityapp.R;
import com.example.productivityapp.model.Course;
import com.example.productivityapp.model.Lesson;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class secondFragmentStudent extends Fragment implements ListCourseAdapter.OnCourseClicked {


    public static final String TAG = "secondFragmentStudent";

    RecyclerView coursesLV;
    ListCourseAdapter adapter;
    ListLessonAdapter lAdapter;
    ArrayList<Course> courseArrayList;
    ArrayList<Lesson> lessonArrayList;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    Lesson lesson;
    FirebaseAuth fAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second_student, container, false);
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
        adapter = new ListCourseAdapter(getActivity(), courseArrayList,this);
        lAdapter = new ListLessonAdapter(getActivity(), lessonArrayList);
        fAuth = FirebaseAuth.getInstance();

        // initializing our variable for firebase
        // fireStore and getting its instance.
        db = FirebaseFirestore.getInstance();
        coursesLV.setAdapter(adapter);
        loadDataInListview();

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

    @Override
    public void onCourseClicked(int position) {
        Course course = courseArrayList.get(position);
        String courseId = course.getCourseId();
        String courseTitle = course.getCourseTitle();
        ArrayList<String> lessonIds = course.getLessonIds();

        String fUser = fAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Записване за курс\n" + courseTitle)
                .setNegativeButton("Затвори", null)
                .setPositiveButton("Запиши се", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("enrolledUsers", FieldValue.arrayUnion(fUser));
                        db.collection("Courses")
                                .document(courseId)
                                .update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Курсът е добавен", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                });

        builder.create();
        builder.show();
    }
}
