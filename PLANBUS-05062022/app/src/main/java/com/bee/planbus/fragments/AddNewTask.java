package com.bee.planbus.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bee.planbus.R;
import com.bee.planbus.adapter.TasksAdapter;
import com.bee.planbus.interfaces.OnDialogCloseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private TextView sonTarih2;
    private EditText taskEditText;
    private Button saveTarihButton;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private Context context;
    private String sonTarihText = "";
    private String id = "";
    private String sontarihUpdate = "";
    private ImageButton micButton;
    private static final int RECOGNIZER_CODE = 1;


    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sonTarih2 = view.findViewById(R.id.sonTarih2);
        taskEditText = view.findViewById(R.id.taskEditText);
        saveTarihButton = view.findViewById(R.id.saveTarihButton);
        micButton = view.findViewById(R.id.micButton);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Konuş");
                startActivityForResult(intent, RECOGNIZER_CODE);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            sontarihUpdate = bundle.getString("due");

            taskEditText.setText(task);
            sonTarih2.setText(sontarihUpdate);

            if(task.length() > 0){
                saveTarihButton.setEnabled(false);
                saveTarihButton.setBackgroundColor(Color.GRAY);

            }

        }

        taskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    saveTarihButton.setEnabled(false);
                    saveTarihButton.setBackgroundColor(Color.GRAY);
                }else{
                    saveTarihButton.setEnabled(true);
                    saveTarihButton.setBackgroundColor(getResources().getColor(R.color.colorBlueMain));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        sonTarih2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        sonTarih2.setText(dayOfMonth + "/" + month + "/" + year);
                        sonTarihText = dayOfMonth + "/" + month + "/" + year;
                    }
                }, YEAR, MONTH, DAY);

                datePickerDialog.show();

            }
        });

        boolean finalIsUpdate = isUpdate;
        saveTarihButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskEditText.getText().toString();

                if(finalIsUpdate){
                    firestore.collection("task").document(firebaseUser.getUid()).collection("myTasks").document(id).update("task", task, "due", sonTarihText);
                    Toast.makeText(context, "Hedef Güncellendi", Toast.LENGTH_LONG).show();
                }
                else {

                    if (task.isEmpty()) {
                        Toast.makeText(context, "Boş olamaz", Toast.LENGTH_LONG).show();

                    } else {
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("task", task);
                        taskMap.put("due", sonTarihText);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp());

                        firestore.collection("task").document(firebaseUser.getUid()).collection("myTasks").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Hedef Kaydedildi", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                dismiss();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RECOGNIZER_CODE && resultCode == RESULT_OK){
            ArrayList<String> taskText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            taskEditText.setText(taskText.get(0).toString());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.context = context;
        }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}
