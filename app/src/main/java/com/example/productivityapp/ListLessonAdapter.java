package com.example.productivityapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.model.Lesson;

import java.util.ArrayList;


public class ListLessonAdapter extends RecyclerView.Adapter<ListLessonAdapter.MyViewHolder> {

    public static String TAG;

    Context context;
    ArrayList<Lesson> lessonArrayList;

    public ListLessonAdapter(Context context, ArrayList<Lesson> lessons) {
        this.context = context;
        this.lessonArrayList = lessons;
    }

    @NonNull
    @Override
    public ListLessonAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_theme, parent, false);

        return new ListLessonAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListLessonAdapter.MyViewHolder holder, int position) {

        Lesson lesson = lessonArrayList.get(position);
        holder.lessonTitle.setText(lesson.getLessonTitle());
        holder.pdfName.setText(lesson.getPdfName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(lesson.getPdfUrl()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView lessonTitle;
        private final TextView pdfName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            lessonTitle = itemView.findViewById(R.id.item_text);
            pdfName = itemView.findViewById(R.id.item_textPDF);
        }
    }
}
