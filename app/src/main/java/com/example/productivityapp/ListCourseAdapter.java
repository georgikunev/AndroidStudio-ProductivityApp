package com.example.productivityapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.model.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


public class ListCourseAdapter extends RecyclerView.Adapter<ListCourseAdapter.MainViewHolder> {


    public static String TAG;
    Context context;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private OnCourseClicked monCourseClicked;
    ArrayList<Course> courseArrayList;

    public ListCourseAdapter(Context context, ArrayList<Course> courses, OnCourseClicked onCourseClicked) {
        this.context = context;
        this.monCourseClicked = onCourseClicked;
        this.courseArrayList = courses;
    }


    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(context).inflate(R.layout.list_theme_course, parent, false);

        fStore = FirebaseFirestore.getInstance();;
        fAuth = FirebaseAuth.getInstance();

        return new MainViewHolder(v, monCourseClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        Course course = courseArrayList.get(position);
        holder.courseTitle.setText(course.getCourseTitle());


    }

    @Override
    public int getItemCount() {
        return courseArrayList.size();
    }


    public static class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        private final TextView courseTitle;
        OnCourseClicked onCourseClicked;

        public MainViewHolder(@NonNull View itemView, OnCourseClicked onCourseClicked) {
            super(itemView);

            this.onCourseClicked = onCourseClicked;
            context = itemView.getContext();
            courseTitle = itemView.findViewById(R.id.item_text);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            onCourseClicked.onCourseClicked(getAdapterPosition());
        }
    }
    public interface OnCourseClicked{
        void onCourseClicked(int position);


    }

}
