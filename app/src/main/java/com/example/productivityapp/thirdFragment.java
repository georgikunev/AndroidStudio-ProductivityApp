package com.example.productivityapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.productivityapp.model.Course;
import com.example.productivityapp.model.Lesson;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class thirdFragment extends Fragment implements ListCourseAdapter.OnCourseClicked {

    public static final String TAG = "thirdFragment";

    RecyclerView coursesLV;
    ListCourseAdapter adapter;
    ListLessonAdapter lAdapter;
    ArrayList<Course> courseArrayList;
    ArrayList<Lesson> lessonArrayList;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseAuth fAuth;
    String fUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_third, container, false);
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
        adapter = new ListCourseAdapter(getActivity(), courseArrayList,this);
        lAdapter = new ListLessonAdapter(getActivity(), lessonArrayList);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser().getUid();

        // initializing our variable for firebase
        // fireStore and getting its instance.
        db = FirebaseFirestore.getInstance();
        coursesLV.setAdapter(adapter);
        loadDataInListview();

        return view;
    }

    private void loadDataInListview() {
        db.collection("Courses").whereEqualTo("createdBy", fAuth.getCurrentUser().getUid()).get()
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