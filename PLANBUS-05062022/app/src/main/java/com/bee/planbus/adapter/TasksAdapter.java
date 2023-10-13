package com.bee.planbus.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bee.planbus.R;
import com.bee.planbus.activities.TargetActivity;
import com.bee.planbus.fragments.AddNewTask;
import com.bee.planbus.fragments.TaskFragment;
import com.bee.planbus.model.ToDoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    private List<ToDoModel> todoList;
    private Fragment fragment;
    private FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public TasksAdapter(Fragment fragment, List<ToDoModel> todoList){
        this.todoList = todoList;
        this.fragment= fragment;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.each_task, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }

    //Hedef silmek icin:
    public void deleteTask(int position){
        ToDoModel toDoModel = todoList.get(position);
        firestore.collection("task").document(firebaseUser.getUid()).collection("myTasks").document(toDoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }


    public Context getContext(){
        return fragment.getContext();
    }

    //Hedef duzenlemek icin:
    public void editTask(int position){
        ToDoModel toDoModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task", toDoModel.getTask());
        bundle.putString("due", toDoModel.getDue());
        bundle.putString("id", toDoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(fragment.getActivity().getSupportFragmentManager(), addNewTask.getTag());
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDoModel toDoModel = todoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());
        holder.sonTarihTextView.setText("Hedef Tarihi: "+ toDoModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    firestore.collection("task").document(firebaseUser.getUid()).collection("myTasks").document(toDoModel.TaskId).update("status", 1);
                }else{
                    firestore.collection("task").document(firebaseUser.getUid()).collection("myTasks").document(toDoModel.TaskId).update("status", 0);
                }
            }
        });

    }


    private boolean toBoolean(int status){
        return status!=0;
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sonTarihTextView;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sonTarihTextView = itemView.findViewById(R.id.sonTarih);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);

        }
    }

}
