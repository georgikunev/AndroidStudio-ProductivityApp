package com.example.productivityapp.StudentPanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.ListCourseAdapter;
import com.example.productivityapp.ListLessonAdapter;
import com.example.productivityapp.R;
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


public class thirdFragmentStudent extends Fragment implements ListCourseAdapter.OnCourseClicked {

    public static final String TAG = "thirdFragmentStudent";
    public static ArrayList<String> enrolledUsersReceiver;
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
        View view = inflater.inflate(R.layout.fragment_third_student, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        enrolledUsersReceiver = getActivity().getIntent().getStringArrayListExtra("enrolledUsers");
        //progressDialog.show();
        coursesLV = view.findViewById(R.id.list_coursesEnrolled);
        coursesLV.setHasFixedSize(true);
        coursesLV.setLayoutManager(new LinearLayoutManager(getContext()));
        courseArrayList = new ArrayList<Course>();
        lessonArrayList = new ArrayList<Lesson>();
        adapter = new ListCourseAdapter(getActivity(), courseArrayList, this);
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

        db.collection("Courses").whereArrayContains("enrolledUsers", fUser).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Course c = d.toObject(Course.class);
                                c.setCourseId(d.getId());

                                Log.d(TAG, "enrolledUsers:" + c.getEnrolledUsers());

                                courseArrayList.add(c);
                            }


                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Няма добавени курсове", Toast.LENGTH_SHORT).show();
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
                        //if(Objects.equals(documentSnapshot.get("userType"),"Student")){
                        Intent intent = new Intent(getActivity(), ListLessonTitleStudent.class);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("courseTitle", courseTitle);
                        intent.putStringArrayListExtra("lessonIds", lessonIds);
                        startActivity(intent);

                    }
                });
    }
}